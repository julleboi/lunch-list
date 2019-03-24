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
import com.lunchlist.util.DateTools._

object JsonTools {

  private def readFromFile(path: String): String = Source.fromFile(path).mkString

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
      val raw = readFromFile(restaurant.getMenuFilePath())
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

  private def getDay(n: Int): String = n match {
    case 0 => "Monday"
    case 1 => "Tuesday"
    case 2 => "Wednesday"
    case 3 => "Thursday"
    case 4 => "Friday"
    case 5 => "Saturday"
    case 6 => "Sunday"
    case _ => "INVALID DAY"
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
          val day = getDate("EEEE", stringToDate((menu \ "Date").as[String]))
          val foodsBuffer = new ListBuffer[Food]()
          val foodsObjects = (menu \ "SetMenus").as[List[JsObject]]
          for(food <- foodsObjects) {
            foodsBuffer += Food(s"lunch ${foodsBuffer.length}", food("Components").as[List[String]].map(x => Component(x)))
          }
          menus += new Menu(day, foodsBuffer.toList)
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
        var n = 0;
        for(menu <- menusObjects) {
          val foodsBuffer = new ListBuffer[Food]()
          val foodsObjects = (menu \ "courses").as[List[JsObject]]
          for(food <- foodsObjects) {
            foodsBuffer += Food(s"lunch ${foodsBuffer.length}", List(Component(food("title_en").as[String])))
          }
          menus += new Menu(getDay(n), foodsBuffer.toList)
          n += 1
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