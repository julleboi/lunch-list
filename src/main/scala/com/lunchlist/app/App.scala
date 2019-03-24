package com.lunchlist.app

import scala.concurrent._
import scala.concurrent.duration._
import ExecutionContext.Implicits.global

import com.lunchlist.restaurant._
import com.lunchlist.util.JsonTools.loadRestaurants
import com.lunchlist.util.JsonTools.loadMenus

object App {
  
  val restaurants: List[Restaurant] = loadRestaurants()

  trait Command {
    def apply(options: String = null): Unit
  }

  def loadAllMenus = restaurants.map(r => Future{loadMenus(r)}).foreach(Await.result(_, 3 seconds))
  val load: Command = _ => loadAllMenus

  def printAvailableActions() = {
    println("Abvailable commands:")
    println(inputToCommand.keys.map("-"+_).reduceLeft(_ + ", " + _))
  }
  val printCommands: Command = _ => printAvailableActions

  val printUsage: Command = _ => println("Usage: '-noOptions -withOptions=<options for this command> ...'\nCommands are executed in order.")

  def terminate() = {
    print("Invalid command, ")
    printCommands()
    println("Terminating.")
    sys.exit(1)
  }
  val invalid: Command = _ => terminate

  val inputToCommand: Map[String, Command] = Map(
    "load"     -> load,
    "commands" -> printCommands,
    "usage"    -> printUsage
  ).withDefaultValue(invalid)

  def handleArgs(args: Array[String]): Unit = {
    val argsSplit = args.foldLeft(" ")(_ + " " + _).split(" -").tail
    if(argsSplit.isEmpty){
      println("No actions specified.")
      printUsage()
      printCommands()
    }
    for(command <- argsSplit) {
      val commandSplit = command.split("=")
      if(commandSplit.length < 2) {
        val commandName = command.toLowerCase()
        inputToCommand(commandName)()
      } else {
        val commandName = commandSplit.head.toLowerCase()
        val options = commandSplit.tail.reduceLeft(_ + _)
        inputToCommand(commandName)(options)
      }
    }
  }

  def main(args: Array[String]): Unit = {
    handleArgs(args)
  }
}