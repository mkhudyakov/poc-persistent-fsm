package com.hakka.fsm.poc

import spray.http.{HttpEntity, MediaTypes, StatusCode}
import spray.httpx.SprayJsonSupport
import spray.httpx.marshalling.{CollectingMarshallingContext, Marshaller, MarshallingContext, MetaMarshallers}
import spray.json.{DefaultJsonProtocol, JsValue, PrettyPrinter}

/**
 * @author Maksym Khudiakov
 */
trait Marshalling extends DefaultJsonProtocol with SprayJsonSupport with MetaMarshallers {

}