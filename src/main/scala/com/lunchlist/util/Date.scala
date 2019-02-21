package com.lunchlist.util

import System.currentTimeMillis
import java.util._
import java.text.SimpleDateFormat

object Date {
  private val time = currentTimeMillis()
  private val date = new Date(time)
  def getDate(format: String) = new SimpleDateFormat(format).format(date)
}