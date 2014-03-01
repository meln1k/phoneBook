package models

import play.api.db._
import play.api.Play.current

import anorm._
import anorm.SqlParser._

/**
 * Created by meln1k on 28/02/14.
 */
case class Number(id: Pk[Long] = NotAssigned, name: String, phoneNumber: String)

/**
 * Helper for pagination.
 */
case class Page[A](items: Seq[A], page: Int, offset: Long, total: Long) {
  lazy val prev = Option(page - 1).filter(_ >= 0)
  lazy val next = Option(page + 1).filter(_ => (offset + items.size) < total)
}

object Number {

  /**
   * Parse a Number from a ResultSet
   */
  val numberParser = {
    get[Pk[Long]]("number.id") ~
    get[String]("number.name") ~
    get[String]("number.phoneNumber") map {
      case id~name~phoneNumber => Number(id, name, phoneNumber)
    }
  }

  /**
   * Retrieve a Number by id.
   */
  def findById(id: Long): Option[Number] = {
    DB.withConnection { implicit connection =>
      SQL("select * from number where id = {id}").on('id -> id).as(Number.numberParser.singleOpt)
    }
  }

  /**
   * Retrieve a Number by name.
   */
  def findByName(name: String): Option[Number] = {
    DB.withConnection { implicit connection =>
      SQL("select * from number where name = {name}").on('name -> name).as(Number.numberParser.singleOpt)
    }
  }

  /**
   * Retrieve a Number by phoneNumber.
   */
  def findByPhoneNumber(phoneNumber: String): Option[Number] = {
    DB.withConnection { implicit connection =>
      SQL("select * from number where phoneNumber = {phoneNumber}").on('phoneNumber -> phoneNumber).as(Number.numberParser.singleOpt)
    }
  }

  /**
   * Check if a name is free to assign
   */
  def nameIsFree(name: String): Boolean = Number.findByName(name).isEmpty

  /**
   * Check if a phoneNumber is free to assign
   */
  def phoneNumberIsFree(phoneNumber: String): Boolean = Number.findByName(phoneNumber).isEmpty

  /**
   * Return a page of Number.
   *
   * @param page Page to display
   * @param pageSize Amount of Numbers per page
   * @param orderBy Number property used for sorting
   * @param filter Filter applied on the name column
   */
  def list(page: Int = 0, pageSize: Int = 10, orderBy: Int = 1, filter: String = "%"): Page[Number] = {

    val offset = pageSize * page

    DB.withConnection { implicit connection =>

      val numbers = SQL(
        """
          select * from number
          where number.name like {filter}
          order by {orderBy} nulls last
          limit {pageSize} offset {offset}
        """
      ).on(
          'pageSize -> pageSize,
          'offset -> offset,
          'filter -> filter,
          'orderBy -> orderBy
        ).as(Number.numberParser *)

      val totalRows = SQL(
        """
          select count(*) from number
          where number.name like {filter}
        """
      ).on(
          'filter -> filter
        ).as(scalar[Long].single)

      Page(numbers, page, offset, totalRows)

    }

  }

  /**
   * Update a number.
   *
   * @param id The number id
   * @param number The number values.
   */
  def update(id: Long, number: Number) = {
    DB.withConnection { implicit connection =>
      SQL(
        """
          update number
          set name = {name}, phoneNumber = {phoneNumber}
          where id = {id}
        """
      ).on(
          'id -> id,
          'name -> number.name,
          'phoneNumber -> number.phoneNumber
        ).executeUpdate()
    }
  }

  /**
   * Insert a new number.
   *
   * @param number The number values.
   */
  def insert(number: Number) = {
    DB.withConnection { implicit connection =>
      SQL(
        """
          insert into number values (
            (select next value for number_seq),
            {name}, {phoneNumber}
          )
        """
      ).on(
          'name -> number.name,
          'phoneNumber -> number.phoneNumber
        ).executeUpdate()
    }
  }

  /**
   * Delete a number.
   *
   * @param id Id of the number to delete.
   */
  def delete(id: Long) = {
    DB.withConnection { implicit connection =>
      SQL("delete from number where id = {id}").on('id -> id).executeUpdate()
    }
  }



}