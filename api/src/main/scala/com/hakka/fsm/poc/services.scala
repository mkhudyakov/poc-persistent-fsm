package com.hakka.fsm.poc

import akka.actor.{Actor, ActorLogging}
import akka.event.LoggingReceive
import spray.can.Http
import spray.can.Http.Register
import spray.http._
import spray.routing._

import scala.util.control.NonFatal

class RoutedHttpService()
  extends Actor
  with ActorLogging
  with HttpService
  with SimpleRestService
{

  import context.dispatcher
  implicit def actorRefFactory = context

  lazy val config = context.system.settings.config
  lazy val normalHttpRoute =
    options {
      complete {
        HttpResponse(status = StatusCodes.OK)
      }
    } ~
    simpleRestService() ~
    staticRoute

  def receive = LoggingReceive {
    case _: Http.Connected =>
      sender ! Register(self)

    case request: HttpRequest =>
      try {
        normalHttpRoute(RequestContext(request, sender, request.uri.path).withDefaultSender(sender))
      } catch {
        case NonFatal(e) =>
          log.error(s"Error $e")
          complete(HttpResponse(StatusCodes.InternalServerError, HttpEntity(ContentType(MediaTypes.`application/json`), e.getMessage)))
      }
  }

  def staticRoute: Route = {
    path("") {
      complete("Success")
    } ~
    {
      getFromResourceDirectory("resources")
    }
  }
}
