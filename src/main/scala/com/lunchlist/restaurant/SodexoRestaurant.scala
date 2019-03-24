package com.lunchlist.restaurant

import com.lunchlist.util.DateTools._

class SodexoRestaurant(name: String, id: String) extends Restaurant(name, id) {

  def getURLs(): List[String] = getDatesThisWeek().map("https://www.sodexo.fi/ruokalistat/output/daily_json/"+this.id+"/"+_+"/en")
    
}