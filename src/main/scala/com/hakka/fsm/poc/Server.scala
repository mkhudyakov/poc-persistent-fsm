package com.hakka.fsm.poc

import akka.actor.ActorSystem
import akka.kernel.Bootable
import akka.util.Timeout
import com.typesafe.config.{Config, ConfigFactory, ConfigParseOptions, ConfigResolveOptions}

import scala.concurrent.duration._

trait ServerOperations {

  implicit val system = {
    val configResource = System.getProperty("config.resource") match {
      case x: String => x
      case _  =>
        System.setProperty("config.resource", "reference.conf")
        "reference.conf"
    }

    val serverSettings: Config = ConfigFactory.load(configResource,
      ConfigParseOptions.defaults().setAllowMissing(false),
      ConfigResolveOptions.defaults())

    ActorSystem("PersistentFsmPoc", serverSettings)
  }

//  val sharedStore = system.actorOf(Props[SharedLeveldbStore], "store")
//  SharedLeveldbJournal.setStore(sharedStore, system)

  class Application(val actorSystem: ActorSystem) extends ServerCore with Api with Web {
    val timeout = Timeout(30000, MILLISECONDS)
  }

  def startup(): Unit = {
    new Application(system)
  }

  def shutdown(): Unit = {
    system.shutdown()
  }
}

class Server extends Bootable with ServerOperations

object Server extends ServerOperations {
  def main(args: Array[String]) {
    startup()

    sys.addShutdownHook {
      shutdown()
    }
  }
}

