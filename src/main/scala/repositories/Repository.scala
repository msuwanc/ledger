package repositories

import exceptions.CustomException
import models.{Id, Transaction}

import scala.concurrent.Future

trait Repository {
  def upsert(transaction: Transaction): Future[Either[CustomException, Any]]
  def get(id: Id): Future[Either[CustomException, Option[Transaction]]]
  def get(startTimestamp: Long, endTimestamp: Long): Future[Either[CustomException, List[Transaction]]]
  def delete(id: Id): Future[Either[CustomException, Any]]
}