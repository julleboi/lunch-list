package com.lunchlist.restaurant

class FazerRestaurant(name: String, id: String) extends Restaurant(name, id) {

  def getURLs() = List[String]("https://www.fazerfoodco.fi/modules/json/json/Index?costNumber="+this.id+"&language=en")
  
}