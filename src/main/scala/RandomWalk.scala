package com.lsc

import org.slf4j.LoggerFactory

import NetGraphAlgebraDefs.{Action, NodeObject}
import NetGraphAlgebraDefs.NetGraphComponent

import org.apache.spark.SparkContext
import org.apache.spark.graphx.Graph
import org.apache.spark.graphx._

object RandomWalk {
  private val logger = LoggerFactory.getLogger(getClass)

  def randomWalk(graph: Graph[NodeObject, Action], startNode: NodeObject): Unit = {

  }
}
