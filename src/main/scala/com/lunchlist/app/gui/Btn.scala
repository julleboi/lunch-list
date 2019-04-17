package com.lunchlist.app.gui

import scalafx.beans.property.StringProperty
import scalafx.beans.property.StringProperty._
import scalafx.scene.control.Button

private class Btn(cb: () => Unit, name: String, toggle: Option[Boolean] = None) extends Button {
  
  private val isToggleable = toggle.isDefined
  private var toggled = toggle.getOrElse(false)
  private def isToggled = isToggleable && toggled

  private val style_ = 
    """
    -fx-background-radius: 3;
    -fx-border-radius: 3;
    -fx-border-color: #aaaaaa;
    """

  private val normalStyle = style_.concat("-fx-background-color: #ffffff;")
  private val hoveredStyle = style_.concat("-fx-background-color: #dddddd;")
  private val toggledStyle = style_.concat("-fx-background-color: #cccccc;")

  private val s = StringProperty(if(isToggled) toggledStyle else normalStyle)

  style <== s
  text = name
  
  onMouseEntered = _ => s.set(hoveredStyle)
  onMouseExited = _ => s.set(if(isToggled) toggledStyle else normalStyle)
  onAction = _ => {
    cb()
    if(isToggleable)
      toggled = !toggled
  }

}