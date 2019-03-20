package com.lunchlist.restaurant

import io.Source

import com.lunchlist.util.Date.getWeek

abstract class Restaurant(val name: String, val id: String) {
  def getURL(): String
  def getMenuFilePath(): String = s"./data/menus/${getWeek}-${id}.json"
  def hasMenu(): Boolean = new java.io.File(this.getMenuFilePath).exists
  protected var menu: Menu = new Menu()
  def setMenu(menu: Menu): Unit = this.menu = menu
  def getMenu(): Menu = this.menu
  override def toString(): String = this.name
}