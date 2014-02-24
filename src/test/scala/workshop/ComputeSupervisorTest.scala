package workshop

import akka.actor._
import akka.testkit.{TestProbe, EventFilter, TestActorRef}
import org.mockito.Matchers._
import org.mockito.Mockito._
import scala.concurrent.duration._
import workshop.helpers._


class ComputeSupervisorTest extends AkkaSpec {

  it should "start compute actor and return its reference" in {

    val computeActorFactory = mock[ComputeActorFactory]
    val computeActor: ActorRef = mock[ActorRef]
    when(computeActorFactory.create(any[ActorContext], anyString())).thenReturn(computeActor)

    val computeSupervisor = TestActorRef(Props(new ComputeSupervisor(computeActorFactory)))

    computeSupervisor ! StartComputeActor("computeActor-1")

    val actor: ActorRef = expectMsgClass(classOf[ActorRef])
    actor shouldBe computeActor
  }

  it should "resume compute actor on arithmetic exception" in {

    val computeSupervisor = TestActorRef(Props(new ComputeSupervisor(new ComputeActorTestFactory)))
    computeSupervisor ! StartComputeActor("computeActor-1")

    val computeTestActor: ActorRef = expectMsgClass(classOf[ActorRef])
    val exception: ArithmeticException = new ArithmeticException
    computeTestActor ! exception

    computeTestActor ! IsRestarted
    expectMsg(false)
  }

  it should "restart compute actor on any exception other than arithmetic exception" in {

    val computeSupervisor = TestActorRef(Props(new ComputeSupervisor(new ComputeActorTestFactory)))
    computeSupervisor ! StartComputeActor("computeActor-1")

    val computeTestActor: ActorRef = expectMsgClass(classOf[ActorRef])

    computeTestActor ! new NumberFormatException("test exception")

    computeTestActor ! IsRestarted
    expectMsg(true)
  }

  it should "request num completed tasks from compute actor every configured interval" in {

    val computeActorFactory = mock[ComputeActorFactory]
    val probe = TestProbe()
    when(computeActorFactory.create(any[ActorContext], anyString())).thenReturn(probe.ref)

    val computeSupervisor = TestActorRef(Props(new ComputeSupervisor(computeActorFactory, 50 millis)))

    computeSupervisor ! StartComputeActor("computeActor-1")

    probe.expectMsg(100 millis, GetNumCompletedTasks)
    probe.expectMsg(100 millis, GetNumCompletedTasks)
  }

  it should "log num completed tasks from compute actor every configured interval on format 'Num completed tasks: <num_completed>'" in {

    val computeActorFactory = mock[ComputeActorFactory]
    val probe = TestProbe()
    when(computeActorFactory.create(any[ActorContext], anyString())).thenReturn(probe.ref)

    val computeSupervisor = TestActorRef(Props(new ComputeSupervisor(computeActorFactory, 50 millis)))

    computeSupervisor ! StartComputeActor("computeActor-1")

    // Throws timeout exception after 3 seconds if filter does not match
    EventFilter.info(start = "Num completed tasks", occurrences = 2).intercept( {
      probe.expectMsg(100 millis, GetNumCompletedTasks)
      probe.reply(NumCompletedTasks(0))

      probe.expectMsg(100 millis, GetNumCompletedTasks)
      probe.reply(NumCompletedTasks(1))
    })
  }
}
