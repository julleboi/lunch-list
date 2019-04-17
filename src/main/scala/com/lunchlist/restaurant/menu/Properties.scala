package com.lunchlist.restaurant.menu

class Property(val name: String)

case class OtherProperty(propName: String) extends Property(propName)

case object GlutenFree extends Property("gluten-free")

case object LactoseFree extends Property("lactose-free")

case object MilkFree extends Property("milk-free")

case object Vegan extends Property("vegan")

case object Vegetarian extends Property("vegetarian")