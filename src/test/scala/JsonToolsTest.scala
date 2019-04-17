import com.lunchlist.restaurant.Restaurant
import com.lunchlist.util.JsonTools
import com.lunchlist.util.Misc.defaultConfig

import org.scalatest.{ FlatSpec, PrivateMethodTester }
import org.scalatest.PrivateMethodTester._

class JsonToolsTest extends FlatSpec with PrivateMethodTester {

  "JsonTools function getRestaurantsFromConfig(rawStr)" should "return a correct List of Restaurant objects" in {
    val getRestaurantsFromConfig = PrivateMethod[List[Restaurant]]('getRestaurantsFromConfig)
    val restaurants = JsonTools invokePrivate getRestaurantsFromConfig(defaultConfig)
    assert(restaurants.exists(_.name == "A Bloc"))
    assert(restaurants.exists(_.name == "Dipoli"))
    assert(restaurants.exists(_.name == "Alvari"))
    assert(restaurants.exists(_.name == "TUAS"))
    assert(restaurants.exists(_.name == "Aalto Tietotekniikantalo"))
    assert(restaurants.exists(_.name == "Aalto Valimo"))
    assert(restaurants.exists(_.name == "Aalto Konetekniikka"))
  }

}