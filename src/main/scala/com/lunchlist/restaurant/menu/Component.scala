package com.lunchlist.restaurant.menu

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