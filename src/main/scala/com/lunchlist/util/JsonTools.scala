package com.lunchlist.util

import collection.mutable.ListBuffer

import concurrent._
import concurrent.duration._
import ExecutionContext.Implicits.global

import scala.util.Try

import play.api.libs.json._

import com.lunchlist.restaurant._
import com.lunchlist.restaurant.Menu._
import com.lunchlist.util.DateTools.{getDay, stringToDate}
import com.lunchlist.util.Misc.{readFromFile, readFromURL, writeToFile}

object JsonTools {

  private val configFilePath = "./data/configurations.json"
  private lazy val configRaw = readFromFile(configFilePath)

  def loadRestaurants() = restaurantsToList(configRaw)

  def loadMenus(restaurant: Restaurant): Unit = restaurant match {
    case fazer: FazerRestaurant =>
      loadFazerMenus(restaurant)
    case sodexo: SodexoRestaurant =>
      loadSodexoMenus(restaurant)
  }

  private def restaurantsToList(rawStr: String): List[Restaurant] = {
    val json = Json.parse(rawStr)
    val restaurants: ListBuffer[Restaurant] = new ListBuffer()
    val restaurantsObjects = json.as[List[Map[String, String]]]
    for(restaurant <- restaurantsObjects) {
      val rType = restaurant("type")
      val name = restaurant("name")
      val id = restaurant("id")
      val fav = Try(restaurant("favorite").toBoolean).getOrElse(false)
      rType match {
        case "fazer" => { 
          val r = new FazerRestaurant(name, id)
          r.setFavorite(fav)
          restaurants += r
        }
        case "sodexo" => { 
          val r = new SodexoRestaurant(name, id)
          r.setFavorite(fav)
          restaurants += r
        }
        case unknownType => 
          println(s"Cannot create a Restaurant-object for a Restaurant of type '$rType'")
      }
    }
    return restaurants.toList
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
          val dateStr: String = (menu \ "Date").as[String]
          val date = stringToDate(dateStr)
          val day = getDay(date)
          val foodsBuffer = new ListBuffer[Food]()
          val foodsObjects = (menu \ "SetMenus").as[List[JsObject]]
          for(food <- foodsObjects) {
            val title = Try(food("Name").as[String]).getOrElse("Lunch")
            foodsBuffer += new Food(title, food("Components").as[List[String]].map(x => new Component(x)))
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
            val name = "Lunch " + (foodsBuffer.length + 1)
            val title = food("title_en").as[String]
            val properties = Try("("+food("properties").as[String]+")").getOrElse("")
            val components = List(new Component(title+properties))
            foodsBuffer += new Food(name, components)
          }
          menus += new Menu(getDay_(n), foodsBuffer.toList)
          n += 1
        }
        restaurant.setMenus(menus.toList)
      }
      case None => println(s"Wasn't able to fetch menu for restaurant '${restaurant.name}'")
    }
  }

  private def getDay_(n: Int): String = n match {
    case 0 => "Monday"
    case 1 => "Tuesday"
    case 2 => "Wednesday"
    case 3 => "Thursday"
    case 4 => "Friday"
    case 5 => "Saturday"
    case 6 => "Sunday"
    case _ => "INVALID DAY"
  }

  private def getRawMenus(restaurant: Restaurant): Option[String] = {
    if(restaurant.menusFileExists) {
      val raw = readFromFile(restaurant.getMenusFilePath)
      return Some(raw)
    } else {
      val urls = restaurant.getURLs()
      val futures: List[Future[String]] = urls.map(url => Future { readFromURL(url) })
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
      writeToFile(raw, restaurant.getMenusFilePath)
      return Some(raw)
    }
    return None
  }

  def setFavorites(restaurants: List[Restaurant]): Unit = {
    var updateNeeded = false
    val json = Json.parse(configRaw)
    val ids = restaurants.map(_.id)
    def updateFavorite(configs: Map[String, String]): Map[String, String] = {
      val id = configs("id")
      if(ids.contains(id)) {
        updateNeeded = true
        val newValue = restaurants.find(_.id == id).map(_.isFavorite.toString).getOrElse("false")
        configs + ("favorite" -> newValue)
      } else {
        configs
      }
    }
    val updated = json.as[List[Map[String, String]]].map(updateFavorite)
    if(updateNeeded) {
      val newJson = Json.toJson(updated)
      val raw = Json.prettyPrint(newJson)
      writeToFile(raw, configFilePath)
    }
  }
  
}