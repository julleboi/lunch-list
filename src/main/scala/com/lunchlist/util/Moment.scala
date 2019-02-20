package com.lunchlist.util

import System.currentTimeMillis
import java.util.Date
import java.text.SimpleDateFormat

object Moment {
  private val time: Long = currentTimeMillis()
  private val date: Date = new Date(time)
  def getDate(format: String) = new SimpleDateFormat(format).format(date)
}