package refined.anorm

import org.scalactic.TypeCheckedTripleEquals
import org.scalatest._
import org.scalatest.prop.GeneratorDrivenPropertyChecks

/**
  * Base class for tests.
  */
@DoNotDiscover
class BaseTest extends WordSpec
  with MustMatchers
  with GeneratorDrivenPropertyChecks
  with TypeCheckedTripleEquals
  with OptionValues
  with TryValues {

}
