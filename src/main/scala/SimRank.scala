package com.lsc

import org.slf4j.LoggerFactory
import NetGraphAlgebraDefs.{Action, NodeObject}
import NetGraphAlgebraDefs.NetGraphComponent
import org.apache.spark.SparkContext
import org.apache.spark.graphx.Graph
import org.apache.spark.graphx._

import scala.collection.mutable.ListBuffer
import scala.language.postfixOps
class SimRank {
  private val logger = LoggerFactory.getLogger(getClass)
  def calculateSimRank(graph1: Graph[NodeObject, Action], graph2: Graph[NodeObject, Action], node1: VertexId, node2: VertexId, depth: Int): Double = {
    logger.info("In Sim Rank:")

    val neighbors1 = findNeighbors(graph1, node1, depth, ListBuffer.empty)
    neighbors1.foreach(x => println(x))

    logger.trace(s"Sim Rank for Node $node1 and Node $node2 completed")
    0.0
  }

  private def findNeighbors(graph: Graph[NodeObject, Action], node: VertexId, depth: Int, result: ListBuffer[VertexId]) : ListBuffer[VertexId] = {
    val neighbors = graph.collectNeighbors(EdgeDirection.Either).lookup(node)
    neighbors.foreach(x => println(x.mkString("Array(", ", ", ")")))

    if(depth <= 0 || result.contains(node)) {
      return result
    }

    val list = neighbors.head
    for ((neighborId, _) <- list) {
      findNeighbors(graph, neighborId, depth - 1, result)
    }

    result
  }
}
