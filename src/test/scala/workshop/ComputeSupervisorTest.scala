package workshop

import scala.language.postfixOps
import akka.actor._
import akka.testkit.TestActorRef
import org.mockito.Matchers._
import org.mockito.Mockito._
import scala.concurrent.duration._
import workshop.helpers._
import workshop.work.RiskyWorkException


class ComputeSupervisorTest extends AkkaSpec {

  val timeout: FiniteDuration = 50 millis

  it should "start compute actor and return its reference" in {
    suppressStackTraceNoise{

      val computeActorFactory = mock[ComputeActorFactory]
      val computeActor: ActorRef = mock[ActorRef]
      when(computeActorFactory.create(any[ActorContext], anyString())).thenReturn(computeActor)

      val computeSupervisor = TestActorRef(workshop.companion.ComputeSupervisor.props(computeActorFactory))

      computeSupervisor ! StartComputeActor("computeActor-1")

      val actor: ActorRef = expectMsgClass(timeout, classOf[ActorRef])
      actor shouldBe computeActor
    }
  }

  it should "resume compute actor on arithmetic exception" in {
    suppressStackTraceNoise{
      val computeSupervisor = TestActorRef(workshop.companion.ComputeSupervisor.props(new ComputeTestActorFactory))
      computeSupervisor ! StartComputeActor("computeActor-1")

      val computeTestActor: ActorRef = expectMsgClass(timeout, classOf[ActorRef])
      val exception: ArithmeticException = new ArithmeticException
      computeTestActor ! exception

      computeTestActor ! IsRestarted
      expectMsg(timeout,false)
    }
  }

  it should "restart compute actor on risky work exception" in {
    suppressStackTraceNoise{
      val computeSupervisor = TestActorRef(workshop.companion.ComputeSupervisor.props(new ComputeTestActorFactory))
      computeSupervisor ! StartComputeActor("computeActor-1")

      val computeTestActor: ActorRef = expectMsgClass(timeout, classOf[ActorRef])

      computeTestActor ! RiskyWorkException("test exception")

      computeTestActor ! IsRestarted
      expectMsg(timeout,true)
    }
  }

  it should "stop compute actor on any exception other than arithmetic and risky work exception" in {
    suppressStackTraceNoise{
      val computeSupervisor = TestActorRef(workshop.companion.ComputeSupervisor.props(new ComputeTestActorFactory))
      computeSupervisor ! StartComputeActor("computeActor-1")

      val computeTestActor: ActorRef = expectMsgClass(timeout, classOf[ActorRef])
      watch(computeTestActor)

      computeTestActor ! new NumberFormatException("test exception")

      expectMsgClass(500 millisecond, classOf[Terminated])
    }
  }
}
