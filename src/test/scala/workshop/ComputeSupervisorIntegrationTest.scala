package workshop

import scala.concurrent.duration._
import akka.actor.{Terminated, ActorRef}
import workshop.work.{RiskyWorkException, RiskyWork}
import workshop.companion.ComputeSupervisor


class ComputeSupervisorIntegrationTest extends AkkaSpec {

  val timeout: FiniteDuration = 50 millis

  it should "resume compute actor on arithmetic exception" in {

    val computeActor: ActorRef = createAndWatchComputeActor()

    computeActor ! "abc"
    expectMsg(timeout, 3) // Result from length of string

    computeActor ! Division(1, 0)

    // Should maintain state when being resumed
    computeActor ! GetNumCompletedTasks
    expectMsg(timeout, NumCompletedTasks(1))

    // Should not receive Terminated message due to watch()
    expectNoMsg(timeout)
  }

  it should "restart compute actor on risky work exception" in {
    class TestWork extends RiskyWork {
      override def perform() = throw new RiskyWorkException("test exception")
    }

    val computeActor: ActorRef = createAndWatchComputeActor()

    computeActor ! "abc"
    expectMsg(timeout, 3) // Result from length of string

    computeActor ! new TestWork

    // Should NOT maintain state when being restarted
    computeActor ! GetNumCompletedTasks
    expectMsg(timeout, NumCompletedTasks(0))

    // Should not receive Terminated message due to watch()
    expectNoMsg(timeout)
  }

  it should "stop compute actor on any exception other than arithmetic and risky work exception" in {
    class TestWork extends RiskyWork {
      override def perform() = throw new NumberFormatException("test exception")
    }

    val computeActor: ActorRef = createAndWatchComputeActor()

    computeActor ! new TestWork

    expectMsgClass(timeout, classOf[Terminated])
  }

  def createAndWatchComputeActor() = {
    val computeSupervisor = system.actorOf(ComputeSupervisor.props(new ComputeActorFactory))
    computeSupervisor ! StartComputeActor("computeActor-1")

    val computeActor: ActorRef = expectMsgClass(timeout, classOf[ActorRef])
    watch(computeActor)

    computeActor
  }

}
