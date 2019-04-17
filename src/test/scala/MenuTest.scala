import com.lunchlist.restaurant.menu._

import org.scalatest._

class MenuTest extends FlatSpec {

  val steakLunch = Food("Steak lunch", List[Component](Component("Rib-Eye steak (G, M, L)"), Component("Potatoes (G, M, L)")))
  val veganLunch = Food("Vegan lunch", List[Component](Component("Vegan pizza (V, M, L)")))
  val dessert = Food("Dessert", List[Component](Component("Chocolate mousse (G)")))
  
  val exFoods = List[Food](
    steakLunch,
    veganLunch,
    dessert
  )

  def exampleFoods: List[Food] = exFoods.map(_.copy())

  "Component method containsFilteredProps(filteredProps)" should "work correctly" in {
    // Steak lunch is milkfree and glutenfree
    assert(exampleFoods(0).containsFilteredProps(List[Property](MilkFree)))

    // Vegan lunch is vegan
    assert(exampleFoods(1).containsFilteredProps(List[Property](Vegan)))

    // Dessert is NOT vegan
    assert(!exampleFoods(2).containsFilteredProps(List[Property](Vegan)))
  }

  "Filtering menu for various components" should "return correctly filtered menus" in {
    val vegan = new Menu("Monday", exampleFoods)
    vegan.filterForProperties(List[Property](Vegan))
    // Foods list should now only contain vegan lunch
    assert(vegan.getFoods().forall(_.name == "Vegan lunch"))

    val glutenfree = new Menu("Tuesday", exampleFoods)
    glutenfree.filterForProperties(List[Property](GlutenFree))
    // foods list should now only contain Steak lunch or Dessert
    assert(glutenfree.getFoods().forall(f => f.name == "Steak lunch" || f.name == "Dessert"))
  }

}