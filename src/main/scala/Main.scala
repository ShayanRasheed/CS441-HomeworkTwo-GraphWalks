package com.lsc

import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.graphx._

import com.lsc.GraphLoader.loadGraph
import com.lsc.RandomWalk.randomWalk

import NetGraphAlgebraDefs.{Action, NodeObject}

import com.typesafe.config.ConfigFactory
import org.slf4j.LoggerFactory

object Main {
  val config = ConfigFactory.load()
  private val logger = LoggerFactory.getLogger(getClass)

  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("GraphWalk").setMaster("local[8]")
    val sc = new SparkContext(conf)

    val perturbedGraph = loadGraph(config.getString("App.perturbedFilePath"), sc)
    val originalGraph = loadGraph(config.getString("App.originalFilePath"), sc)

    perturbedGraph match {
      case Some(perturbedGraph) =>
        logger.info("Beginning random walks on graph:")

        originalGraph match {
          case Some(originalGraph) =>
//            logger.info("printing valuable data")
//            originalGraph.vertices.foreach {case (_, nodeObject) => println(nodeObject)}

            val valuableNodes = originalGraph.vertices.filter {
              case (_, nodeObject) => nodeObject.valuableData }
              .map { case (vertexId, _) => vertexId }
              .collect()

            val numValNodes = valuableNodes.size
            logger.info(s"Found $numValNodes valuable nodes")
            //valuableNodes.foreach(x => println(x))

            val startNode = perturbedGraph.vertices.takeSample(withReplacement = false, 1)(0)._1
            val result = randomWalk(perturbedGraph, originalGraph, startNode, valuableNodes, List.empty)

            logger.info(s"THE RESULT IS: $result")

          case None =>
            logger.warn("MAIN: Graph Failed to load")
        }
      case None =>
        logger.warn("MAIN: Graph Failed to load")
    }

    sc.stop()
  }
}
