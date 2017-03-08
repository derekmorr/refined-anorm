# Refined Anorm

Adds support for [Refinement types](https://github.com/fthomas/refined) to the 
[Anorm](https://github.com/playframework/anorm) database access layer for Scala.

# Usage

Add the following to Anorm models:

```scala
import refined.anorm._
```

Then you'll be able to serialize and deserialize refined types with JDBC.

For example:

```scala
/** 802.1Q Vlan ID */
type VlanId = Int Refined Interval.Closed[W.`0`.T, W.`4095`.T]

type NonBlankString = String Refined And[NonEmpty, Exists[Not[Whitespace]]]

case class Vlan(id: VlanId, name: NonBlankString)

object Vlan {
   import refined.anorm._
   val parser: RowParser[Vlan] = Macro.namedParser[Vlan]
  
  def getById(id: VlanId)(implicit connection: Connection): Option[Vlan] = {
    SQL"""SELECT id, name FROM vlans WHERE id = $id""".as(Vlan.parser.singleOpt)
  }
 
  def create(vlan: Vlan)(implicit connection: Connection): Boolean = {
    SQL"""INSERT INTO vlans (id, name) VALUES (${vlan.id}, ${vlan.name})""".executeUpdate() == 1
  }
}
```

There is a complete, working example in the integration tests (in `src/it`).



# Testing the app

To run unit tests, run

    sbt test
To run the integration tests:

1.  Install a database (PostgreSQL is the default).
2. Create a database named `refinement`. Grant a user read/write and `create table` access to it.
3. Edit `src/it/resources/application.conf`:
   1. Edit the `username` and `password` fields
   2. Adjust the database URL if necessary.
4. Run `sbt it:test`



# Code coverage report

To generate a code coverage report run,

    sbt clean coverage test coverageReport

The HTML report will be written to `target/scala-2.12/scoverage-report/index.html`.

# Code quality analysis

The project uses the [scapegoat tool](https://github.com/sksamuel/scapegoat) for code quality analysis.
Run run a scapegoat report, run

    sbt scapegoat

The HTML report will be written to `target/scala-2.12/scapegoat-report/scapegoat.html`

