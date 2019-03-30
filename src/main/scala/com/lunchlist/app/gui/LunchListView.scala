package com.lunchlist.app.gui

import javafx.embed.swing.JFXPanel

import scalafx.application.Platform
import scalafx.beans.property.BooleanProperty
import scalafx.beans.property.BooleanProperty._
import scalafx.beans.property.StringProperty
import scalafx.beans.property.StringProperty._
import scalafx.geometry.{Insets, Pos}
import scalafx.Includes._
import scalafx.stage.Stage
import scalafx.scene.Scene
import scalafx.scene.text.Font
import scalafx.scene.control.{ScrollPane, Label, Button}
import scalafx.scene.layout.{BorderPane, TilePane, HBox, VBox}

import com.lunchlist.restaurant._
import com.lunchlist.util.DateTools.{getDate, getDay, getWeek}
import com.lunchlist.util.JsonTools.setFavorites

class LunchListView(private val restaurants: List[Restaurant]) extends Stage {

  private val days = 
    List("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")
      .dropWhile(_ != getDay())
  private val day = StringProperty(getDay())

  private val restaurantByName: Map[String, Restaurant] =
    restaurants.map(r => (r.name, r))
      .toMap
  
  private val isVisible: Map[String, BooleanProperty] = 
    restaurants.map(r => (r.name, BooleanProperty(true)))
      .toMap
        .withDefaultValue(BooleanProperty(false))

  private val isFavorite: Map[String, BooleanProperty] = 
    restaurants.map(r => (r.name, BooleanProperty(r.isFavorite())))
      .toMap
        .withDefaultValue(BooleanProperty(false))

  private def getMenu(r: Restaurant): String = 
    r.getMenus()
      .find(menu => menu.day == day.get)
        .map(_.toString())
          .getOrElse("")
  private val menuText: Map[String, StringProperty] = 
    restaurants.map(r => (r.name, StringProperty(getMenu(r))))
      .toMap
        .withDefaultValue(StringProperty(""))
  
  title = "Otaniemi lunch list - Week " + getWeek()
  height = 800
  scene = new Scene {
    root = new BorderPane {
      style =
        """
        -fx-font-family: 'Montserrat Light', sans-serif;
        -fx-box-border: transparent;
        """
      top = new VBox {
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
            margin = Insets(10, 0, 10, 0)
            spacing = 5
            children = days.map((d: String) => {
              val cb = () => {
                day.set(d)
                for((key, value) <- menuText) {
                  val restaurant = restaurantByName(key)
                  val menu = getMenu(restaurant)
                  value.set(menu)
                }
              }
              new Btn(cb, d)
            })
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
      val style_ = 
        """
        -fx-background-radius: 5;
        -fx-effect: dropshadow(three-pass-box, rgba(0, 0, 0, 0.3), 5, 0, 0, 0);
        """
      val notFavorite = style_.concat("-fx-background-color: #ffffff;")
      val favorite = style_.concat("-fx-background-color: #ffffee")
      style <== when(isFavorite(restaurant.name)) choose favorite otherwise notFavorite
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
          val isFav = restaurant.isFavorite
          restaurant.setFavorite(!isFav)
          isFavorite(restaurant.name).set(!isFav)
        }
        children = Seq(
          new Btn(hideBtnCb, "🚫"),
          new Btn(favBtnCb, "❤️")
        )
      }
    }
  }

  private class Btn(cb: () => Unit, name: String) extends Button {
    val style_ = 
      """
      -fx-background-radius: 3;
      -fx-border-radius: 3;
      -fx-border-color: #aaaaaa;
      """
    val notHovered = style_.concat("-fx-background-color: #ffffff;")
    val hovered = style_.concat("-fx-background-color: #dddddd;")
    val s = StringProperty(notHovered)
    style <== s
    text = name
    onMouseEntered = _ => s.set(hovered)
    onMouseExited = _ => s.set(notHovered)
    onAction = _ => cb()
  }

}

object LunchListView {
  def start(restaurants: List[Restaurant]) = {
    new JFXPanel()

    // to fix blurry fonts
    System.setProperty("prism.lcdtext", "false")

    // load custom font
    Font.loadFont(new java.io.FileInputStream("./data/montserrat-light.ttf"), 14)
    
    Platform.runLater({
      val view = new LunchListView(restaurants)
      view.showAndWait()
      setFavorites(restaurants)
      Platform.exit()
    })
  }
}