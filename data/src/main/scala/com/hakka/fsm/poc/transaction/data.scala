package com.hakka.fsm.poc.transaction

/**
 * @author Maksym Khudiakov
 */
@SerialVersionUID(1L)
case class PerformTransactionStart(transactionId: String)

@SerialVersionUID(1L)
case class ChangeTransactionData(transactionId: String, data: String)

@SerialVersionUID(1L)
case class PerformTransactionEnd(transactionId: String)
