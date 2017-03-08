package refined.anorm

import _root_.anorm.SqlParser.scalar
import _root_.anorm.{AnormException, SQL}

import acolyte.jdbc.AcolyteDSL.{connection, handleQuery}
import acolyte.jdbc.Implicits._
import acolyte.jdbc.QueryResult
import acolyte.jdbc.RowLists._
import eu.timepit.refined._
import eu.timepit.refined.api.Refined
import eu.timepit.refined.numeric._
import org.scalacheck.Gen.choose
import refined.anorm._

/**
  * Tests for refined-anorm
  */

class RefinedAnormTest extends BaseTest {

  /** Sample refined type for tests. An 802.1Q VLAN identifier. */
  type VlanIdRange = Interval.Closed[W.`0`.T, W.`4095`.T]
  type VlanId = Int Refined VlanIdRange

  def withQueryResult[A](r: QueryResult)(f: java.sql.Connection => A): A =
    f(connection(handleQuery { _ => r }))

  "RefinedAnorm" must {

    "support Column mapped as VLanId" which {

      "reads data from the database" which {
        "validates when the int is range" in {
          forAll(choose(0, 4095)) { intId =>
            withQueryResult(intList :+ intId) { implicit con =>
              val expected: VlanId = refineV[VlanIdRange](intId).right.get
              SQL("SELECT vlanId").as(scalar[VlanId].single) must === (expected)
            }
          }
        }

        "rejects the record" when {
          "the int is out of range" in {
            forAll { invalidId: Int =>
              whenever(invalidId < 0 || invalidId > 4095) {
                withQueryResult(intList :+ invalidId) { implicit con =>
                  val ex = the[AnormException] thrownBy SQL("SELECT vlanId").as(scalar[VlanId].single)
                  ex.getMessage must include("TypeDoesNotMatch")
                  ex.getMessage must include("does not satisfy refinement predicate")
                }
              }
            }
          }

          "the base type is invalid" in {
            withQueryResult(booleanList :+ true) { implicit con =>
              val ex = the[AnormException] thrownBy SQL("SELECT vlanId").as(scalar[VlanId].single)
              ex.getMessage must include("TypeDoesNotMatch")
            }
          }
        }
      }
    }
  }
}
