package com.lunchlist.restaurant

import collection.mutable.ListBuffer

import com.lunchlist.util.DateTools._

class SodexoRestaurant(name: String, id: String) extends Restaurant(name, id) {
  def getURLs(): List[String] = getDatesThisWeek().map((date: String) => 
    "https://www.sodexo.fi/ruokalistat/output/daily_json/"+this.id+"/"+date+"/en")
}