package com.lunchlist.restaurant

import java.net.URI

abstract class Restaurant(val name: String, val menuUri: URI) {
  def parseMenu: Unit
  var menu: String = " - blank menu"
  override def toString(): String = this.name + "\n" + this.menu
}