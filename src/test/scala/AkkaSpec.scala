import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestKit}
import org.scalatest.mock.MockitoSugar
import org.scalatest.{Matchers, BeforeAndAfterAll, FlatSpecLike}

abstract class AkkaSpec extends TestKit(ActorSystem("testSystem"))
with FlatSpecLike
with ImplicitSender
with BeforeAndAfterAll
with MockitoSugar
with Matchers {

  override def afterAll {
    TestKit.shutdownActorSystem(system)
  }

}
