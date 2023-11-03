package com.lsc

import org.slf4j.LoggerFactory
import org.apache.spark.graphx.Graph
import org.apache.spark.graphx._
import scala.language.postfixOps

// SIMRANK
// Performs a simrank algorithm to find the similarity between two nodes from different graphs
// based on how many nearby nodes are the same
class SimRank {
  private val logger = LoggerFactory.getLogger(getClass)
  def calculateSimRank(graph1: Graph[NodeObject, Action], graph2: Graph[NodeObject, Action], node1: VertexId, node2: VertexId, depth: Int): Double = {
    logger.trace("In Sim Rank:")

    val neighbors1 = findNeighbors(graph1, node1, depth, List.empty)
    logger.info(s"Neighbors found for node $node1: ${neighbors1.toString()}")
    val neighbors2 = findNeighbors(graph2, node2, depth, List.empty)
    logger.info(s"Neighbors found for node $node2: ${neighbors2.toString()}")

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
    def findNodes(node: VertexId, currentDepth: Int): List[VertexId] = {
      if (currentDepth == 0) {
        List(node) // At the specified depth, return the current node
      } else {
        val neighbors = graph.collectNeighborIds(EdgeDirection.Either).lookup(node).headOption.getOrElse(Array.empty[VertexId])
        neighbors.flatMap(neighbor => findNodes(neighbor, currentDepth - 1)).toList
      }
    }

    findNodes(node, depth).distinct
  }
}
