package com.lunchlist.restaurant

import collection.mutable.ArrayBuffer

class Property(val name: String)
case class OtherProperty(propName: String) extends Property(propName)
case object GlutenFree extends Property("gluten free")
case object LactoseFree extends Property("lactose free")
case object Vegan extends Property("vegan")

case class Component(val name: String = "undefined") {
  val properties: List[Property] = 
    this.name
      .dropWhile(char => char != '(')
        .takeWhile(char => char != ')')
          .split(",")
            .map(_.trim)
              .map(str => str.toLowerCase() match {
                case "g" => GlutenFree
                case "l" => LactoseFree
                case "veg" => Vegan
                case _ => OtherProperty(str)
              })
                .toList
}

case class Food(val name: String, val components: List[Component])

class Menu(val day: String = "", private val allFoods: List[Food] = List[Food]()) {

  private var foods: List[Food] = allFoods

  def filterForProperties(filteredProps: List[Property]) = {
    if(filteredProps.isEmpty)
      foods = allFoods
    else
      foods = 
        allFoods
          .filter(food => {
            val props = food.components.map(_.properties)
            /* all food components' props have filteredProps */
            filteredProps.forall(prop => props.forall(_.contains(prop))) || 
            /* 
             * or food name contains a filtered props' names, 
             * e.g. you're filtering for vegan food and 
             * the food's title is vegan pizza, so the
             * condition is met in this case.
             */
            filteredProps.forall(prop => food.name.toLowerCase().contains(prop.name))
          })
  }

  override def toString(): String = this.foods.map(f=>f.name+"\n  - "+f.components.map(_.name).mkString("\n  - ")).mkString("\n")
  
}