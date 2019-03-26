package com.lunchlist.app.gui

import javafx.embed.swing.JFXPanel

import scalafx.application.Platform
import scalafx.geometry.Insets
import scalafx.stage.Stage
import scalafx.scene.Scene
import scalafx.scene.paint.Color._
import scalafx.scene.control.Label
import scalafx.scene.layout.{TilePane, GridPane}

import com.lunchlist.restaurant._
import com.lunchlist.util.DateTools.getDate

class LunchListView(val restaurants: List[Restaurant]) extends Stage {
  title = "Otaniemi lunch list - " + getDate("dd/MM/yyyy")
  scene = new Scene {
    root = new TilePane {
      children = restaurants.map(r => 
        new GridPane {
          add(new Label(r.name), 0, 0)
          add(new Label(r.getTodaysMenu().getOrElse("No menu for this day").toString()), 0, 1)
        }
      )
    }
  }
}

object LunchListView {
  def start(restaurants: List[Restaurant]) = {
    new JFXPanel()
    Platform.runLater({
      val view = new LunchListView(restaurants)
      view.showAndWait()
      Platform.exit()
    })
  }
}