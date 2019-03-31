package com.lunchlist.util

import io.Source

import java.io.PrintWriter

object Misc {

  def readFromFile(path: String): String = {
    val f = Source.fromFile(path)
    val ret = f.mkString
    f.close()
    return ret
  }

  def readFromURL(url: String): String = {
    val f = Source.fromURL(url)
    val ret = f.mkString
    f.close()
    return ret
  }

  def writeToFile(toWrite: String, location: String): Unit = {
    new PrintWriter(location) {
      write(toWrite)
      close
    }
  }

}