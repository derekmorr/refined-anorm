package refined.anorm.models

import java.sql.Connection

import anorm._
import anorm.SqlParser._

import refined.anorm.types.Types.{NonBlankString, VlanId}

/**
  * Information about VLANs.
  */
case class Vlan(id: VlanId, name: NonBlankString)

object Vlan {
  import refined.anorm._
  val parser: RowParser[Vlan] = Macro.namedParser[Vlan]

  // or, if you prefer the old, manual way, you can define parse like this:
  //  val parser: RowParser[Vlan] = {
  //    get[VlanId]("id") ~
  //    get[NonBlankString]("name") map {
  //      case id ~ name => Vlan(id, name)
  //    }
  //  }

  /**
    * Retrieve a Vlan record by its id.
    * @param id The vlanId to query in the database.
    * @return An option of Vlan indicating if the record was found.
    */
  def getById(id: VlanId)(implicit connection: Connection): Option[Vlan] = {
    SQL"""SELECT id, name FROM vlans WHERE id = $id""".as(Vlan.parser.singleOpt)
  }

  /**
    * Write a new Vlan record.
    * @param vlan The record to write.
    * @return true if the record was writen successfully, otherwise false.
    */
  def create(vlan: Vlan)(implicit connection: Connection): Boolean = {
    SQL"""INSERT INTO vlans (id, name) VALUES (${vlan.id}, ${vlan.name})""".executeUpdate() == 1
  }

}
