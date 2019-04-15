package com.lunchlist.restaurant

import collection.mutable.ArrayBuffer

class Property(val name: String)
case class OtherProperty(propName: String) extends Property(propName)
case object GlutenFree extends Property("gluten-free")
case object LactoseFree extends Property("lactose-free")
case object MilkFree extends Property("milk-free")
case object Vegan extends Property("vegan")
case object Vegetarian extends Property("vegetarian")

class Component(val name: String = "undefined") {

  val properties: List[Property] = {
    def parseProps(str: String): List[String] =
      str
        .dropWhile((c: Char) => c != '(')
        .takeWhile((c: Char) => c != ')')
        .split(",")
        .map((str: String) => str.trim)
        .toList

    def matchProps(str: String): List[Property] = str.toLowerCase() match {
      case "g"    => List(GlutenFree)
      case "l"    => List(LactoseFree)
      case "m"    => List(MilkFree, LactoseFree)
      case "veg"  => List(Vegan, Vegetarian)
      case _      => List(OtherProperty(str))
    }

    parseProps(this.name).flatMap(matchProps).toList
  }

}

class Food(val name: String, val components: List[Component]) {

  def containsFilteredProps(filteredProps: List[Property]) = {
    val props = this.components.map(_.properties)
    val existsOnProps = (prop: Property) => props.forall(_.contains(prop))
    val appearsInName = (prop: Property) => this.name.toLowerCase.contains(prop.name)
    filteredProps.forall((p: Property) => existsOnProps(p) || appearsInName(p))
  }
  
}

class Menu(val day: String = "", private val allFoods: List[Food] = List[Food]()) {

  private var foods: List[Food] = allFoods

  def filterForProperties(filteredProps: List[Property]): Unit = {
    if(filteredProps.isEmpty)
      foods = allFoods
    else
      foods = allFoods.filter(_.containsFilteredProps(filteredProps))
  }

  override def toString(): String = {
    this.foods.map((f: Food) => 
      f.name + 
      "\n  - " + 
      f.components
        .map(_.name)
        .mkString("\n  - ")
    ).mkString("\n")
  }
  
}