package com.lunchlist.util

import System.currentTimeMillis
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit
import java.text.SimpleDateFormat

object DateTools {

  private val time = currentTimeMillis()
  private val date_ = new Date(time)
  private val locale = new Locale("fi", "FI")

  def getDate(format: String = "yyyy/MM/dd", date: Date = date_) = new SimpleDateFormat(format, locale).format(date)
  
  def getWeek() = getDate("w")
  
  def getDatesThisWeek(format: String = "yyyy/MM/dd"): List[String] = {
    val dayInMs = TimeUnit.DAYS.toMillis(1)
    val n = dayInMs * Math.max(getDate("u").toInt - 1, 0)
    val start = time - n
    (start to start + (6 * (dayInMs)) by dayInMs).map((time: Long) => getDate(format, new Date(time))).toList
  }

}