package com.lsc

import org.slf4j.LoggerFactory
import NetGraphAlgebraDefs.{Action, NodeObject}
import org.apache.spark.SparkContext
import org.apache.spark.graphx.Graph
import org.apache.spark.graphx._

object RandomWalk {
  private val logger = LoggerFactory.getLogger(getClass)

  def randomWalk(perturbedGraph: Graph[NodeObject, Action], originalGraph: Graph[NodeObject, Action], startNode: VertexId, valuableNodes: Array[VertexId], visited: Set[VertexId]): Unit = {
    logger.info(s"Beginning Random Walk with Vertex: $startNode")
    
  }
}
