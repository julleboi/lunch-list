package com.lunchlist.util

import System.currentTimeMillis
import java.util.Date
import java.util.concurrent.TimeUnit
import java.text.SimpleDateFormat

object DateTools {
  private val time = currentTimeMillis()
  private val date_ = new Date(time)
  def getDate(format: String = "yyyy/MM/dd", date: Date = date_) = new SimpleDateFormat(format).format(date)
  def getWeek() = getDate("w")
  def getDatesThisWeek(): List[String] = {
    val dayInMs = TimeUnit.DAYS.toMillis(1)
    val n = dayInMs * Math.max(getDate("u").toInt - 1, 0)
    def toDate(n: Long) = new Date(n)
    ((time - n) to (time + (6 * (dayInMs)))).map((time: Long) => getDate("yyyy/MM/dd", new Date(time))).toList
  }
}