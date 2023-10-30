package com.lsc

import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.graphx._
import GraphLoader.{getClass, loadGraph}
import NetGraphAlgebraDefs.{Action, NodeObject}
import com.typesafe.config.ConfigFactory
import org.slf4j.LoggerFactory

object Main {
  val config = ConfigFactory.load()
  private val logger = LoggerFactory.getLogger(getClass)

  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("GraphWalk").setMaster("local[4]")
    val sc = new SparkContext(conf)

    val graph = loadGraph(config.getString("App.originalFilePath"), sc)

    graph match {
      case Some(graph) =>
        logger.info("Beginning random walks on graph:")

        val startNode = graph.vertices.takeSample(withReplacement = false, 1)(0)._1

        logger.info(s"Beginning with Vertex: $startNode")

        
      case None =>
        logger.warn("MAIN: Graph Failed to load")
    }

    sc.stop()
  }
}
