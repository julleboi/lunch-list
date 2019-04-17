package com.lunchlist.restaurant

import collection.mutable.ArrayBuffer

class Property(val name: String)
case class OtherProperty(propName: String) extends Property(propName)
case object GlutenFree extends Property("gluten-free")
case object LactoseFree extends Property("lactose-free")
case object MilkFree extends Property("milk-free")
case object Vegan extends Property("vegan")
case object Vegetarian extends Property("vegetarian")

case class Component(val name: String = "undefined") {

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

case class Food(val name: String, val components: List[Component]) {

  def containsFilteredProps(filteredProps: List[Property]) = {
    val props = this.components.map(_.properties)
    val existsOnProps = (prop: Property) => props.forall(_.contains(prop))
    val appearsInName = (prop: Property) => this.name.toLowerCase.contains(prop.name)
    filteredProps.forall((p: Property) => existsOnProps(p) || appearsInName(p))
  }
  
}

class Menu(val day: String = "", private val allFoods: List[Food] = List[Food]()) {

  private var foods: List[Food] = this.allFoods
  def getFoods(): List[Food] = this.foods

  private var filteredProps: List[Property] = List[Property]()
  private var filteredStr: String = ""

  def filterForProperties(filteredProps: List[Property]): Unit = {
    this.filteredProps = filteredProps
    val filteredForStr = filterString(this.filteredStr, this.allFoods)
    this.foods = filterProperties(filteredProps, filteredForStr)
  }

  private def filterProperties(filteredProps: List[Property], src: List[Food]): List[Food] = {
    if(filteredProps.isEmpty)
      src
    else
      src.filter(_.containsFilteredProps(filteredProps))
  }

  def filterForString(str: String): Unit = {
    this.filteredStr = str
    val filteredForProps = filterProperties(this.filteredProps, this.allFoods)
    this.foods = filterString(str, filteredForProps)
  }

  private def filterString(s: String, src: List[Food]): List[Food] = {
    def clean(str: String) = str.toLowerCase.trim
    val str = clean(s)
    if(str.isEmpty)
      src
    else
      src.filter(_.components.exists(c => clean(c.name).contains(str)))
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