package com.lunchlist.restaurant

abstract class Restaurant(val name: String, val id: String) {
  def getURL: String
  def fetchMenu: Unit
  var menu: String = null
  override def toString(): String = this.name + "\n" + this.menu
}