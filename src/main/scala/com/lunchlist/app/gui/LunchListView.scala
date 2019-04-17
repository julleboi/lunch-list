package com.lunchlist.app.gui

import javafx.embed.swing.JFXPanel

import collection.mutable.ListBuffer

import scalafx.application.Platform
import scalafx.beans.property.BooleanProperty
import scalafx.beans.property.BooleanProperty._
import scalafx.beans.property.StringProperty
import scalafx.beans.property.StringProperty._
import scalafx.beans.property.ObjectProperty
import scalafx.beans.property.ObjectProperty._
import scalafx.geometry.{Insets, Pos}
import scalafx.stage.Stage
import scalafx.scene.Scene
import scalafx.scene.text.Font
import scalafx.scene.control.{ScrollPane, Label, TextField}
import scalafx.scene.layout.{BorderPane, TilePane, HBox, VBox}

import com.lunchlist.restaurant._
import com.lunchlist.restaurant.Menu._
import com.lunchlist.util.DateTools.{getDate, getDay, getWeek}
import com.lunchlist.util.JsonTools.setFavorites

class LunchListView(private val restaurants: List[Restaurant]) extends Stage {

  private val days: List[String] = 
    List("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")
      .dropWhile(_ != getDay())
  private val day = StringProperty(getDay())
  
  private val restaurantByName: Map[String, Restaurant] =
    restaurants
      .map(r => (r.name, r))
      .toMap
  
  private val isVisible: Map[String, BooleanProperty] = 
    restaurants
      .map(r => (r.name, BooleanProperty(true)))
      .toMap
      .withDefaultValue(BooleanProperty(false))

  private val isFavorite: Map[String, BooleanProperty] = 
    restaurants
      .map(r => (r.name, BooleanProperty(r.isFavorite())))
      .toMap
      .withDefaultValue(BooleanProperty(false))

  private def getMenu(r: Restaurant): String = 
    r.getMenus()
      .find(menu => menu.day == day.get)
      .map(_.toString())
      .getOrElse("")
  private val menuText: Map[String, StringProperty] = 
    restaurants
      .map(r => (r.name, StringProperty(getMenu(r))))
      .toMap
      .withDefaultValue(StringProperty(""))
  private def updateMenus(): Unit = {
    for((key, value) <- menuText) {
      val restaurant = restaurantByName(key)
      val menu = getMenu(restaurant)
      if(menu.trim.nonEmpty) {
        isVisible(key).set(true)
      } else {
        isVisible(key).set(false)
      }
      if(menu != value)
        value.set(menu)
    }
  }

  private val searchField: StringProperty = StringProperty("")

  private val filtered = ListBuffer[Property]()
  private def filterProperty(prop: Property): Unit = {
    if(filtered.contains(prop)) {
      filtered -= prop
    } else {
      filtered += prop
    }
  }
  
  title = "Otaniemi lunch list - Week " + getWeek()
  height = 800
  resizable = false
  scene = new Scene {
    root = new BorderPane {
      style =
        """
        -fx-font-family: 'Montserrat Light', sans-serif;
        -fx-box-border: transparent;
        """
      top = new VBox {
        style =
          """
          -fx-border-color: #cccccc;
          -fx-border-width: 0 0 1 0;
          """
        children = Seq(
          new HBox {
            alignment = Pos.Center
            children = Seq(
              new Label {
                style = "-fx-font-size: 18"
                text <== day
              },
            )
          },
          new HBox {
            alignment = Pos.Center
            padding = Insets(5, 0, 5, 0)
            spacing = 5
            children = days.map((d: String) => {
              val cb = () => {
                day.set(d)
                updateMenus()
              }
              new Btn(cb, d)
            })
          },
          new HBox {
            padding = Insets(0, 0, 5, 40)
            spacing = 5
            val filterCb = (prop: Property) => {
              filterProperty(prop)
              for{
                restaurant <- restaurants
                menu <- restaurant.getMenus()
              }{
                menu.filterForProperties(filtered.toList)
              }
              updateMenus()
            }
            val vegCb = () => filterCb(Vegan)
            val vgtCb = () => filterCb(Vegetarian)
            val mlkCb = () => filterCb(MilkFree)
            val lacCb = () => filterCb(LactoseFree)
            val gluCb = () => filterCb(GlutenFree)
            children = Seq(
              new TextField {
                val prompt = "search for foods containing this (e.g. fish)"
                prefWidth = prompt.length * 7
                text <==> searchField
                promptText = prompt
                onAction = _ => {
                  println(searchField)
                }
              },
              new Btn(vegCb, "🌿 Vegan", Some(false)),
              new Btn(vgtCb, "Vegetarian", Some(false)),
              new Btn(mlkCb, "🐄 Milk free", Some(false)),
              new Btn(lacCb, "Lactose free", Some(false)),
              new Btn(gluCb, "🌾 Gluten free", Some(false))
            )
          }
        )
      }
      center = new ScrollPane {
        hbarPolicy = ScrollPane.ScrollBarPolicy.Never
        content = new TilePane {
          padding = Insets(10, 40, 10, 40)
          hgap = 10
          vgap = 10
          prefColumns = 3
          updateMenus()
          children = 
            restaurants
              .sortWith(_.isFavorite() && !_.isFavorite())
                .map(menuView)
        }
      }
    }
  }

  private def menuView(restaurant: Restaurant) = {
    new BorderPane {
      style = 
        """
        -fx-background-radius: 5;
        -fx-background-color: #ffffff;
        -fx-effect: dropshadow(three-pass-box, rgba(0, 0, 0, 0.3), 5, 0, 0, 0);
        """
      padding = Insets(10, 10, 10, 10)
      visible <== isVisible(restaurant.name)
      managed <== isVisible(restaurant.name)
      prefWidth = 400
      top = new Label {
        style = "-fx-underline: true"
        padding = Insets(0, 0, 20, 0)
        alignmentInParent = Pos.Center
        text = restaurant.name
      }
      center = new Label {
        alignmentInParent = Pos.TopLeft
        wrapText = true
        text <== menuText(restaurant.name)
      }
      bottom = new HBox {
        alignment = Pos.BottomRight
        margin = Insets(20, 0, 0, 0)
        spacing = 5
        val hideBtnCb = () => isVisible(restaurant.name).set(false)
        val favBtnCb = () => {
          val isFav = restaurant.isFavorite()
          restaurant.setFavorite(!isFav)
          isFavorite(restaurant.name).set(!isFav)
        }
        children = Seq(
          new Btn(hideBtnCb, "🚫"),
          new Btn(favBtnCb, "❤️", Some(restaurant.isFavorite()))
        )
      }
    }
  }

}

object LunchListView {
  def start(restaurants: List[Restaurant]) = {
    new JFXPanel()
    Platform.runLater({
      val view = new LunchListView(restaurants)
      view.showAndWait()
      setFavorites(restaurants)
    })
  }
}