package com.lsc

import NetGraphAlgebraDefs.{Action, NodeObject}
import org.apache.spark.SparkContext
import org.apache.spark.graphx.Graph
import java.io.FileInputStream
import NetGraphAlgebraDefs.NetGraphComponent
import org.apache.spark.graphx._
import org.apache.spark.rdd.RDD
import org.slf4j.LoggerFactory

import java.io._
import java.io.ObjectInputStream
import scala.util.{Failure, Success, Try}

object GraphLoader {
  private val logger = LoggerFactory.getLogger(getClass)
  def loadGraph(fileName: String): List[NetGraphComponent] = {
    logger.info(s"Loading the NetGraph from $fileName")
    val fis = new FileInputStream(fileName)
    val ois = new ObjectInputStream(fis)
    val ng = ois.readObject.asInstanceOf[List[NetGraphComponent]]

    ng
  }

}