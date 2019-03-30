package com.lunchlist.restaurant

import io.Source

import com.lunchlist.util.DateTools.{getWeek, getDay}

abstract class Restaurant(val name: String, val id: String) {

  def getURLs(): List[String]

  def getMenuFilePath(): String = "./data/menus/"+getWeek()+"-"+id+".json"

  def hasMenu(): Boolean = new java.io.File(this.getMenuFilePath).exists

  protected var menus: List[Menu] = List[Menu]()

  def setMenus(menus: List[Menu]): Unit = this.menus = menus

  def getMenus(): List[Menu] = this.menus

  def getTodaysMenu(): Option[Menu] = this.menus.find(_.day == getDay())

  protected var favorite: Boolean = false

  def isFavorite(): Boolean = this.favorite

  def setFavorite(b: Boolean) = this.favorite = b

  override def toString(): String = this.name+"\n"+("="*this.name.length)+"\n"+this.getTodaysMenu.getOrElse("No menu for this day")+"\n"
  
}