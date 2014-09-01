package workshop.part2

import scala.concurrent.duration.Duration.Zero
import scala.language.postfixOps
import akka.actor._
import akka.testkit.TestActorRef
import org.mockito.Matchers._
import org.mockito.Mockito._
import scala.concurrent.duration._
import workshop.helpers._
import workshop.work.RiskyWorkException
import workshop.AkkaSpec


class ComputeSupervisorTest extends AkkaSpec {

  val timeout: FiniteDuration = 75 millis

  it should "start compute actor and return its reference" in {
    suppressStackTraceNoise {
      val computeActorFactory = mock[ComputeActorFactory]
      val computeActor: ActorRef = mock[ActorRef]
      when(computeActorFactory.create(any[ActorContext], anyString())).thenReturn(computeActor)

      val computeSupervisor = TestActorRef(Props(classOf[ComputeSupervisor], computeActorFactory))

      computeSupervisor ! CreateComputeActor("computeActor-1")

      val actor: ActorRef = expectMsgClass(Zero, classOf[ActorRef])
      actor shouldBe computeActor
    }
  }

  it should "resume compute actor on arithmetic exception" in {
    suppressStackTraceNoise {
      val computeSupervisor = TestActorRef(Props(classOf[ComputeSupervisor], new ComputeTestActorFactory))
      computeSupervisor ! CreateComputeActor("computeActor-1")

      val computeTestActor: ActorRef = expectMsgClass(Zero, classOf[ActorRef])
      val exception: ArithmeticException = new ArithmeticException
      computeTestActor ! exception

      computeTestActor ! IsRestarted
      expectMsg(timeout, false)
    }
  }

  it should "restart compute actor on risky work exception" in {
    suppressStackTraceNoise {
      val computeSupervisor = TestActorRef(Props(classOf[ComputeSupervisor], new ComputeTestActorFactory))
      computeSupervisor ! CreateComputeActor("computeActor-1")

      val computeTestActor: ActorRef = expectMsgClass(Zero, classOf[ActorRef])

      computeTestActor ! RiskyWorkException("test exception")

      computeTestActor ! IsRestarted
      expectMsg(timeout, true)
    }
  }

  it should "stop compute actor on any exception other than arithmetic and risky work exception" in {
    suppressStackTraceNoise {
      val computeSupervisor = TestActorRef(Props(classOf[ComputeSupervisor], new ComputeTestActorFactory))
      computeSupervisor ! CreateComputeActor("computeActor-1")

      val computeTestActor: ActorRef = expectMsgClass(Zero, classOf[ActorRef])
      watch(computeTestActor)

      computeTestActor ! new NumberFormatException("test exception")

      expectMsgClass(timeout, classOf[Terminated])
    }
  }
}
