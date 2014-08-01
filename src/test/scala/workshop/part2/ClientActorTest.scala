package workshop.part2

import scala.language.postfixOps
import akka.testkit.TestProbe
import akka.actor._
import workshop.helpers.ComputeTestActor
import scala.concurrent.duration._
import workshop.AkkaSpec
import workshop.work.RiskyWork
import workshop.work.RiskyWorkException
import workshop.work.RiskyAddition
import akka.actor.Terminated
import workshop.work.RiskyAdditionResult


class ClientActorTest extends AkkaSpec {

  val timeout: FiniteDuration = 50 millis

  it should "start compute actor at startup" in {
    suppressStackTraceNoise {
      val resultProbe = TestProbe()
      val computeSupervisorProbe = TestProbe()
      system.actorOf(Props(classOf[ClientActor], computeSupervisorProbe.ref, resultProbe.ref, List()))
      computeSupervisorProbe.expectMsgClass(timeout, classOf[StartComputeActor])
    }
  }

  it should "stop if compute actor terminates" in {
    suppressStackTraceNoise {
      val computeTestActor = system.actorOf(Props(classOf[ComputeTestActor]))
      val computeSupervisorProbe = TestProbe()
      val clientActor = system.actorOf(Props(classOf[ClientActor], computeSupervisorProbe.ref, mock[ActorRef], List()))

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

      val computeSupervisor = system.actorOf(Props(classOf[ComputeSupervisor], new ComputeActorFactory))
      val resultProbe = TestProbe()
      system.actorOf(Props(classOf[ClientActor], computeSupervisor, resultProbe.ref, work))

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

      val computeSupervisor = system.actorOf(Props(classOf[ComputeSupervisor], new ComputeActorFactory))
      val resultProbe = TestProbe()
      system.actorOf(Props(classOf[ClientActor], computeSupervisor, resultProbe.ref, work))

      resultProbe.expectMsg(timeout, RiskyAdditionResult(5))
      resultProbe.expectMsg(timeout, RiskyAdditionResult(6))
    }
  }
}
