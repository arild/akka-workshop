package workshop

import akka.actor._
import akka.testkit.{EventFilter, TestActorRef}
import org.mockito.Matchers._
import org.mockito.Mockito._
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

  it should "schedule a tick to itself and then receive a tick within resonable time" in {

    system.actorOf(Props(new ComputeSupervisor(new ComputeActorTestFactory)))


    // Throws timeout exception after 3 seconds if filter does not match
    EventFilter.info(message="ticktick", occurrences = 2).intercept( {
    })
  }
}
