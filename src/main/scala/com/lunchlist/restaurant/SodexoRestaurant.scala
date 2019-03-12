package com.lunchlist.restaurant

import com.lunchlist.util.Date.getDate

class SodexoRestaurant(name: String, id: String) extends Restaurant(name, id) {
  def getURL() = "https://www.sodexo.fi/ruokalistat/output/daily_json/"+this.id+"/"+getDate("yyyy/MM/dd")+"/en"
}