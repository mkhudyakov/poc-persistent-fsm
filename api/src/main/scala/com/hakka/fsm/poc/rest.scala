package com.hakka.fsm.poc

import akka.actor._
import akka.cluster.sharding.{ClusterSharding, ClusterShardingSettings}
import akka.event.LoggingAdapter
import akka.pattern.ask
import com.hakka.fsm.poc.fsm.SimpleTransactionActor
import com.hakka.fsm.poc.transaction.{ChangeTransactionData, PerformTransactionEnd, PerformTransactionStart}
import spray.http.{HttpEntity, HttpResponse, StatusCodes}
import spray.routing._

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}

trait SimpleRestService
  extends Directives
  with DefaultTimeout
  with Marshalling
{
  this: Actor =>

  protected def log: LoggingAdapter

  def simpleRestService()(implicit executionContext: ExecutionContext): Route = {
    path("api" / "transactions" / Segment / "start") { (transactionId: String) =>
      get {
        ctx =>
          val fsm = lookupTransactionActor(transactionId)
          (fsm ? PerformTransactionStart(transactionId)).mapTo[String] onComplete {
            case Success(id) =>

              ctx.complete(HttpResponse(StatusCodes.OK, HttpEntity(s"Transaction with id $id started")))
            case Failure(throwable) => ctx.complete(HttpResponse(StatusCodes.InternalServerError, HttpEntity(s"${throwable.getMessage}")))
          }
      }
    } ~
    path("api" / "transactions" / Segment / "end") { (transactionId: String) =>
      get {
        ctx =>
          val fsm = lookupTransactionActor(transactionId)
          (fsm ? PerformTransactionEnd(transactionId)).mapTo[String] onComplete {
            case Success(id) => ctx.complete(HttpResponse(StatusCodes.OK, HttpEntity(s"Transaction with id $id ended")))
            case Failure(throwable) => ctx.complete(HttpResponse(StatusCodes.InternalServerError, HttpEntity(s"${throwable.getMessage}")))
          }
      }
    } ~
    path("api" / "transactions" / Segment / "data" / Segment) { (transactionId: String, data: String) =>
      get {
        ctx =>
          val fsm = lookupTransactionActor(transactionId)
          (fsm ? ChangeTransactionData(transactionId, data)).mapTo[String] onComplete {
            case Success(id) => ctx.complete(HttpResponse(StatusCodes.OK, HttpEntity(s"Transaction data for transaction $id submitted")))
            case Failure(throwable) => ctx.complete(HttpResponse(StatusCodes.InternalServerError, HttpEntity(s"${throwable.getMessage}")))
          }
      }
    }
  }
  
  def lookupTransactionActor(transactionId: String) = {
    val fsm = ClusterSharding(context.system).start(
      SimpleTransactionActor.shardName,
      SimpleTransactionActor.props,
      ClusterShardingSettings(context.system),
      SimpleTransactionActor.idExtractor,
      SimpleTransactionActor.shardResolver
    )
    fsm
  }
}
