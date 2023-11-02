package com.lsc

import NetGraphAlgebraDefs.{Action, NodeObject}
import NetGraphAlgebraDefs.NetGraphComponent
import org.apache.spark.SparkContext
import org.apache.spark.graphx.Graph
import org.apache.spark.graphx._
import org.apache.spark.rdd.RDD
import org.slf4j.LoggerFactory

import java.io._
import java.io.ObjectInputStream
import java.io.FileInputStream
import java.net.URL
import scala.util.{Failure, Success, Try}

object GraphLoader {
  private val logger = LoggerFactory.getLogger(getClass)
  def loadGraph(fileName: String, sc: SparkContext): Option[Graph[NodeObject, Action]] = {
    logger.info(s"Loading the NetGraph from $fileName")

    Try {
      val inputStream: InputStream = if (fileName.startsWith("http://") || fileName.startsWith("https://")) {
        val url = new URL(fileName)
        url.openStream()
      } else {
        new FileInputStream(new File(fileName))
      }

      val ois = new ObjectInputStream(inputStream)
      val ng = ois.readObject.asInstanceOf[List[NetGraphComponent]]

      inputStream.close()
      ois.close()

      ng
    } match {
      case Success(lstOfNetComponents) =>
        val vertices: RDD[(VertexId, NodeObject)] = sc.parallelize(lstOfNetComponents.collect {
          case node: NodeObject => (node.id.toLong, node)
        })
        val edges: RDD[Edge[Action]] = sc.parallelize(lstOfNetComponents.collect {
          case action: Action => Edge(action.fromNode.id.toLong, action.toNode.id.toLong, action)
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