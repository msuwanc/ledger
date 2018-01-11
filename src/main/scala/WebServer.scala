import java.time.Instant
import java.util.UUID.randomUUID

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import com.google.inject.{Guice, Injector}
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
import io.circe.generic.auto._
import io.circe.syntax._
import models._
import services.LedgerService
import io.circe._
import io.circe.generic.auto._
import io.circe.parser._
import io.circe.syntax._
import io.circe.Encoder._

import scala.io.StdIn

object WebServer {
  def main(args: Array[String]) {
    implicit val system = ActorSystem("ledger-system")
    implicit val materializer = ActorMaterializer()
    // needed for the future flatMap/onComplete in the end
    implicit val executionContext = system.dispatcher

    val injector: Injector = Guice.createInjector(new Module)
    val ledgerService: LedgerService = injector.getInstance(classOf[LedgerService])

    val route: Route =
      put {
        path("expense") {
          entity(as[IncomeOutcomeRequest]) { incomeOutcomeRequest =>
            val outcome: Outcome = Outcome(Id(randomUUID().toString), Type(incomeOutcomeRequest.`type`), incomeOutcomeRequest.value, Instant.now().toEpochMilli)

            onSuccess(ledgerService.expense(outcome)) {
              case Left(exception) => complete(StatusCodes.InternalServerError)
              case Right(any) => complete(StatusCodes.Created)
            }
          }
        } ~
        path("income") {
          entity(as[IncomeOutcomeRequest]) { incomeOutcomeRequest =>
            val income: Income = Income(Id(randomUUID().toString), Type(incomeOutcomeRequest.`type`), incomeOutcomeRequest.value, Instant.now().toEpochMilli)

            onSuccess(ledgerService.income(income)) {
              case Left(exception) => complete(StatusCodes.InternalServerError)
              case Right(any) => complete(StatusCodes.Created)
            }
          }
        }
      } ~
      get {
        path("daily") {
          ledgerService.dailyStatement(Instant.now().toEpochMilli)

          onSuccess(ledgerService.dailyStatement(Instant.now().toEpochMilli)) {
            case Left(exception) => complete(StatusCodes.InternalServerError)
            case Right(transactions) => {
              complete(transactions map {
                  case income: Income => income.asJson
                  case outcome: Outcome => outcome.asJson
                }
              )
            }
          }
        }
      } ~
      delete {
        pathPrefix("transaction" / Segment) { id =>
          onSuccess(ledgerService.delete(Id(id))) {
            case Left(exception) => complete(StatusCodes.InternalServerError)
            case Right(any) => complete(StatusCodes.OK)
          }
        }
      }

    println(s"Server online at http://localhost:8080/")
    val bindingFuture = Http().bindAndHandle(route, "localhost", 8080)

    StdIn.readLine() // let it run until user presses return
    bindingFuture
      .flatMap(_.unbind()) // trigger unbinding from the port
      .onComplete(_ ⇒ system.terminate()) // and shutdown when done
  }
}