package services

import exceptions.CustomException
import models.{Id, Income, Outcome, Transaction}

import scala.concurrent.Future

trait LedgerService {
  def expense(outcome: Outcome): Future[Either[CustomException, Any]]
  def income(income: Income): Future[Either[CustomException, Any]]
  def dailyStatement(date: Long): Future[Either[CustomException, List[Transaction]]]
  def update(transaction: Transaction): Future[Either[CustomException, Any]]
  def delete(id: Id): Future[Either[CustomException, Any]]
}