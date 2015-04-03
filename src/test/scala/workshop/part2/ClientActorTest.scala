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

  it should "tell the supervisor to create a new compute actor at startup" in {
    suppressStackTraceNoise {
      val resultProbe = TestProbe()
      val computeSupervisorProbe = TestProbe()
      createClientActor(computeSupervisorProbe.ref, resultProbe.ref, List())
      computeSupervisorProbe.expectMsgClass(Zero, classOf[CreateComputeActor])
    }
  }

  it should "receive a computeActor ref from the supervisor and delegate all risky work to it" in {
    suppressStackTraceNoise {
      val computeSupervisorProbe = TestProbe()
      val computeActorProbe = TestProbe()
      val work = List(RiskyAddition(1, 3), RiskyAddition(1, 5), RiskyAddition(6, 3))
      val clientActor = createClientActor(computeSupervisorProbe.ref, mock[ActorRef], work)

      computeSupervisorProbe.expectMsgClass(Zero, classOf[CreateComputeActor])
      clientActor.tell(computeActorProbe.ref, computeSupervisorProbe.ref)

      computeActorProbe.expectMsgClass(timeout, classOf[RiskyAddition])
      computeActorProbe.expectMsgClass(timeout, classOf[RiskyAddition])
      computeActorProbe.expectMsgClass(timeout, classOf[RiskyAddition])
    }
  }

  it should "stop if compute actor terminates" in {
    suppressStackTraceNoise {
      val computeTestActor = TestActorRef(Props(classOf[ComputeTestActor]))
      val computeSupervisorProbe = TestProbe()
      val clientActor = createClientActor(computeSupervisorProbe.ref, mock[ActorRef], List())

      computeSupervisorProbe.expectMsgClass(Zero, classOf[CreateComputeActor])
      computeSupervisorProbe.reply(computeTestActor)

      watch(clientActor)
      computeTestActor ! PoisonPill // Poison pill makes actor terminate
      expectMsgClass(timeout, classOf[Terminated])
    }
  }

  it should "compute and send risky work result to ResultActor when work throws no exceptions" in {
    suppressStackTraceNoise {
      val computeSupervisor = createComputeSupervisor
      val resultProbe = TestProbe()
      val work = List(RiskyAddition(2, 3), RiskyAddition(3, 3))
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
      val computeSupervisor = createComputeSupervisor
      val resultProbe = TestProbe()
      val work = List(RiskyAddition(2, 3), new WorkWithFailure(), RiskyAddition(3, 3))
      createClientActor(computeSupervisor, resultProbe.ref, work)

      resultProbe.expectMsg(timeout, RiskyAdditionResult(5))
      resultProbe.expectMsg(timeout, RiskyAdditionResult(6))
    }
  }

  def createComputeSupervisor: TestActorRef[Nothing] = {
    val computeActorFactory = new ComputeActorFactory(mock[ActorRef])
    TestActorRef(Props(classOf[ComputeSupervisor], computeActorFactory))
  }

  def createClientActor(computeSupervisor: ActorRef, resultActor: ActorRef, riskyWork: List[RiskyWork]) = {
    TestActorRef(Props(classOf[ClientActor], computeSupervisor, resultActor, riskyWork))
  }
}
