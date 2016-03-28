package com.hakka.fsm.poc

import java.util.concurrent.TimeUnit._

import akka.actor.Props
import akka.io.IO
import akka.util.Timeout
import spray.can.Http
import spray.http.{HttpResponse, StatusCodes}
import spray.routing.{Rejection, RouteConcatenation}

/**
 * @author Maksym Khudiakov
 */
trait DefaultTimeout {
  val timeoutValue = 20000
  implicit val timeout = Timeout(timeoutValue, MILLISECONDS)
}

trait Api
  extends RouteConcatenation {

  this: ServerCore =>

  def rejectionHandler: PartialFunction[scala.List[Rejection], HttpResponse] = {
    case (rejections: List[Rejection]) => HttpResponse(StatusCodes.BadRequest)
  }

  val rootService = actorSystem.actorOf(Props(new RoutedHttpService()))
}

trait Web {

  this: Api with ServerCore =>

  val httpServer = IO(Http)

  val interface = config.getString("server.bind")
  val webPort = config.getInt("server.port")
  httpServer.tell(Http.Bind(rootService, interface, webPort), rootService)

  actorSystem.registerOnTermination {
    // ioBridge ! Stop
  }
}
