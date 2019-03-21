package com.lunchlist.app

import com.lunchlist.restaurant._
import com.lunchlist.util.JsonTools.loadRestaurants
import com.lunchlist.util.JsonTools.loadMenu

object App {
  
  val restaurants: List[Restaurant] = loadRestaurants()

  trait Command {
    def apply(options: String = null): Unit
  }

  val loadMenus: Command = _ => restaurants.foreach(loadMenu)

  def printAvailableActions() = {
    println("Abvailable actions:")
    println(inputToCommand.keys.map("-"+_).reduceLeft(_ + ", " + _))
  }
  val printCommands: Command = _ => printAvailableActions

  val printUsage: Command = _ => println("Usage: '-noOptions -withOptions=<options for this command> ...'")

  def terminate() = {
    print("Invalid command, ")
    printCommands()
    println("Terminating.")
    sys.exit(1)
  }
  val invalid: Command = _ => terminate

  val inputToCommand: Map[String, Command] = Map(
    "load"     -> loadMenus,
    "commands" -> printCommands,
    "usage"    -> printUsage
  ).withDefaultValue(invalid)

  /*
   * Parses  user input and runs the corresponding command
   */
  def handleArgs(args: Array[String]): Unit = {
    val argsSplit = args.foldLeft(" ")(_ + " " + _).split(" -").tail
    if(argsSplit.isEmpty){
      // No actions were specified to be run
      println("No actions specified.")
      printUsage()
      printCommands()
    }
    for(command <- argsSplit) {
      val commandSplit = command.split("=")
      if(commandSplit.length < 2) {
        // No options for this command, run it as it's
        val commandName = command.toLowerCase()
        inputToCommand(commandName)()
      } else {
        // Give options as parameter for this command
        val commandName = commandSplit.head.toLowerCase()
        val options = commandSplit.tail.reduceLeft(_ + _) // reduceLeft is sort of unnecessary, 
                                                          // but makes it fail-safe in case of multiple '='s
        inputToCommand(commandName)(options)
      }
    }
  }

  /*
   * Main method
   */
  def main(args: Array[String]): Unit = {
    handleArgs(args)
  }
}