package repositories
import exceptions.CustomException
import models.{Id, Income, Outcome, Transaction}

import scala.concurrent.Future

class RepositoryImpl extends Repository {
  var incomeRepository: Map[String, Income] = Map.empty[String, Income]
  var outcomeRepository: Map[String, Outcome] = Map.empty[String, Outcome]

  override def upsert(transaction: Transaction): Future[Either[CustomException, Any]] = {
    transaction match {
      case income: Income => Future.successful(Right(incomeRepository += ((income.id.value, income))))
      case outcome: Outcome => Future.successful(Right(outcomeRepository += ((outcome.id.value, outcome))))
    }
  }

  override def get(id: Id): Future[Either[CustomException, Option[Transaction]]] = Future.successful(Right((incomeRepository ++ outcomeRepository).get(id.value)))

  override def get(startTimestamp: Long, endTimestamp: Long): Future[Either[CustomException, List[Transaction]]] = {
    val merged: Iterator[(String, Transaction with Product with Serializable)] = (incomeRepository ++ outcomeRepository).iterator
    val transactions: List[Transaction with Product with Serializable] = merged.map (_._2).toList
    val filtered: List[Transaction with Product with Serializable] = transactions.filter(transaction => transaction.timestamp > startTimestamp || transaction.timestamp < endTimestamp)

    Future.successful(Right(filtered))
  }

  override def delete(id: Id): Future[Either[CustomException, Any]] = {
    incomeRepository -= id.value
    outcomeRepository -= id.value

    Future.successful(Right(true))
  }
}