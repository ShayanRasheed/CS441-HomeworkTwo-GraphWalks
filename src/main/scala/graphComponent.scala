package com.lsc
trait graphComponent

case class NodeObject(id: Int, valuableData: Boolean)

case class Action(fromId: Int, toId: Int)
