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
    val conf = new SparkConf().setAppName("GraphWalk").setMaster("local[4]")
    val sc = new SparkContext(conf)

    val perturbedGraph = loadGraph(config.getString("App.perturbedFilePath"), sc)
    val originalGraph = loadGraph(config.getString("App.originalFilePath"), sc)

    perturbedGraph match {
      case Some(perturbedGraph) =>
        logger.info("Beginning random walks on graph:")

        originalGraph match {
          case Some(originalGraph) =>
            val startNode = perturbedGraph.vertices.takeSample(withReplacement = false, 1)(0)._1
            val Node2 = originalGraph.vertices.takeSample(withReplacement = false, 1)(0)._1

            logger.info(s"Beginning with Vertex: $startNode")
            logger.info("Calculating Sim Rank Test:")

            val SimRank = new SimRank();
            val result = SimRank.calculateSimRank(perturbedGraph, originalGraph, startNode, Node2, config.getInt("App.similarityDepth"))
            println(result)

          case None =>
            logger.warn("MAIN: Graph Failed to load")
        }
      case None =>
        logger.warn("MAIN: Graph Failed to load")
    }

    sc.stop()
  }
}
