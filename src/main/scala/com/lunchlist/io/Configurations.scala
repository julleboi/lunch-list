package com.lunchlist.io

import io.Source

import com.lunchlist.util.JsonParser.restaurantsToList

object Configurations {
  def readFromFile(path: String): String = {
    val source = Source.fromFile(path)
    val raw = source.getLines.reduceLeft(_ + "\n" + _)
    return raw
  }
  private val configFilePath = "./data/configurations.json"
  private val configRaw = readFromFile(configFilePath)
  def loadRestaurants() = restaurantsToList(configRaw)
}