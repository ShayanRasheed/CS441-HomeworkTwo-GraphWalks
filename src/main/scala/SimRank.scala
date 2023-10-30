package com.lsc

import org.slf4j.LoggerFactory
import NetGraphAlgebraDefs.{Action, NodeObject}
import NetGraphAlgebraDefs.NetGraphComponent
import org.apache.spark.SparkContext
import org.apache.spark.graphx.Graph
import org.apache.spark.graphx._

import scala.language.postfixOps
class SimRank {
  private val logger = LoggerFactory.getLogger(getClass)
  def calculateSimRank(graph1: Graph[NodeObject, Action], graph2: Graph[NodeObject, Action], node1: VertexId, node2: VertexId, depth: Int): Double = {
    logger.info("In Sim Rank:")
    

    0.0
  }
}
