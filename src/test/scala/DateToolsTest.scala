import com.lunchlist.util.DateTools.{ getDay, getDatesThisWeek, stringToDate, getDate }

import org.scalatest._

class DateToolsTest extends FlatSpec {
  
  "getDay(date)" should "return 'Thursday' with Date(01/01/1970)" in {
    val dateObject = new java.util.Date(0L)
    assert(getDay(dateObject) == "Thursday")
  }

  "getDatesThisWeek()" should "return List[String] of length 7" in {
    assert(getDatesThisWeek().length == 7)
  }

  """stringToDate("1970-01-01")""" should "return a correct Date object" in {
    assert(getDate("yyyy/MM/dd", stringToDate("1970-01-01")) == getDate("yyyy/MM/dd", new java.util.Date(0)))
  }

}