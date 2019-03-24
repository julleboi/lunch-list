package com.lunchlist.util

import io.Source
import collection.mutable.ListBuffer

import scala.concurrent._
import scala.concurrent.duration._
import ExecutionContext.Implicits.global

import java.io.PrintWriter

import play.api.libs.json._

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

  private def getRawMenus(restaurant: Restaurant): Option[String] = {
    if(restaurant.hasMenu()) {
      val file = Source.fromFile(restaurant.getMenuFilePath())
      val raw = file.mkString
      return Some(raw)
    } else {
      val urls = restaurant.getURLs()
      val futures: List[Future[String]] = urls.map(url => Future { Source.fromURL(url).mkString })
      val result: String = futures.map(Await.result(_, 3 seconds)).mkString(",")
      def prettify(rawStr: String) = Json.prettyPrint(Json.parse(rawStr))
      val raw = {
        if(urls.length > 1) {
          val start = """{"menus":["""
          val end = "]}"
          prettify(start + result + end)
        } else {
          prettify(result)
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

  private def loadFazerMenus(restaurant: Restaurant): Unit = {
    val menusOption = getRawMenus(restaurant)
    menusOption match {
      case Some(menu) => {
        println(s"Found menu for restaurant '${restaurant.name}'")
        val json = Json.parse(menu)
        val menus: ListBuffer[Menu] = new ListBuffer[Menu]()
        val menusObjects = (json \ "MenusForDays").as[List[JsObject]]
        for(menu <- menusObjects) {
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

  private def loadSodexoMenus(restaurant: Restaurant): Unit = {
    val menusOption = getRawMenus(restaurant)
      menusOption match {
      case Some(menu) => {
        println(s"Found menu for restaurant '${restaurant.name}'")
        val json = Json.parse(menu)
        val menus: ListBuffer[Menu] = new ListBuffer[Menu]()
        val menusObjects = (json \ "menus").as[List[JsObject]]
        for(menu <- menusObjects) {
          val foodsBuffer = new ListBuffer[Food]()
          val foodsObjects = (menu \ "courses").as[List[JsObject]]
          for(food <- foodsObjects) {
            foodsBuffer += Food(s"lunch ${foodsBuffer.length}", List(Component(food("title_en").as[String])))
          }
          menus += new Menu(foodsBuffer.toList)
        }
        restaurant.setMenus(menus.toList)
      }
      case None => println(s"Wasn't able to fetch menu for restaurant '${restaurant.name}'")
    }
  }

  def loadMenus(restaurant: Restaurant): Unit = {
    restaurant match {
      case fazer: FazerRestaurant =>
        loadFazerMenus(restaurant)
      case sodexo: SodexoRestaurant =>
        loadSodexoMenus(restaurant)
    }
  }
}