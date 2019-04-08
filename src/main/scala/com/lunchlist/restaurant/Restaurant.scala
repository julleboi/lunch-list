package com.lunchlist.restaurant

import io.Source

import com.lunchlist.util.Misc.{fileExists}
import com.lunchlist.util.DateTools.{getWeek, getDay}

abstract class Restaurant(val name: String, val id: String) {

  def getURLs(): List[String]

  val menusFilePath: String = "menus/"+getWeek()+"-"+id+".json"

  def menusFileExists(): Boolean = fileExists(menusFilePath)

  protected var menus: List[Menu] = List[Menu]()

  def setMenus(menus: List[Menu]): Unit = this.menus = menus

  def getMenus(): List[Menu] = this.menus

  def getMenuForDay(day: String): Option[Menu] = this.menus.find(_.day == day)

  def getTodaysMenu(): Option[Menu] = this.getMenuForDay(getDay())

  protected var favorite: Boolean = false

  def isFavorite(): Boolean = this.favorite

  def setFavorite(b: Boolean) = this.favorite = b

  override def toString(): String = {
    this.name + 
    "\n" + 
    ( "=" * this.name.length ) + 
    "\n" + 
    this.getTodaysMenu.getOrElse("No menu for this day") + 
    "\n"
  }
  
}

case object UnknownRestaurant extends Restaurant("Unknown", "") { def getURLs() = List("") }