package refined.anorm.tests

import scala.collection.JavaConverters._

import anorm.SQL
import com.typesafe.config.Config
import com.zaxxer.hikari.{HikariConfig, HikariDataSource}

/**
  * Database config
  */
class DBConfig private (url: String, driver: String, user: String, password: String, sql: List[String]) {
  private val hikariConfig = new HikariConfig()
  hikariConfig.setJdbcUrl(url)
  hikariConfig.setUsername(user)
  hikariConfig.setPassword(password)
  hikariConfig.setDriverClassName(driver)

  val hikariDataSource = new HikariDataSource(hikariConfig)

  /** Runs any SQL necessary to prep the database */
  def prepDatabase(): Unit = {
    val conn = hikariDataSource.getConnection
    sql.foreach { sql => SQL(sql).execute()(conn) }
  }
}

object DBConfig {
  def loadDBConfig(config: Config): DBConfig = {
    val url = config.getString("db.url")
    val driver = config.getString("db.driver")
    val username = config.getString("db.username")
    val password = config.getString("db.password")
    val sql = config.getStringList("db.sql").asScala.toList

    new DBConfig(url, driver, username, password, sql)
  }

}
