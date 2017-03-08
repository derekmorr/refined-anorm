package refined.anorm.types

import eu.timepit.refined.W
import eu.timepit.refined.api.Refined
import eu.timepit.refined.boolean.{And, Not}
import eu.timepit.refined.char.Whitespace
import eu.timepit.refined.collection.{Exists, NonEmpty}
import eu.timepit.refined.numeric.Interval

/**
  * Custom data types
  */
object Types {

  /** 802.1Q Vlan ID */
  type VlanIdRange = Interval.Closed[W.`0`.T, W.`4095`.T]
  type VlanId = Int Refined VlanIdRange

  /** Non-blank string */
  type NonBlank = And[NonEmpty, Exists[Not[Whitespace]]]
  type NonBlankString = String Refined NonBlank
}
