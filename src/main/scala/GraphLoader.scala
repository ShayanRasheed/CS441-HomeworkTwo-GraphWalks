package com.lsc

import org.apache.spark.SparkContext
import org.apache.spark.graphx.Graph
import org.apache.spark.graphx._
import org.apache.spark.rdd.RDD
import org.slf4j.LoggerFactory

import java.io._
import java.net.URL
import scala.util.{Failure, Success, Try}
import scala.io.Source

object GraphLoader {
  private val logger = LoggerFactory.getLogger(getClass)

  // LOAD GRAPH
  // Creates graph object from text file produced by ngsConverter
  // Can load graph from local file as well as s3 bucket
  def loadGraph(fileName: String, sc: SparkContext): Option[Graph[NodeObject, Action]] = {
    logger.info(s"Loading the NetGraph from $fileName")

    Try {
      val source = if (fileName.startsWith("http://") || fileName.startsWith("https://")) {
        val url = new URL(fileName)
        Source.fromURL(url)
      } else {
        Source.fromFile(fileName)
      }

      val components = source.getLines.flatMap { line =>
        val parts = line.split(" ")
        parts.map {
          case str if str.forall(_.isDigit) => Action(parts(0).toInt, str.toInt)
          case "true" | "false" => NodeObject(parts(0).toInt, parts(1).toBoolean)
        }
      }.toList

      source.close()
      //components.foreach(x => println(x))

      components
    } match {
      case Success(lstOfNetComponents) =>
        val vertices: RDD[(VertexId, NodeObject)] = sc.parallelize(lstOfNetComponents.collect {
          case node: NodeObject => (node.id.toLong, node)
        })
        val edges: RDD[Edge[Action]] = sc.parallelize(lstOfNetComponents.collect {
          case action: Action => Edge(action.fromId.toLong, action.toId.toLong, action)
        })
        logger.info("Returning Graph Object")
        Some(Graph(vertices, edges))
      case Failure(e: FileNotFoundException) =>
        logger.error(s"File not found: $fileName", e)
        None
      case Failure(e) =>
        logger.error("An error occurred while loading the graph", e)
        None
    }
  }

}