package com.hakka.fsm.poc.transaction

/**
 * @author Maksym Khudiakov
 */
object SimpleTransactionActorStates {

  sealed trait SimpleTransactionActorState
  case object Idle extends SimpleTransactionActorState
  case object Corrupted extends SimpleTransactionActorState
  case object Completed extends SimpleTransactionActorState
}

sealed trait SimpleTransactionActorData
case class UninitializedData() extends SimpleTransactionActorData
case class ActiveTransactionData(transactionId: String, data: List[String]) extends SimpleTransactionActorData
