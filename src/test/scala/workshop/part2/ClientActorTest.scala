package workshop.part2

import scala.language.postfixOps
import akka.testkit.{TestActorRef, TestProbe}
import akka.actor._
import workshop.helpers.ComputeTestActor
import workshop.AkkaSpec
import workshop.work.RiskyWork
import workshop.work.RiskyWorkException
import workshop.work.RiskyAddition
import akka.actor.Terminated
import workshop.work.RiskyAdditionResult


class ClientActorTest extends AkkaSpec {

  it should "start compute actor at startup" in {
    suppressStackTraceNoise {
      val resultProbe = TestProbe()
      val computeSupervisorProbe = TestProbe()
      createClientActor(computeSupervisorProbe.ref, resultProbe.ref, List())
      computeSupervisorProbe.expectMsgClass(classOf[StartComputeActor])
    }
  }

  it should "stop if compute actor terminates" in {
    suppressStackTraceNoise {
      val computeTestActor = system.actorOf(Props(classOf[ComputeTestActor]))
      val computeSupervisorProbe = TestProbe()
      val clientActor = createClientActor(computeSupervisorProbe.ref, mock[ActorRef], List())

      computeSupervisorProbe.expectMsgClass(classOf[StartComputeActor])
      computeSupervisorProbe.reply(computeTestActor)

      watch(clientActor)
      computeTestActor ! PoisonPill // Poison pill makes actor terminate
      expectMsgClass(classOf[Terminated])
    }
  }

  it should "compute and send risky work result to ResultActor when work throws no exceptions" in {
    suppressStackTraceNoise {
      val work = List(RiskyAddition(2, 3), RiskyAddition(3, 3))

      val computeSupervisor = createComputeSupervisor(new ComputeActorFactory)
      val resultProbe = TestProbe()
      createClientActor(computeSupervisor, resultProbe.ref, work)

      resultProbe.expectMsg(RiskyAdditionResult(5))
      resultProbe.expectMsg(RiskyAdditionResult(6))
    }
  }

  it should "compute and send remaining risky work to ResultActor when work throws risky work exception" in {
    suppressStackTraceNoise {
      class WorkWithFailure extends RiskyWork {
        override def perform() = throw new RiskyWorkException("test exception")
      }

      val work = List(RiskyAddition(2, 3), new WorkWithFailure(), RiskyAddition(3, 3))

      val computeSupervisor = createComputeSupervisor(new ComputeActorFactory)
      val resultProbe = TestProbe()
      createClientActor(computeSupervisor, resultProbe.ref, work)

      resultProbe.expectMsg(RiskyAdditionResult(5))
      resultProbe.expectMsg(RiskyAdditionResult(6))
    }
  }

  def createComputeSupervisor(computeActorFactory: ComputeActorFactory): TestActorRef[Nothing] = {
    TestActorRef(Props(classOf[ComputeSupervisor], computeActorFactory))
  }

  def createClientActor(computeSupervisor: ActorRef, resultActor: ActorRef, riskyWork: List[RiskyWork]) = {
    TestActorRef(Props(classOf[ClientActor], computeSupervisor, resultActor, riskyWork))
  }
}
