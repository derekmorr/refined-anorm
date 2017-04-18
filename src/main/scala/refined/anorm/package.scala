package refined

import java.sql.PreparedStatement

import scala.language.higherKinds

import _root_.anorm.{Column, ParameterMetaData, ToStatement, TypeDoesNotMatch}
import eu.timepit.refined.api.{RefType, Validate}

/**
  * Refined support for Anorm.
  */
package object anorm {

  /**
    * Implicit support for deserializing a column from a database record into a refined type.
    * @param refType The refinement carrier type
    * @param validate A [[Validate]] instance for the base type and the type-level refinement predicate.
    * @param baseColumnTo The Anorm [[Column]] for the refinement's base type (e.g. Int, String, etc)
    * @tparam F The refined type
    * @tparam T The base type that's being refined.
    * @tparam P The type-level refinement predicate
    * @return A Column for type F[T, P] (e.g., Refined[Int, Positive])
    */
  implicit def columnToRefType[F[_, _], T, P](implicit
                                              refType: RefType[F],
                                              validate: Validate[T, P],
                                              baseColumnTo: Column[T]): Column[F[T, P]] = {

    Column.nonNull { (value, meta) =>
      baseColumnTo(value, meta).toEither match {
        case Left(err) => Left(err)
        case Right(value) => refType.refine[P](value) match {
          case Left(errMsg) => 
            val className = value.asInstanceOf[AnyRef].getClass.toString
            Left(TypeDoesNotMatch(
              s"Value $value of type $className for column ${meta.column.qualified} does not satisfy refinement predicate: $errMsg"))
          case Right(r) => Right(r)
        }
      }
    }
  }


  /**
    * Implicit support for writing a refined type into a JDBC prepared statement
    * @param baseTypeToStatement The Anorm [[ToStatement]] for the refinement's base type (e.g. Int, String, etc)
    * @param refType The refinement carrier type
    * @tparam F carrier type of a refinement.
    * @tparam T The base type that's being refined.
    * @tparam P The type-level refinement predicate
    * @return A ToStatement instance for the refined type F[T, P] (e.g., Refined[Int, Positive])
    */
  implicit def refTypeToStatement[F[_, _], T, P](implicit
                                                 baseTypeToStatement: ToStatement[T],
                                                 refType: RefType[F]): ToStatement[F[T, P]] =
    new ToStatement[F[T, P]] {
      def set(s: PreparedStatement, index: Int, refined: F[T, P]): Unit =
        baseTypeToStatement.set(s, index, refType.unwrap(refined))
    }


  /**
    * Implicit metadata for refined types. Used when writing records into a database.
    * @param baseParam The Anorm [[ParameterMetaData]] instance for the refinement's base type (e.g., Int, String, etc)
    * @tparam F carrier type of a refinement.
    * @tparam T The base type that's being refined.
    * @tparam P The type-level refinement predicate
    * @return A ParameterMetaDAta instance for the refined type F[T, P] (e.g., Refined[Int, Positive])
    */
  implicit def refTypeMetaData[F[_, _], T, P](implicit baseParam: ParameterMetaData[T]): ParameterMetaData[F[T, P]] =
    new ParameterMetaData[F[T, P]] {
      val sqlType: String = baseParam.sqlType
      val jdbcType: Int = baseParam.jdbcType
    }

}
