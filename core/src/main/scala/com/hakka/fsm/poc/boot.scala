package com.hakka.fsm.poc

import akka.actor.ActorSystem
import akka.util.Timeout

trait ServerCore {

  implicit def actorSystem: ActorSystem
  implicit val timeout: Timeout

  def config = actorSystem.settings.config
}