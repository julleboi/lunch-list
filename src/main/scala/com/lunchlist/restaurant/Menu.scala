package com.lunchlist.restaurant

case class Component(val name: String = "undefined")

case class Food(val name: String, val components: List[Component])

class Menu(val foods: List[Food] = List[Food]())