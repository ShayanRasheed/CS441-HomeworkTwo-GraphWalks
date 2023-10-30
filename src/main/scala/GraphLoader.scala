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
  def loadGraph(fileName: String, sc: SparkContext): Option[Graph[NodeObject, Action]] = {
    logger.info(s"Loading the NetGraph from $fileName")

    Try {
          val fis = new FileInputStream(fileName)
          val ois = new ObjectInputStream(fis)
          val ng = ois.readObject.asInstanceOf[List[NetGraphComponent]]

//      val temp = NodeObject(id = 1, children = 2, props = 2, currentDepth = 1, propValueRange = 2, maxDepth = 1, maxBranchingFactor = 1, maxProperties = 1, storedValue = 10)
//      val temp2 = NodeObject(id = 2, children = 1, props = 5, currentDepth = 2, propValueRange = 3, maxDepth = 1, maxBranchingFactor = 1, maxProperties = 1, storedValue = 20, valuableData = true)
//      val temp3 = NodeObject(id = 3, children = 1, props = 5, currentDepth = 2, propValueRange = 3, maxDepth = 1, maxBranchingFactor = 1, maxProperties = 1, storedValue = 30)
//      val temp4 = NodeObject(id = 4, children = 1, props = 5, currentDepth = 2, propValueRange = 3, maxDepth = 1, maxBranchingFactor = 1, maxProperties = 1, storedValue = 40, valuableData = true)
//      val temp5 = NodeObject(id = 5, children = 1, props = 5, currentDepth = 2, propValueRange = 3, maxDepth = 1, maxBranchingFactor = 1, maxProperties = 1, storedValue = 50)
//      val temp6 = NodeObject(id = 6, children = 1, props = 5, currentDepth = 2, propValueRange = 3, maxDepth = 1, maxBranchingFactor = 1, maxProperties = 1, storedValue = 60, valuableData = true)
//      val temp7 = NodeObject(id = 7, children = 1, props = 5, currentDepth = 2, propValueRange = 3, maxDepth = 1, maxBranchingFactor = 1, maxProperties = 1, storedValue = 70)
//      val temp8 = NodeObject(id = 8, children = 1, props = 5, currentDepth = 2, propValueRange = 3, maxDepth = 1, maxBranchingFactor = 1, maxProperties = 1, storedValue = 80, valuableData = true)
//      val temp9 = NodeObject(id = 9, children = 1, props = 5, currentDepth = 2, propValueRange = 3, maxDepth = 1, maxBranchingFactor = 1, maxProperties = 1, storedValue = 90)
//
//      val act = Action(actionType = 1, fromNode = temp, toNode = temp2, fromId = 1, toId = 2, resultingValue = None, cost = 0.0)
//      val act2 = Action(actionType = 1, fromNode = temp, toNode = temp5, fromId = 1, toId = 5, resultingValue = None, cost = 0.0)
//      val act3 = Action(actionType = 1, fromNode = temp2, toNode = temp6, fromId = 2, toId = 6, resultingValue = None, cost = 0.0)
//      val act4 = Action(actionType = 1, fromNode = temp3, toNode = temp8, fromId = 3, toId = 8, resultingValue = None, cost = 0.0)
//      val act5 = Action(actionType = 1, fromNode = temp4, toNode = temp3, fromId = 4, toId = 3, resultingValue = None, cost = 0.0)
//      val act6 = Action(actionType = 1, fromNode = temp5, toNode = temp9, fromId = 5, toId = 9, resultingValue = None, cost = 0.0)
//      val act7 = Action(actionType = 1, fromNode = temp6, toNode = temp4, fromId = 1, toId = 2, resultingValue = None, cost = 0.0)
//      val act8 = Action(actionType = 1, fromNode = temp7, toNode = temp, fromId = 7, toId = 1, resultingValue = None, cost = 0.0)
//      val act9 = Action(actionType = 1, fromNode = temp8, toNode = temp5, fromId = 8, toId = 4, resultingValue = None, cost = 0.0)
//      val act10 = Action(actionType = 1, fromNode = temp9, toNode = temp7, fromId = 9, toId = 7, resultingValue = None, cost = 0.0)
//
//      val ng = List.apply(temp, temp2, temp3, temp4, temp5, temp6, temp7, temp8, temp9, act, act2, act3, act4, act5, act6, act7, act8, act9, act10)

      fis.close()
      ois.close()

      ng
    } match {
      case Success(lstOfNetComponents) =>
        val vertices: RDD[(VertexId, NodeObject)] = sc.parallelize(lstOfNetComponents.collect {
          case node: NodeObject => (node.id.toLong, node)
        })
        val edges: RDD[Edge[Action]] = sc.parallelize(lstOfNetComponents.collect {
          case action: Action => Edge(action.fromId.toLong, action.toId.toLong, action)
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