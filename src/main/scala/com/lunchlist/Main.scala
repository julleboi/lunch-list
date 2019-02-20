package com.lunchlist

object Main {
  trait Action {
    def apply(options: String = null): Unit
  }

  /*
   * ----------------------------
   * --- START OF ALL ACTIONS ---
   * ----------------------------
   */
  // Print time action
  val printTime: Action = _ => println(s"Time: ${System.currentTimeMillis()}ms")

  // Wait action
  val wait100ms: Action = _ => Thread.sleep(100)

  // Print action
  val printText: Action = str => println(str)

  // Count action
  def countTo(num: String) = {
    try {
      val n = num.toInt
      for(number <- (1 to n)) {
        wait100ms()
        println(number)
      }
    } catch {
      case e: Exception => {
        println(s"Invalid value '$num'")
        e.printStackTrace()
      }
    }
  }
  val count: Action = str => countTo(str)

  // Print actions action
  def printAvailableActions() = {
    println("Abvailable actions:")
    println(s"${inputToAction.keys.map("-"+_).reduceLeft(_ + ", " + _)}")
  }
  val actions: Action = _ => printAvailableActions

  // Invalid action
  def terminate() = {
    print("Invalid action, ")
    printAvailableActions()
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
    "time"    -> printTime,
    "wait"    -> wait100ms,
    "print"   -> printText,
    "count"   -> count,
    "actions" -> actions
  ).withDefaultValue(invalid)

  /*
   * Parses  user input and runs the corresponding action
   */
  def handleArgs(args: Array[String]): Unit = {
    val argsSplit = args.foldLeft(" ")(_ + " " + _).split(" -").tail
    // DEBUG: argsSplit.foreach(arg => println(s"found arg: '$arg'"))
    if(argsSplit.isEmpty){
      // No actions were specified to be run
      println("No actions specified, usage: '-noOptions -withOptions=<options for this action> ...'")
      printAvailableActions()
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
    handleArgs(args);
  }
}