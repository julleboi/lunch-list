package com.lunchlist.app.gui

import javafx.embed.swing.JFXPanel

import scalafx.application.Platform
import scalafx.geometry.{Insets, Pos}
import scalafx.stage.Stage
import scalafx.scene.Scene
import scalafx.scene.text.Font
import scalafx.scene.control.{ScrollPane, Label}
import scalafx.scene.layout.{BorderPane, TilePane}

import com.lunchlist.restaurant._
import com.lunchlist.util.DateTools.getDate

class LunchListView(val restaurants: List[Restaurant]) extends Stage {
  title = "Otaniemi lunch list"
  height = 800
  resizable = false
  scene = new Scene {
    root = new BorderPane {
      style = "-fx-font-family: 'Montserrat Light', sans-serif"
      top = new Label {
        style = "-fx-font-size: 18"
        alignmentInParent = Pos.Center
        text = getDate("dd / MM / yyyy")
      }
      center = new ScrollPane {
        style = "-fx-background-color: #f9f9f9"
        hbarPolicy = ScrollPane.ScrollBarPolicy.Never
        content = new TilePane {
          prefColumns = 2
          padding = Insets(20, 40, 20, 40)
          hgap = 10
          vgap = 10
          children = restaurants.map(r => 
            new BorderPane {
              style = 
                """
                -fx-background-radius: 5;
                -fx-background-color: white;
                -fx-effect: dropshadow(three-pass-box, rgba(0, 0, 0, 0.3), 5, 0, 0, 0);
                """
              padding = Insets(10, 10, 10, 10)
              top = new Label {
                style = "-fx-underline: true"
                padding = Insets(0, 0, 20, 0)
                alignmentInParent = Pos.Center
                text = r.name
              }
              center = new Label {
                alignmentInParent = Pos.TopLeft
                text = r.getTodaysMenu().getOrElse("No menu for this day").toString
              }
            }
          )
        }
      }
    }
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
      Platform.exit()
    })
  }
}