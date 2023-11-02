package com.lsc

import org.slf4j.LoggerFactory
import com.lsc.{Action, NodeObject}
import org.apache.spark.SparkContext
import org.apache.spark.graphx.Graph
import org.apache.spark.graphx._
import com.typesafe.config.{Config, ConfigFactory}

import scala.language.postfixOps
import scala.util.Random

object RandomWalk {
  private val logger = LoggerFactory.getLogger(getClass)
  private val config: Config = ConfigFactory.load()
  private val simRank = new SimRank()

  def randomWalk(perturbedGraph: Graph[NodeObject, Action], originalGraph: Graph[NodeObject, Action], startNode: VertexId, valuableNodes: Array[VertexId], initialVisited: List[VertexId]): VertexId = {
    logger.trace(s"Beginning Random Walk with Vertex: $startNode")
    val maxNumWalks = config.getInt("App.maxNumWalks")

    def traverse(node: VertexId, visited: List[VertexId], remainingWalks: Int): VertexId = {
      val visitedNodes = visited :+ startNode

      val similarNodes = valuableNodes.filter { nodeId =>
        val similarity = simRank.calculateSimRank(perturbedGraph, originalGraph, node, nodeId, config.getInt("App.similarityDepth"))
        similarity > config.getDouble("App.similarityThreshold")
      }

      if(!similarNodes.isEmpty) {
        val randomIndex = Random.nextInt(similarNodes.length)
        similarNodes(randomIndex)
      }
      else {
        val neighbors = perturbedGraph.collectNeighbors(EdgeDirection.Either).lookup(node)
        val unvisitedNeighbors = neighbors.head.filter { case (neighborId, _) =>
          !visitedNodes.contains(neighborId)
        }.map { case (neighborId, _) =>
          neighborId
        }

        if(!unvisitedNeighbors.isEmpty && remainingWalks > 0) {
          val randomIndex = Random.nextInt(unvisitedNeighbors.length)
          traverse(unvisitedNeighbors(randomIndex), visitedNodes, remainingWalks - 1)
        }
        else {
          -1
        }
      }
    }

    traverse(startNode, initialVisited, maxNumWalks)
  }
}
