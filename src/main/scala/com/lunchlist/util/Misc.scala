package com.lunchlist.util

import scala.io.Source
import util.Try

import java.io.{ PrintWriter, File }

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
  
  def clearMenus(): Unit = {
    val menusDir = path+"menus/"
    val menusFolder = new File(menusDir)
    val menusFiles = menusFolder.listFiles
    menusFiles.foreach(f => if(!f.delete) println("Unable to delete "+f.getAbsolutePath))
  }

  def fileExists(suffix: String): Boolean = new File(path + suffix).exists

  private def getFilePath(suffix: String): Option[String] = 
    Try {
      val fullPath = path + suffix
      val file = new File(fullPath)
      if(!file.exists) {
        if(!file.getParentFile.exists)
          file.getParentFile.mkdirs
        file.createNewFile()
        if(suffix == configFileName)
          writeToFile(defaultConfig, configFileName)
      }
      fullPath
    } toOption


  private val path: String = System.getProperty("user.home") + "/.lunch-list/"
  val configFileName: String = "configurations.json"
  lazy val defaultConfig = 
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