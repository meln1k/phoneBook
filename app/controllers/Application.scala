package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._

import anorm._

import views._
import models._
import play.api.data.validation.Constraints

/**
 * Manage a phone numbers database
 */
object Application extends Controller {

  /**
   * This result redirect to the home page.
   */
  val Home = Redirect(routes.Application.list(0, 2, ""))

  /**
   * The number form.
   */
  val numberForm = Form(
    mapping(
      "id" -> ignored(NotAssigned:Pk[Long]),
      "name" -> nonEmptyText.verifying(Number.nameCheckConstraint),
      "phoneNumber" -> nonEmptyText.verifying(Number.phoneNumberIsUniqueConstraint, Number.phoneNumberIsRealConstraint)
    )(Number.apply)(Number.unapply)
  )

  /**
   * Redirect to numbers list
   */
  def index = Action { Home }

  /**
   * Display the paginated list of phone numbers.
   *
   * @param page Current page number (starts from 0)
   * @param orderBy Column to be sorted
   * @param filter Filter applied on names
   */
  def list(page: Int, orderBy: Int, filter: String) = Action { implicit request =>
    Ok(html.list(
      Number.list(page = page, orderBy = orderBy, filter = ("%"+filter+"%")),
      orderBy, filter
    ))
  }

  /**
   * Display the 'new number form'.
   */
  def create = Action {
    Ok(html.createForm(numberForm))
  }

  /**
   * Handle the 'new number form' submission.
   */
  def save = Action { implicit request =>
    numberForm.bindFromRequest.fold(
      formWithErrors => BadRequest(html.createForm(formWithErrors)),
      number => {
        Number.insert(number)
        Home.flashing("success" -> Messages("number.created", number.name))
      }
    )
  }

  /**
   * Display the 'edit form' of a existing number.
   *
   * @param id Id of the number to edit
   */
  def edit(id: Long) = TODO

  /**
   * Handle the 'edit form' submission.
   *
   * @param id Id of the number to edit
   */
  def update(id: Long) = TODO

  /**
   * Handle number deletion.
   *
   * @param id Id of the number to delete
   */
  def delete(id: Long) = TODO

}