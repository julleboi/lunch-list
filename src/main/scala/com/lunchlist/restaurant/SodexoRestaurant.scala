package com.lunchlist.restaurant

import java.net.URI

class SodexoRestaurant(name: String, menuUri: URI) extends Restaurant(name, menuUri) {
  def parseMenu() = {
    this.menu = "A Sodexu menu"
  }
}