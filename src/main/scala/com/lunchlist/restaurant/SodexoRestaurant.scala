package com.lunchlist.restaurant

import com.lunchlist.util.Moment.getDate

class SodexoRestaurant(name: String, id: String) extends Restaurant(name, id) {
  def getURL() = "https://www.sodexo.fi/ruokalistat/output/daily_json/"+this.id+"/"+getDate("yyyy/mm/dd")+"/en"
  def fetchMenu() = {
    this.menu = "A Sodexu menu"
  }
}