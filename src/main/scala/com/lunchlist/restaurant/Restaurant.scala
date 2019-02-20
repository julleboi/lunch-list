package com.lunchlist.restaurant

abstract class Restaurant(val name: String, val id: String) {
  def getURL: String
  def parseMenu: Unit
  var menu: String = " - blank menu"
  override def toString(): String = this.name + "\n" + this.menu
}