package refined.anorm.tests

import java.sql.Connection

import com.typesafe.config.ConfigFactory
import eu.timepit.refined.auto._
import refined.anorm.models.Vlan

/**
  * Integration tests for refined-anorm
  */
class RefinedAnormIntegrationTest extends BaseTest {

  val dbConfig: DBConfig = DBConfig.loadDBConfig(ConfigFactory.load())

  override def beforeAll(): Unit = {
    dbConfig.prepDatabase()
  }

  override def afterAll(): Unit = {
    dbConfig.hikariDataSource.close()
  }

  val sampleRecord: Vlan = Vlan(300, "This is my vlan")

  def withConnection(test: Connection => Any): Any = {
    val connection = dbConfig.hikariDataSource.getConnection()
    try {
      test(connection)
    } finally {
      connection.close()
    }
  }

  "RefinedAnorm" must {
    "write records to the database" in withConnection { implicit connection =>
      Vlan.create(sampleRecord) must === (true)
    }

    "read records from the database" in withConnection { implicit connection =>
      val fromDB = Vlan.getById(sampleRecord.id)
      fromDB.value must === (sampleRecord)
    }
  }



}
