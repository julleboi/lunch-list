package com.lunchlist.restaurant.menu

case class Food(val name: String, val components: List[Component]) {

  def containsFilteredProps(filteredProps: List[Property]): Boolean = {
    val props = this.components.map(_.properties)
    val existsOnProps = (prop: Property) => props.forall(_.contains(prop))
    val appearsInName = (prop: Property) => this.name.toLowerCase.contains(prop.name)
    filteredProps.forall((p: Property) => existsOnProps(p) || appearsInName(p))
  }
  
}