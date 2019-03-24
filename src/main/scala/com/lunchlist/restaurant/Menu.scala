package com.lunchlist.restaurant

case class Component(val name: String = "undefined")

case class Food(val name: String, val components: List[Component])

class Menu(val day: String = "", val foods: List[Food] = List[Food]()) {
  override def toString(): String = this.day+"\n\n"+this.foods.map(f=>f.name+"\n  - "+f.components.map(_.name).mkString("\n  - ")).mkString("\n")
}