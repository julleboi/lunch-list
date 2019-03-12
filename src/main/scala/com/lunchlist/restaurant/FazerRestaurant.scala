package com.lunchlist.restaurant

import io.Source

class FazerRestaurant(name: String, id: String) extends Restaurant(name, id) {
  def getURL() = "https://www.fazerfoodco.fi/modules/json/json/Index?costNumber="+this.id+"&language=en"
}