package com.lunchlist.restaurant.menu

class Menu(val day: String = "", private val allFoods: List[Food] = List[Food]()) {

  import Menu._
  
  private var foods: List[Food] = this.allFoods
  def getFoods(): List[Food] = this.foods
  
  private var filteredProps: List[Property] = List[Property]()
  private var filteredStr: String = ""

  def filterForProperties(filteredProps: List[Property]): Unit = {
    this.filteredProps = filteredProps
    val filteredForStr = filterString(this.filteredStr, this.allFoods)
    this.foods = filterProperties(filteredProps, filteredForStr)
  }

  def filterForString(str: String): Unit = {
    this.filteredStr = str
    val filteredForProps = filterProperties(this.filteredProps, this.allFoods)
    this.foods = filterString(str, filteredForProps)
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

object Menu {

  private def filterProperties(filteredProps: List[Property], src: List[Food]): List[Food] = {
    if(filteredProps.isEmpty)
      src
    else
      src.filter(_.containsFilteredProps(filteredProps))
  }

  private def filterString(s: String, src: List[Food]): List[Food] = {
    def clean(str: String) = str.toLowerCase.trim
    val str = clean(s)
    if(str.isEmpty)
      src
    else
      src
        .filter(f => 
          clean(f.name).contains(str) || 
          f.components.exists(c => clean(c.name).contains(str))
        )
  }

}