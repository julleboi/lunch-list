package com.lunchlist.util

import collection.mutable.ListBuffer

import play.api.libs.json._

import com.lunchlist.restaurant._

object JsonParser {
  def restaurantsToList(rawStr: String): List[Restaurant] = {
    val json = Json.parse(rawStr)
    val restaurants: ListBuffer[Restaurant] = new ListBuffer()
    val restaurantsJSON = (json \ "restaurants").as[List[Map[String, String]]]
    for(restaurant <- restaurantsJSON){
      val rType = restaurant("type")
      val name = restaurant("name")
      val id = restaurant("id")
      var r: Restaurant = null
      rType match {
        case "fazer" => 
          r = new FazerRestaurant(name, id)
        case "sodexo" => 
          r = new SodexoRestaurant(name, id)
        case unknownType => 
          println(s"Unknown restaurant type '$rType'")
      }
      restaurants += r
    }
    return restaurants.toList
  }
}