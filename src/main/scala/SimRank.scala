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
    logger.trace("In Sim Rank:")

    val neighbors1 = findNeighbors(graph1, node1, depth, List.empty)
    neighbors1.foreach(x => println(x))

    val neighbors2 = findNeighbors(graph2, node2, depth, List.empty)

    val commonNeighbors = neighbors1.intersect(neighbors2)
    val totalUniqueNeighbors = (neighbors1 ++ neighbors2).distinct.size

    logger.trace(s"Sim Rank for Node $node1 and Node $node2 completed")
    if(totalUniqueNeighbors > 0) {
      commonNeighbors.size.toDouble / totalUniqueNeighbors
    }
    else {
      0.0
    }
  }

  private def findNeighbors(graph: Graph[NodeObject, Action], node: VertexId, depth: Int, visitedNodes: List[VertexId]) : List[VertexId] = {
    val updatedVisitedNodes = visitedNodes :+ node

    if (depth > 0) {
      val neighbors = graph.collectNeighbors(EdgeDirection.Either).lookup(node)
      val unvisitedNeighbors = neighbors.head.filter { case (neighborId, _) =>
        !visitedNodes.contains(neighborId)
      }

      unvisitedNeighbors.foldLeft(updatedVisitedNodes) { (acc, neighbor) =>
        findNeighbors(graph, neighbor._1, depth - 1, acc)
      }
    } else {
      updatedVisitedNodes
    }
  }
}
