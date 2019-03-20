package com.lunchlist.app

import com.lunchlist.restaurant._
import com.lunchlist.util.JsonTools.loadRestaurants
import com.lunchlist.util.JsonTools.loadMenu

object App {
  val restaurants: List[Restaurant] = loadRestaurants()

  trait Action {
    def apply(options: String = null): Unit
  }

  /*
   * ----------------------------
   * --- START OF ALL ACTIONS ---
   * ----------------------------
   */
  // Download menus action
  val loadMenus: Action = _ => restaurants.foreach(loadMenu)

  // Print actions action
  def printAvailableActions() = {
    println("Abvailable actions:")
    println(s"${inputToAction.keys.map("-"+_).reduceLeft(_ + ", " + _)}")
  }
  val printActions: Action = _ => printAvailableActions

  // Usage action
  val printUsage: Action = _ => println("Usage: '-noOptions -withOptions=<options for this action> ...'")

  // Invalid action
  def terminate() = {
    print("Invalid action, ")
    printActions()
    println("Terminating.")
    sys.exit(1)
  }
  val invalid: Action = _ => terminate
  /*
   * --------------------------
   * --- END OF ALL ACTIONS ---
   * --------------------------
   */

  /*
   * Maps input strings to corresponding actions
   */
  val inputToAction: Map[String, Action] = Map(
    "load"   -> loadMenus,
    "actions" -> printActions,
    "usage"   -> printUsage
  ).withDefaultValue(invalid)

  /*
   * Parses  user input and runs the corresponding action
   */
  def handleArgs(args: Array[String]): Unit = {
    val argsSplit = args.foldLeft(" ")(_ + " " + _).split(" -").tail
    // DEBUG: argsSplit.foreach(arg => println(s"found arg: '$arg'"))
    if(argsSplit.isEmpty){
      // No actions were specified to be run
      println("No actions specified.")
      printUsage()
      printActions()
    }
    for(action <- argsSplit) {
      val actionSplit = action.split("=")
      if(actionSplit.length < 2) {
        // No options for this action, run it as it's
        val actionName = action.toLowerCase()
        inputToAction(actionName)()
      } else {
        // Give options as parameter for this action
        val actionName = actionSplit.head.toLowerCase()
        val options = actionSplit.tail.reduceLeft(_ + _) // reduceLeft is sort of unnecessary, 
                                                         // but makes it fail-safe in case of multiple '='s
        inputToAction(actionName)(options)
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