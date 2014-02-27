package workshop

import akka.testkit.TestProbe
import akka.actor.{ActorContext, ActorRef}
import org.mockito.Matchers._
import org.mockito.Mockito._

class ClientActorTest extends AkkaSpec {

  trait Actors {
    val resultProbe = TestProbe()
    val computeSupervisor = system.actorOf(ComputeSupervisor.props(new ComputeActorFactory))
    val computeSupervisorProbe = TestProbe()
  }

  it should "start compute actor at startup" in new Actors {
    val clientActor = system.actorOf(ClientActor.props(computeSupervisorProbe.ref, resultProbe.ref, List()))

    computeSupervisorProbe.expectMsgClass(classOf[StartComputeActor])
  }

  it should "stop if compute actor terminates" in new Actors {
    val clientActor = system.actorOf(ClientActor.props(computeSupervisor, mock[ActorRef], List()))

    val computeActorFactory = mock[ComputeActorFactory]
    val computeActor: ActorRef = mock[ActorRef]
    when(computeActorFactory.create(any[ActorContext], anyString())).thenReturn(computeActor)

  }

}
