package com.lunchlist.restaurant

import java.net.URI

class FazerRestaurant(name: String, menuUri: URI) extends Restaurant(name, menuUri) {
  def parseMenu() = {
    this.menu = "A Fazer menu"
  }
}