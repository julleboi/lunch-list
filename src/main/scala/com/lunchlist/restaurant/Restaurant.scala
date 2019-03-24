package com.lunchlist.restaurant

import io.Source

import com.lunchlist.util.DateTools.getWeek

abstract class Restaurant(val name: String, val id: String) {

  def getURLs(): List[String]

  def getMenuFilePath(): String = "./data/menus/"+getWeek()+"-"+id+".json"

  def hasMenu(): Boolean = new java.io.File(this.getMenuFilePath).exists

  protected var menus: List[Menu] = List[Menu]()

  def setMenus(menus: List[Menu]): Unit = this.menus = menus

  def getMenus(): List[Menu] = this.menus

  override def toString(): String = this.name + "\n==========\n" + menus.map(_.toString()).mkString("\n-----\n") + "==========\n"
  
}