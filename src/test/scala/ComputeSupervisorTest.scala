import akka.actor.{ActorContext, Props, ActorRef, ActorSystem}
import akka.testkit.{TestActorRef, ImplicitSender, TestKit}
import scala.concurrent.duration._
import org.scalatest.mock.MockitoSugar
import org.scalatest.{Matchers, BeforeAndAfterAll, FunSpecLike}
import org.mockito.Matchers._
import org.mockito.Mockito._

class ComputeSupervisorTest(_system: ActorSystem) extends TestKit(_system)
with FunSpecLike
with ImplicitSender
with BeforeAndAfterAll
with MockitoSugar
with Matchers {

  def this() = this(ActorSystem("testSystem"))

  override def afterAll {
    TestKit.shutdownActorSystem(system)
  }

  it("should start compute actor and return its reference") {

    val computeActorFactory = mock[ComputeActorFactory]
    val computeActor: ActorRef = mock[ActorRef]
    when(computeActorFactory.create(any[ActorContext], anyString())).thenReturn(computeActor)

    val computeSupervisor = TestActorRef(Props(new ComputeSupervisor(computeActorFactory)))

    computeSupervisor ! StartComputeActor("computeActor-1")

    within(500 millis){
      val actor: ActorRef = expectMsgClass(classOf[ActorRef])
      actor shouldBe computeActor
    }
  }
}
