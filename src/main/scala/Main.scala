package com.lsc

import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.graphx._
import GraphLoader.loadGraph

object Main {

  def main(args: Array[String]): Unit = {
    //val conf = new SparkConf().setAppName("GraphWalk").setMaster("local")
    //val sc = new SparkContext(conf)

//    // Define vertices and edges
//    val vertices = sc.makeRDD(Array((1L, "Alice"), (2L, "Bob"), (3L, "Charlie"), (4L, "David")))
//    val edges = sc.makeRDD(Array(Edge(1L, 2L, "Friend"), Edge(2L, 3L, "Follows"), Edge(3L, 4L, "Friend")))
//
//    // Create a Graph
//    val defaultVertex = ("John Doe")
//    val graph = Graph(vertices, edges, defaultVertex)
//
//    // Basic GraphX operations
//    println("Vertices:")
//    graph.vertices.foreach { case (id, name) => println(s"$id: $name") }
//
//    println("Edges:")
//    graph.edges.foreach { edge => println(s"${edge.srcId} --${edge.attr}--> ${edge.dstId}") }
//
//    // Applying a graph algorithm: PageRank
//    val pageRank = graph.pageRank(0.1).vertices
//    println("PageRank results:")
//    pageRank.foreach { case (id, rank) => println(s"$id: $rank") }

    val list = loadGraph("/Users/shayanrasheed/Documents/Cloud Computing/NetGameSimNetGraph_21-10-23-12-20-35.ngs")

    println("Got here")
    list.foreach(x => println(x))

    //sc.stop()
  }
}
