package com.lunchlist.app

import scala.concurrent._
import scala.concurrent.duration._
import ExecutionContext.Implicits.global

import com.lunchlist.app.gui.LunchListView
import com.lunchlist.restaurant._
import com.lunchlist.util.JsonTools.{getRestaurants, loadMenus}

object App {
  
  val restaurants: List[Restaurant] = getRestaurants()
  var launchGUI: Boolean = false

  trait Command {
    def apply(options: String = null): Unit
  }

  def guiCb = launchGUI = true
  val gui: Command = _ => guiCb

  def printCommandsCb() = {
    println("Abvailable commands:\n" + inputToCommand.keys.map("-" + _).mkString(", "))
    sys.exit(0)
  }
  val printCommands: Command = _ => printCommandsCb

  def printUsageCb() = {
    println("Usage: '-noOptions -withOptions=<options for this command> ...'")
    sys.exit(0)
  }
  val printUsage: Command = _ => printUsageCb

  def invalidCb() = {
    print("Invalid command, ")
    printCommands()
  }
  val invalid: Command = _ => invalidCb

  val inputToCommand: Map[String, Command] = Map(
    "gui"      -> gui,
    "commands" -> printCommands,
    "usage"    -> printUsage
  ).withDefaultValue(invalid)

  def handleArgs(args: Array[String]): Unit = {
    val argsSplit = 
      args
        .mkString
        .split("-")
        .filter(_ != "")
    for(command <- argsSplit) {
      val commandSplit = command.split("=")
      if(commandSplit.length < 2) {
        val commandName = command.toLowerCase
        inputToCommand(commandName)()
      } else {
        val commandName = commandSplit.head.toLowerCase
        val options = commandSplit.tail.mkString
        inputToCommand(commandName)(options)
      }
    }
  }

  def main(args: Array[String]): Unit = {
    handleArgs(args)
    loadMenus(restaurants)
    if(launchGUI)
      LunchListView.start(restaurants)
    else
      restaurants.foreach(println)
  }
}