package com.lunchlist.util

import io.Source
import util.Try

import java.io.PrintWriter

object Misc {

  def readFromFile(path: String): Option[String] = 
    getFilePath(path).map(path => {
      val f = Source.fromFile(path)
      val ret = f.mkString
      f.close
      ret
    })

  def readFromURL(url: String): Option[String] = 
    Try {
      val f = Source.fromURL(url)
      val ret = f.mkString
      f.close
      ret
    } toOption

  def writeToFile(toWrite: String, path: String): Unit = 
    getFilePath(path).foreach(path => {
      new PrintWriter(path) {
        write(toWrite)
        close
      }
    })

  private val path: String = System.getProperty("user.home") + "/.lunch-list/"
  
  def fileExists(suffix: String): Boolean = new java.io.File(path + suffix).exists

  private def getFilePath(suffix: String): Option[String] = 
    Try {
      val fullPath = path + suffix
      val file = new java.io.File(fullPath)
      if(!file.exists) {
        if(!file.getParentFile.exists)
          file.getParentFile.mkdirs
        file.createNewFile()
        if(suffix == configFileName)
          writeToFile(defaultConfig, configFileName)
      }
      fullPath
    } toOption


  val configFileName: String = "configurations.json"

  private lazy val defaultConfig = 
"""[ {
  "type" : "fazer",
  "name" : "A Bloc",
  "id" : "3087",
  "favorite" : false
}, {
  "type" : "fazer",
  "name" : "Dipoli",
  "id" : "3101",
  "favorite" : false
}, {
  "type" : "fazer",
  "name" : "Alvari",
  "id" : "0190",
  "favorite" : false
}, {
  "type" : "fazer",
  "name" : "TUAS",
  "id" : "0199",
  "favorite" : false
}, {
  "type" : "sodexo",
  "name" : "Aalto Tietotekniikantalo",
  "id" : "142",
  "favorite" : false
}, {
  "type" : "sodexo",
  "name" : "Aalto Valimo",
  "id" : "13918",
  "favorite" : false
}, {
  "type" : "sodexo",
  "name" : "Aalto Konetekniikka",
  "id" : "140",
  "favorite" : false
} ]"""

}