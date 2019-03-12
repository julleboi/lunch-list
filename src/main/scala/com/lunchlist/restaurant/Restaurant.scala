package com.lunchlist.restaurant

import io.Source

abstract class Restaurant(val name: String, val id: String) {
  protected def getURL(): String

  protected var menuRaw: String = null
  protected def fetchMenus(): Unit = {
    val url = this.getURL()
    val file = Source.fromURL(url)
    val raw = file.mkString
    this.menuRaw = raw
  }
  fetchMenus()

  override def toString(): String = this.name
}