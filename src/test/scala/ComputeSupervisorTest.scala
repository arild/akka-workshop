import akka.actor._
import akka.testkit.{TestActorRef, ImplicitSender, TestKit}
import actors.{IsRestarted, ComputeTestActor}
import scala.concurrent.duration._
import org.scalatest.mock.MockitoSugar
import org.scalatest.{Matchers, BeforeAndAfterAll, FunSpecLike}
import org.mockito.Matchers._
import org.mockito.Mockito._

class ComputeSupervisorTest extends TestKit(ActorSystem("testSystem"))
with FunSpecLike
with ImplicitSender
with BeforeAndAfterAll
with MockitoSugar
with Matchers {

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

  it("should resume compute actor on arithmetic exception") {

    val computeSupervisor = TestActorRef(Props(new ComputeSupervisor(new ComputeActorTestFactory)))
    computeSupervisor ! StartComputeActor("computeActor-1")

    val computeTestActor: ActorRef = expectMsgClass(classOf[ActorRef])
    val exception: ArithmeticException = new ArithmeticException
    computeTestActor ! exception

    computeTestActor ! IsRestarted
    expectMsg(false)
  }

  it("should restart compute actor on any exception other than arithmetic exception") {

    val computeSupervisor = TestActorRef(Props(new ComputeSupervisor(new ComputeActorTestFactory)))
    computeSupervisor ! StartComputeActor("computeActor-1")

    val computeTestActor: ActorRef = expectMsgClass(classOf[ActorRef])


      computeTestActor ! new NumberFormatException("test exception")

      computeTestActor ! IsRestarted
      expectMsg(true)
  }

  class ComputeActorTestFactory extends ComputeActorFactory {
    override def create(context: ActorContext, actorName: String): ActorRef = {
      context.actorOf(Props(classOf[ComputeTestActor]), actorName)
    }
  }
}
