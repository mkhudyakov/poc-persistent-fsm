package com.hakka.fsm.poc.fsm

import akka.actor.Props
import akka.cluster.sharding.ShardRegion
import akka.util.Timeout
import com.hakka.fsm.poc.transaction._
import com.hakka.fsm.poc.transaction.SimpleTransactionActorStates.{Corrupted, Completed, Idle}
import com.hakka.fsm.poc.transaction.{ActiveTransactionData, UninitializedData, SimpleTransactionActorData, SimpleTransactionActorStates}
import com.hakka.fsm.poc.util.BaseFSMLogging

import scala.concurrent.duration._

object SimpleTransactionActor {

  val shardName = "transaction-actor"
  val props = Props[SimpleTransactionActor]

  val idExtractor: ShardRegion.ExtractEntityId = {
    case msg: PerformTransactionStart      => (msg.transactionId.toString, msg)
    case msg: ChangeTransactionData       => (msg.transactionId.toString, msg)
    case msg: PerformTransactionEnd        => (msg.transactionId.toString, msg)
  }

  val shardResolver: ShardRegion.ExtractShardId = {
    case msg: PerformTransactionStart ⇒ s"${msg.transactionId.hashCode() % 10}"
    case msg: ChangeTransactionData ⇒ s"${msg.transactionId.hashCode() % 10}"
    case msg: PerformTransactionEnd ⇒ s"${msg.transactionId.hashCode() % 10}"
  }
}

/**
 * @author Maksym Khudiakov
 */
class SimpleTransactionActor extends BaseFSMLogging[SimpleTransactionActorStates.SimpleTransactionActorState, SimpleTransactionActorData] {

  val config = context.system.settings.config
  val futureTimeoutDuration = config.getDuration("timeouts.future", SECONDS).seconds
  implicit val futureTimeout = Timeout(futureTimeoutDuration)
  val idleTimeout = config.getDuration("timeouts.idle", SECONDS).seconds
  val waitingTimeout = config.getDuration("timeouts.waiting", SECONDS).seconds

  startWith(Idle, UninitializedData())
  when(Idle, waitingTimeout)(idleStateFunction)
  when(Corrupted, waitingTimeout)(PartialFunction.empty)
  when(Completed, waitingTimeout)(PartialFunction.empty)

  def idleStateFunction: StateFunction = {
    case Event(msg: PerformTransactionStart, UninitializedData()) =>
      persist(msg) { _ => }
      
      log.info(s"Starting a transaction with id ${msg.transactionId}")
      val data = ActiveTransactionData(msg.transactionId, List())

      /* Send a reply */
      sender() ! msg.transactionId
      stay using data
      
    case Event(msg @ ChangeTransactionData(transactionId, data), t: ActiveTransactionData) =>
      persist(msg) { _ => }
      
      val updatedTransaction = t.copy(data = t.data ::: List(data))
      log.info(s"There are ${updatedTransaction.data.size} data items within a transaction ${t.transactionId}")

      /* Send a reply */
      sender() ! t.transactionId
      stay() using updatedTransaction

    case Event(PerformTransactionEnd(transactionId), data: ActiveTransactionData) =>
      log.info(s"Ending a transaction with id ${data.transactionId}")

      goto(Completed) replying data.transactionId

    case Event(StateTimeout, data: ActiveTransactionData) =>
      log.error(s"Time out occurred.")
      goto(Corrupted)
    
    case Event(event, _) =>
      log.info(s"Unexpected event received: $event. Transaction is of $stateData")
      stay()
  }

  def saveAnd(f: TransitionHandler): TransitionHandler = {
    case a -> b =>
      log.info("** Transition from " + a + " -> " + b)
      nextStateData match {
        case t: ActiveTransactionData =>
        case _ =>
      }
      f(a, b)
  }

  onTransition {
    saveAnd {
      def bail() {
        context.children.foreach(context.stop)
        context.stop(self)
        stop()
      }
      {
        case _ -> Completed =>
          stateData match {
            case rt: ActiveTransactionData =>
              bail()
            case _ =>
          }
        case _ -> Corrupted =>
          stateData match {
            case rt: ActiveTransactionData =>
              bail()
            case _ =>
          }
        case _ -> _ =>
      }
    }
  }

  def persistenceId: String = context.self.path.name
  
  override def receive: Receive = {
    case message =>
      log.info(s"RECEIVE CALLED with message $message")
      super.receive(message)
  }

  override def receiveRecover: Receive = {
    case msg: PerformTransactionStart =>
      log.info(s"Receive Recover called with message: $msg")
      
      val data = ActiveTransactionData(msg.transactionId, List())
      startWith(Idle, data, Some(waitingTimeout))
      
    case msg: ChangeTransactionData =>
      log.info(s"Receive Recover called with message: $msg")
      stateData match {
        case t: ActiveTransactionData =>
          val updatedTransaction = t.copy(data = t.data ::: List(msg.data))
          startWith(Idle, updatedTransaction, Some(waitingTimeout))
        case _ =>
      }

    case message =>
      log.info(s"Receive Recover called with message: $message")

  }

  override def receiveCommand: Receive = {
    case message =>
      log.info(s"Receive Command called with message: $message")
  }
}
