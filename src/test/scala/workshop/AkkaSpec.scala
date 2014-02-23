package workshop

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestKit}
import org.scalatest.mock.MockitoSugar
import org.scalatest.{Matchers, BeforeAndAfterAll, FlatSpecLike}
import com.typesafe.config.ConfigFactory


object AkkaSpec {
  def getTestSystem(): ActorSystem = {
    // Replace normal event handler with test event handler supporting
    // expecting exceptions and log messages during tests
    val config = ConfigFactory.parseString("""akka.loggers = ["akka.testkit.TestEventListener"]""")

    ActorSystem("testSystem", config)
  }
}

abstract class AkkaSpec extends TestKit(AkkaSpec.getTestSystem())
with FlatSpecLike
with ImplicitSender
with BeforeAndAfterAll
with MockitoSugar
with Matchers {

  override def afterAll {
    TestKit.shutdownActorSystem(system)
  }
}
