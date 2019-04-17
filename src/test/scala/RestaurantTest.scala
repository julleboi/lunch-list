import com.lunchlist.restaurant._
import com.lunchlist.util.JsonTools
import com.lunchlist.util.Misc.defaultConfig

import scala.collection.JavaConverters._

import java.net.{ InetAddress, URL, URLConnection }
import java.util.Map

import org.scalatest.{ FlatSpec, PrivateMethodTester }
import org.scalatest.PrivateMethodTester._

class RestaurantTest extends FlatSpec {

  val getRestaurantsFromConfig = PrivateMethod[List[Restaurant]]('getRestaurantsFromConfig)
  val restaurants: List[Restaurant] = JsonTools invokePrivate getRestaurantsFromConfig(defaultConfig)

  def isReachable(url: String): Boolean = InetAddress.getByName(url).isReachable(6000)

  def isFound(url: String): Boolean = {
    val conn = new URL(url).openConnection()
    val head = conn.getHeaderFields.asScala
    val respType = head(null).asScala
    respType.contains("HTTP/1.1 200 OK")
  }

  "Method getURLS() for type Fazer" should "return URLs which are of correct style for menu-Jsons to be fetched" in {
    val restaurant = 
      restaurants
        .find(
          _ match {
            case f: FazerRestaurant => true
            case _ => false
          }
        )

    restaurant match {
      case Some(r) => {
        if(isReachable("www.fazerfoodco.fi")) {
          assert(r.getURLs.forall(isFound(_)))
        }
      }
      case None => assert(false)
    }
  }

  "Method getURLS() for type Sodexo" should "return URLs which are of correct style for menu-Jsons to be fetched" in {
    val restaurant = 
      restaurants
        .find(
          _ match {
            case s: SodexoRestaurant => true
            case _ => false
          }
        )

    restaurant match {
      case Some(r) => {
        if(isReachable("www.sodexo.fi")) {
          assert(r.getURLs.forall(isFound(_)))
        }
      }
      case None => assert(false)
    }
  }

}