package com.hakka.fsm.poc.util

import akka.actor.{FSM, Actor, ActorLogging}
import akka.persistence.PersistentActor
import org.apache.commons.lang3.exception.ExceptionUtils

trait BaseActorLogging extends Actor with ActorLogging with ExceptionLoggingUtils {

  override def preStart() = {
    log.info(s"${getClass.getSimpleName} started")
    super.preStart()
  }

  override def preRestart(cause: Throwable, msg: Option[Any]) = {
    log.info(s"${getClass.getSimpleName} restarting whilst handling ${msg.map(_.getClass.getSimpleName)} - reason: ${exceptionString(cause)}")
    super.preRestart(cause, msg)
  }

  override def postStop() = {
    log.info(s"${getClass.getSimpleName} stopping")
    super.postStop()
  }
}

trait BaseFSMLogging[State, Data] extends PersistentActor with FSM[State, Data] with BaseActorLogging {

  override def preRestart(cause: Throwable, msg: Option[Any]) = {
    log.debug(s"Restarting ${getClass.getSimpleName}")
    super.preRestart(cause, msg)
    
  }

  override def postStop() = {
    log.debug(s"Stopping ${getClass.getSimpleName}")
    super.postStop()
  }
}

trait ExceptionLoggingUtils {

  def exceptionString(cause: Throwable): String = {
    ExceptionUtils.getStackTrace(cause)
  }
}
