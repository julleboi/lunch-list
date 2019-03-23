package com.lunchlist.util

import io.Source
import collection.mutable.ListBuffer

import java.io.PrintWriter

import play.api.libs.json._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.async.Async.{async, await}

import com.lunchlist.restaurant._
import com.lunchlist.restaurant.Menu._

object JsonTools {

  private def readFromFile(path: String): String = {
    val source = Source.fromFile(path)
    val raw = source.getLines.reduceLeft(_ + "\n" + _)
    return raw
  }

  private val configFilePath = "./data/configurations.json"
  
  private val configRaw = readFromFile(configFilePath)

  private def restaurantsToList(rawStr: String): List[Restaurant] = {
    val json = Json.parse(rawStr)
    val restaurants: ListBuffer[Restaurant] = new ListBuffer()
    val restaurantsObjects = (json \ "restaurants").as[List[Map[String, String]]]
    for(restaurant <- restaurantsObjects) {
      val rType = restaurant("type")
      val name = restaurant("name")
      val id = restaurant("id")
      rType match {
        case "fazer" => 
          restaurants += new FazerRestaurant(name, id)
        case "sodexo" => 
          restaurants += new SodexoRestaurant(name, id)
        case unknownType => 
          println(s"Cannot create a Restaurant-object for a Restaurant of type '$rType'")
      }
    }
    return restaurants.toList
  }

  def loadRestaurants() = restaurantsToList(configRaw)

  /*
  * TODO: Fetch JSONs asynchronously to cut down the loading phase
  */
  private def getRawMenus(restaurant: Restaurant): Option[String] = {
    if(restaurant.hasMenu()) {
      val file = Source.fromFile(restaurant.getMenuFilePath())
      val raw = file.mkString
      return Some(raw)
    } else {
      val urls = restaurant.getURLs()
      val readFromURL = (url: String) => Source.fromURL(url).mkString
      val prettify = (rawStr: String) => Json.prettyPrint(Json.parse(rawStr))
      val raw = {
        if(urls.length > 1) {
          val start = """{"menus":["""
          val mid = (urls.init.map(url => readFromURL(url) + ","):+readFromURL(urls.last)).mkString
          val end = """]}"""
          prettify(start + mid + end)
        } else {
          val str = urls.map(readFromURL).mkString
          prettify(str)
        }
      }
      new PrintWriter(restaurant.getMenuFilePath) {
        write(raw)
        close
      }
      return Some(raw)
    }
    return None
  }

  private def loadFazerMenu(restaurant: Restaurant): Unit = {
    val menusOption = getRawMenus(restaurant)
    menusOption match {
      case Some(menu) => {
        println(s"Found menu for restaurant '${restaurant.name}'")
        val json = Json.parse(menu)
        val menus: ListBuffer[Menu] = new ListBuffer[Menu]()
        val menuObjects = (json \ "MenusForDays").as[List[JsObject]]
        for(menu <- menuObjects) {
          val foodsBuffer = new ListBuffer[Food]()
          val foodsObjects = (menu \ "SetMenus").as[List[JsObject]]
          for(food <- foodsObjects) {
            foodsBuffer += Food(s"lunch ${foodsBuffer.length}", food("Components").as[List[String]].map(x => Component(x)))
          }
          menus += new Menu(foodsBuffer.toList)
        }
        restaurant.setMenus(menus.toList)
      }
      case None => println(s"Wasn't able to fetch menu for restaurant '${restaurant.name}'")
    }
  }

  private def loadSodexoMenu(restaurant: Restaurant): Unit = {
    val menusOption = getRawMenus(restaurant)
      menusOption match {
      case Some(menu) => {
        println(s"Found menu for restaurant '${restaurant.name}'")
        val json = Json.parse(menu)
        val menus: ListBuffer[Menu] = new ListBuffer[Menu]()

      }
      case None => println(s"Wasn't able to fetch menu for restaurant '${restaurant.name}'")
    }
  }

  def loadMenu(restaurant: Restaurant): Unit = {
    restaurant match {
      case fazer: FazerRestaurant =>
        loadFazerMenu(restaurant)
      case sodexo: SodexoRestaurant =>
        loadSodexoMenu(restaurant)
    }
  }
}