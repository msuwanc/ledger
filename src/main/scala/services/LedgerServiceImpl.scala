package services

import java.time.{Instant, LocalDateTime, ZoneId, ZoneOffset}
import javax.inject.Inject

import exceptions.CustomException
import models.{Id, Income, Outcome, Transaction}
import repositories.Repository

import scala.concurrent.Future

class LedgerServiceImpl @Inject()(repository: Repository) extends LedgerService {
  override def expense(outcome: Outcome): Future[Either[CustomException, Any]] = repository.upsert(outcome)

  override def income(income: Income): Future[Either[CustomException, Any]] = repository.upsert(income)

  override def dailyStatement(date: Long): Future[Either[CustomException, List[Transaction]]] = {
    val localDateTime: LocalDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(date), ZoneId.systemDefault())
    val startTimestamp: Long = LocalDateTime.of(localDateTime.getYear, localDateTime.getMonth, localDateTime.getDayOfMonth, 0, 0, 0).toEpochSecond(ZoneOffset.UTC)
    val endTimestamp: Long = LocalDateTime.of(localDateTime.getYear, localDateTime.getMonth, localDateTime.getDayOfMonth, 23, 59, 59).toEpochSecond(ZoneOffset.UTC)

    repository.get(startTimestamp, endTimestamp)
  }

  override def update(transaction: Transaction): Future[Either[CustomException, Any]] = repository.upsert(transaction)

  override def delete(id: Id): Future[Either[CustomException, Any]] = repository.delete(id)
}