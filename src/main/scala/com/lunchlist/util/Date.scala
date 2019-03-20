package com.lunchlist.util

import System.currentTimeMillis
import java.util._
import java.text.SimpleDateFormat

object Date {
  private val time = currentTimeMillis()
  private val date = new Date(time)
  def getDate(format: String = "yyyy/MM/dd") = new SimpleDateFormat(format).format(date)
  def getWeek() = new SimpleDateFormat("w").format(date)
}