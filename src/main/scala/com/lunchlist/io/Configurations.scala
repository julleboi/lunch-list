package com.lunchlist.io

import collection.mutable.ListBuffer
import io.Source

import play.api.libs.json._

import com.lunchlist.restaurant._

object Configurations {
  val configurationsFilePath = "./data/configurations.json"
  val config = Json.parse(Source.fromFile(configurationsFilePath).getLines.reduceLeft(_ + "\n" + _))
  
  def loadRestaurants(): List[Restaurant] = {
    val restaurants: ListBuffer[Restaurant] = new ListBuffer()
    val restaurantsJSON = (config \ "restaurants").as[List[Map[String, String]]]
    for(restaurant <- restaurantsJSON){
      val rType = restaurant("type")
      val name = restaurant("name")
      val id = restaurant("id")
      var r: Restaurant = null
      rType match {
        case "fazer" => { r = new FazerRestaurant(name, id) }
        case "sodexo" => { r = new SodexoRestaurant(name, id) }
        case unknownType => println(s"Unknown restaurant type '$rType'")
      }
      restaurants += r
    }
    return restaurants.toList
  }
}