package workshop

import akka.testkit.TestProbe
import akka.actor._
import workshop.helpers.ComputeTestActor
import akka.actor.Terminated
import work._


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

  it should "stop if compute actor terminates" in {
    val computeTestActor = system.actorOf(Props(classOf[ComputeTestActor]))
    val computeSupervisorProbe = TestProbe()
    val clientActor = system.actorOf(ClientActor.props(computeSupervisorProbe.ref, mock[ActorRef], List()))

    computeSupervisorProbe.expectMsgClass(classOf[StartComputeActor])
    computeSupervisorProbe.reply(computeTestActor)

    watch(clientActor)
    computeTestActor ! PoisonPill // Poison pill makes actor terminate
    expectMsgClass(classOf[Terminated])
  }

  it should "complete heavy work" in {
    val work = List(HeavyAddition(2, 3), HeavyAddition(3, 3))

    val computeSupervisor = system.actorOf(ComputeSupervisor.props(new ComputeActorFactory))
    val resultProbe = TestProbe()
    system.actorOf(ClientActor.props(computeSupervisor, resultProbe.ref, work))

    resultProbe.expectMsg(HeavyAdditionResult(5))
    resultProbe.expectMsg(HeavyAdditionResult(6))
  }

}
