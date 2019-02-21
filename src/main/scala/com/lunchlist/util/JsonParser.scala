package com.lunchlist.util

import collection.mutable.ListBuffer

import play.api.libs.json._

import com.lunchlist.restaurant._

object JsonParser {
  def restaurantsToList(rawStr: String): List[Restaurant] = {
    val json = Json.parse(rawStr)
    val restaurants: ListBuffer[Restaurant] = new ListBuffer()
    val restaurantsObjects = (json \ "restaurants").as[List[Map[String, String]]]
    for(restaurant <- restaurantsObject){
      val rType = restaurant("type")
      val name = restaurant("name")
      val id = restaurant("id")
      rType match {
        case "fazer" => 
          restaurants += new FazerRestaurant(name, id)
        case "sodexo" => 
          restaurants += new SodexoRestaurant(name, id)
        case unknownType => 
          println(s"Unknown restaurant type '$rType'")
      }
    }
    return restaurants.toList
  }
}