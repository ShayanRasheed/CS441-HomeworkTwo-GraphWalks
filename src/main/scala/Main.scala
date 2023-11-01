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
            val numAttacks = config.getInt("App.numAttacks")

            val startNodes : Array[VertexId] = perturbedGraph.vertices.takeSample(withReplacement = false, numAttacks)
              .map { case (neighborId, _) => neighborId }

            val attackResults = startNodes.map { startNode =>
              randomWalk(perturbedGraph, originalGraph, startNode, valuableNodes, List.empty)
            }

            logger.info("Attack Results received:")
            println(attackResults.mkString("Array(", ", ", ")"))

            val matches = attackResults.filter(result => result >= 0)
            val successfulAttacks = matches.intersect(valuableNodes)

            val numSuccessfulAttacks = successfulAttacks.length
            val numFailedAttacks = matches.length - numSuccessfulAttacks

            val successRatio = numSuccessfulAttacks.toDouble / numAttacks
            val failRatio = numFailedAttacks.toDouble / numAttacks

            logger.info("----RESULTS:-----")
            println(s"Number of attacks: $numAttacks")
            println(s"Number of successful attacks: $numSuccessfulAttacks")
            println(s"Number of failed attacks: $numFailedAttacks")

            println("Valuable nodes from original graph:")
            valuableNodes.foreach(x => println(x))

            println("Successfully identified valuable nodes in perturbed graph")
            successfulAttacks.foreach(x => println(x))

            println(s"Ratio of successful attacks / total number of attacks: $successRatio")
            println(s"Ratio of failed attacks / total number of attacks: $failRatio")

          case None =>
            logger.warn("MAIN: Graph Failed to load")
        }
      case None =>
        logger.warn("MAIN: Graph Failed to load")
    }

    sc.stop()
  }
}
