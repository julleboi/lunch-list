import com.lunchlist.util.DateTools.{getDay}

import org.scalatest._

class DateToolsTest extends FlatSpec {
  
  "DateTools.getDay(date: java.util.Date)" should "return 'Thursday' with Date(01/01/1970)" in {
    val dateObject = new java.util.Date(0L)
    assert(getDay(dateObject) == "Thursday")
  }

}