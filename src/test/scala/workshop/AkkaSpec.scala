package workshop

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestKit}
import org.scalatest.mock.MockitoSugar
import org.scalatest.{Matchers, BeforeAndAfterAll, FlatSpecLike}
import com.typesafe.config.ConfigFactory


abstract class AkkaSpec extends TestKit(ActorSystem("testSystem"
  ,
  ConfigFactory.parseString(AkkaSpec.config
  )
))
with FlatSpecLike
with ImplicitSender
with BeforeAndAfterAll
with MockitoSugar
with Matchers {

  override def afterAll {
    TestKit.shutdownActorSystem(system)
  }

}

object AkkaSpec {
  val config = """
    akka.loggers = ["akka.testkit.TestEventListener"]
  """
}