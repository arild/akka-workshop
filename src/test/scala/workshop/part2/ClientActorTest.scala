package workshop.part2

import scala.language.postfixOps
import akka.testkit.TestProbe
import akka.actor._
import workshop.helpers.ComputeTestActor
import akka.actor.Terminated
import workshop.companion.{ClientActor, ComputeSupervisor}
import scala.concurrent.duration._
import workshop.AkkaSpec
import workshop.work.{RiskyWorkException, RiskyWork, RiskyAdditionResult, RiskyAddition}


class ClientActorTest extends AkkaSpec {

  val timeout: FiniteDuration = 50 millis

  trait Actors {
    val resultProbe = TestProbe()
    val computeSupervisor = system.actorOf(ComputeSupervisor.props(new ComputeActorFactory))
    val computeSupervisorProbe = TestProbe()
  }

  it should "start compute actor at startup" in new Actors {
    suppressStackTraceNoise {
      system.actorOf(ClientActor.props(computeSupervisorProbe.ref, resultProbe.ref, List()))
      computeSupervisorProbe.expectMsgClass(timeout, classOf[StartComputeActor])
    }
  }

  it should "stop if compute actor terminates" in {
    suppressStackTraceNoise {
      val computeTestActor = system.actorOf(Props(classOf[ComputeTestActor]))
      val computeSupervisorProbe = TestProbe()
      val clientActor = system.actorOf(ClientActor.props(computeSupervisorProbe.ref, mock[ActorRef], List()))

      computeSupervisorProbe.expectMsgClass(timeout, classOf[StartComputeActor])
      computeSupervisorProbe.reply(computeTestActor)

      watch(clientActor)
      computeTestActor ! PoisonPill // Poison pill makes actor terminate
      expectMsgClass(timeout, classOf[Terminated])
    }
  }

  it should "compute and send risky work result to ResultActor when work throws no exceptions" in {
    suppressStackTraceNoise {
      val work = List(RiskyAddition(2, 3), RiskyAddition(3, 3))

      val computeSupervisor = system.actorOf(ComputeSupervisor.props(new ComputeActorFactory))
      val resultProbe = TestProbe()
      system.actorOf(ClientActor.props(computeSupervisor, resultProbe.ref, work))

      resultProbe.expectMsg(timeout, RiskyAdditionResult(5))
      resultProbe.expectMsg(timeout, RiskyAdditionResult(6))
    }
  }

  it should "compute and send remaining risky work to ResultActor when work throws risky work exception" in {
    suppressStackTraceNoise {
      class WorkWithFailure extends RiskyWork {
        override def perform() = throw new RiskyWorkException("test exception")
      }

      val work = List(RiskyAddition(2, 3), new WorkWithFailure(), RiskyAddition(3, 3))

      val computeSupervisor = system.actorOf(ComputeSupervisor.props(new ComputeActorFactory))
      val resultProbe = TestProbe()
      system.actorOf(ClientActor.props(computeSupervisor, resultProbe.ref, work))

      resultProbe.expectMsg(timeout, RiskyAdditionResult(5))
      resultProbe.expectMsg(timeout, RiskyAdditionResult(6))
    }
  }
}
