package workshop.part2

import akka.actor.{Terminated, _}
import akka.testkit.{TestActorRef, TestProbe}
import workshop.AkkaSpec
import workshop.helpers.ComputeTestActor
import workshop.work.{RiskyAddition, RiskyAdditionResult, RiskyWork, RiskyWorkException}

import scala.concurrent.duration.Duration.Zero
import scala.concurrent.duration._
import scala.language.postfixOps

class ClientActorTest extends AkkaSpec {

  val timeout: FiniteDuration = 75 millis

  it should "start compute actor at startup" in {
    suppressStackTraceNoise {
      val resultProbe = TestProbe()
      val computeSupervisorProbe = TestProbe()
      createClientActor(computeSupervisorProbe.ref, resultProbe.ref, List())
      computeSupervisorProbe.expectMsgClass(Zero, classOf[StartComputeActor])
    }
  }

  it should "stop if compute actor terminates" in {
    suppressStackTraceNoise {
      val computeTestActor = TestActorRef(Props(classOf[ComputeTestActor]))
      val computeSupervisorProbe = TestProbe()
      val clientActor = createClientActor(computeSupervisorProbe.ref, mock[ActorRef], List())

      computeSupervisorProbe.expectMsgClass(Zero, classOf[StartComputeActor])
      computeSupervisorProbe.reply(computeTestActor)

      watch(clientActor)
      computeTestActor ! PoisonPill // Poison pill makes actor terminate
      expectMsgClass(timeout, classOf[Terminated])
    }
  }

  it should "compute and send risky work result to ResultActor when work throws no exceptions" in {
    suppressStackTraceNoise {
      val work = List(RiskyAddition(2, 3), RiskyAddition(3, 3))

      val computeSupervisor = createComputeSupervisor(new ComputeActorFactory)
      val resultProbe = TestProbe()
      createClientActor(computeSupervisor, resultProbe.ref, work)

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

      val computeSupervisor = createComputeSupervisor(new ComputeActorFactory)
      val resultProbe = TestProbe()
      createClientActor(computeSupervisor, resultProbe.ref, work)

      resultProbe.expectMsg(timeout, RiskyAdditionResult(5))
      resultProbe.expectMsg(timeout, RiskyAdditionResult(6))
    }
  }

  def createComputeSupervisor(computeActorFactory: ComputeActorFactory): TestActorRef[Nothing] = {
    TestActorRef(Props(classOf[ComputeSupervisor], computeActorFactory))
  }

  def createClientActor(computeSupervisor: ActorRef, resultActor: ActorRef, riskyWork: List[RiskyWork]) = {
    TestActorRef(Props(classOf[ClientActor], computeSupervisor, resultActor, riskyWork))
  }
}
