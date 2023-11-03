package com.lsc

import org.slf4j.LoggerFactory
import org.apache.spark.SparkContext
import org.apache.spark.graphx.Graph
import org.apache.spark.graphx._
import com.typesafe.config.{Config, ConfigFactory}

import scala.annotation.tailrec
import scala.language.postfixOps
import scala.util.Random

object RandomWalk {
  private val logger = LoggerFactory.getLogger(getClass)
  private val config: Config = ConfigFactory.load()
  private val simRank = new SimRank()

  // RANDOM WALK
  // Performs a random walk through the perturbed graph in order to find valuable nodes
  // The length of the walk is determined by the 'maxNumWalks' parameter
  // If the first walk attempt fails to find a match, it will repeat the walk while avoiding any nodes it already visited
  def randomWalk(perturbedGraph: Graph[NodeObject, Action], originalGraph: Graph[NodeObject, Action], startNode: VertexId, valuableNodes: Array[VertexId]): (VertexId, List[VertexId]) = {
    logger.trace(s"Beginning Random Walk with Vertex: $startNode")
    val maxNumWalks = config.getInt("App.maxNumWalks")

    @tailrec
    def traverse(node: VertexId, visited: List[VertexId], remainingWalks: Int): (VertexId, List[VertexId]) = {
      logger.trace(s"Traversing Node: $node")
      logger.trace(s"Remaining walks: $remainingWalks")
      val visitedNodes = visited :+ node

      // Check if any valuable nodes are similar to the current node
      val similarNodes = valuableNodes.filter { nodeId =>
        val similarity = simRank.calculateSimRank(perturbedGraph, originalGraph, node, nodeId, config.getInt("App.similarityDepth"))
        logger.info(s"Similarity between nodes $node and $nodeId is $similarity")
        similarity > config.getDouble("App.similarityThreshold")
      }

      // If any valuable nodes are similar, return the current node
      if(!similarNodes.isEmpty) {
        (node, visitedNodes)
      }
        // Otherwise, continue the walk
      else {
        val unvisitedNeighbors = RandomWalk.unvisitedNeighbors(perturbedGraph, node, visitedNodes)
        logger.info(s"Unvisited Neighbors found: ${unvisitedNeighbors.mkString("Array(", ", ", ")")}")

        if(!unvisitedNeighbors.isEmpty && remainingWalks > 0) {
          // Pick a random unvisited neighbor to check next
          val randomIndex = Random.nextInt(unvisitedNeighbors.length)
          traverse(unvisitedNeighbors(randomIndex), visitedNodes, remainingWalks - 1)
        }
        else {
          (-1, visitedNodes)
        }
      }
    }

    val result = traverse(startNode, List.empty, maxNumWalks)
    // Once initial walk is completed, return if a match was found
    // Otherwise, repeat walk
    result match{
      case (-1, myList) =>
        val unvisitedNeighbors = RandomWalk.unvisitedNeighbors(perturbedGraph, startNode, myList)
        if (!unvisitedNeighbors.isEmpty) {
          val randomIndex = Random.nextInt(unvisitedNeighbors.length)
          traverse(unvisitedNeighbors(randomIndex), myList, maxNumWalks)
        } else {
          result
        }
      case _ =>
        result
    }
  }

  // UNVISITED NEIGHBORS
  // Returns all nodes connected to a specified node that are not already in the set of visited nodes
  def unvisitedNeighbors(graph: Graph[NodeObject, Action], node: VertexId, visitedNodes: List[VertexId]): Array[VertexId] = {
    val neighbors = graph.collectNeighbors(EdgeDirection.Either).lookup(node)
    val unvisitedNeighbors = neighbors.head.filter { case (neighborId, _) =>
      !visitedNodes.contains(neighborId)
    }.map { case (neighborId, _) =>
      neighborId
    }

    unvisitedNeighbors
  }
}
