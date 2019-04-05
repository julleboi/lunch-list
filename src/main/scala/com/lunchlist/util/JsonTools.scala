package com.lunchlist.util

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

  def loadRestaurants(): List[Restaurant] = getRestaurantsFromConfig(configRaw)
  def loadMenus(restaurants: List[Restaurant]): Unit = restaurants.foreach(getMenus)

  private def getRestaurantsFromConfig(rawStr: String): List[Restaurant] = {
    val json = Json.parse(rawStr)

    def toRestaurantObject(rType: String, name: String, id: String) = rType match {
      case "fazer" => { 
        new FazerRestaurant(name, id)
      }
      case "sodexo" => { 
        new SodexoRestaurant(name, id)
      }
      case unknownType => 
        UnknownRestaurant
    }

    for(restaurant <- json.as[List[Map[String, JsValue]]]) 
    yield {
      val rType = restaurant("type").as[String]
      val name = restaurant("name").as[String]
      val id = restaurant("id").as[String]
      val fav = restaurant("favorite").as[Boolean]
      val ret = toRestaurantObject(rType, name, id)
      ret.setFavorite(fav)
      ret
    }
  }

  private def getMenus(restaurant: Restaurant): Unit = restaurant match {
    case fazer: FazerRestaurant =>
      loadFazerMenus(restaurant)
    case sodexo: SodexoRestaurant =>
      loadSodexoMenus(restaurant)
  }

  private def loadFazerMenus(restaurant: Restaurant): Unit = {
    val menusOption = getRawMenus(restaurant)
    menusOption match {
      case Some(menu) => {
        println(s"Found menu for restaurant '${restaurant.name}'")
        val json = Json.parse(menu)

        def foodsObjectToFoodList(foods: List[JsObject]): List[Food] = 
          for(food <- foods)
          yield {
            val title = Try(food("Name").as[String]).getOrElse("Lunch")
            val components = for(name <- food("Components").as[List[String]]) yield new Component(name)
            new Food(title, components)
          }

        val menus: List[Menu] = 
          for(menu <- (json \ "MenusForDays").as[List[JsObject]]) yield {
            val foods = (menu \ "SetMenus").as[List[JsObject]]
            val foodsList = foodsObjectToFoodList(foods)
            val dateStr = (menu \ "Date").as[String]
            val date = stringToDate(dateStr)
            val day = getDay(date)
            new Menu(day, foodsList)
          }

        restaurant.setMenus(menus)
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
        val days = List("Monday", "Tuesday", "wednesday", "Thursday", "Friday", "Saturday").iterator

        def foodsObjectToFoodsList(foods: List[JsObject]): List[Food] =
          for(food <- foods)
          yield {
            val title = Try(food("title_en").as[String]).getOrElse("")
            val properties = Try(" ("+food("properties").as[String]+")").getOrElse("")
            val components = List(new Component(title+properties))
            new Food("Lunch", components)
          }

        val menus: List[Menu] = 
          for {
            menu <- (json \ "menus").as[List[JsObject]]
            if days.hasNext
          } yield {
            val foods = (menu \ "courses").as[List[JsObject]]
            val foodsList = foodsObjectToFoodsList(foods)
            new Menu(days.next, foodsList)
          }

        restaurant.setMenus(menus)
      }
      case None => println(s"Wasn't able to fetch menu for restaurant '${restaurant.name}'")
    }
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

    def updateFavorite(configs: Map[String, JsValue]): Map[String, JsValue] = {
      val id = configs("id").as[String]
      val isFav = configs("favorite").as[Boolean]
      if(ids.contains(id)) {
        val newValue = (restaurants.find(_.id == id).map(_.isFavorite).getOrElse(false))
        if(newValue != isFav)
          return configs + ("favorite" -> Json.toJson(newValue))
      }
      configs
    }

    val updated = json.as[List[Map[String, JsValue]]].map(updateFavorite)
    if(updateNeeded) {
      val newJson = Json.toJson(updated)
      val raw = Json.prettyPrint(newJson)
      writeToFile(raw, configFilePath)
    }
  }
  
}