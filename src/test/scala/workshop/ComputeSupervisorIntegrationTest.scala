package workshop

import scala.concurrent.duration._
import akka.actor.{Terminated, ActorRef}
import workshop.work.{HeavyWorkException, HeavyWork}


class ComputeSupervisorIntegrationTest extends AkkaSpec {

  it should "resume compute actor on arithmetic exception" in {

    val computeActor: ActorRef = createAndWatchComputeActor()

    computeActor ! Addition(1, 2)
    expectMsg(3) // Result from addition

    computeActor ! Division(1, 0)

    // Should maintain state when being resumed
    computeActor ! GetNumCompletedTasks
    expectMsg(NumCompletedTasks(1))

    // Should not receive Terminated message due to watch()
    expectNoMsg(1 second)
  }

  it should "restart compute actor on heavy work exception" in {
    class TestWork extends HeavyWork {
      override def perform(): Any = throw new HeavyWorkException("test exception")
    }

    val computeActor: ActorRef = createAndWatchComputeActor()

    computeActor ! Addition(1, 2)
    expectMsg(3) // Result from addition

    computeActor ! new TestWork

    // Should NOT maintain state when being restarted
    computeActor ! GetNumCompletedTasks
    expectMsg(NumCompletedTasks(0))

    // Should not receive Terminated message due to watch()
    expectNoMsg(1 second)
  }

  it should "stop compute actor on any exception other than arithmetic and heavy work exception" in {
    class TestWork extends HeavyWork {
      override def perform(): Any = throw new NumberFormatException("test exception")
    }

    val computeActor: ActorRef = createAndWatchComputeActor()

    computeActor ! new TestWork

    expectMsgClass(500 millisecond, classOf[Terminated])
  }

  def createAndWatchComputeActor() = {
    val computeSupervisor = system.actorOf(ComputeSupervisor.props(new ComputeActorFactory))
    computeSupervisor ! StartComputeActor("computeActor-1")

    val computeActor: ActorRef = expectMsgClass(classOf[ActorRef])
    watch(computeActor)

    computeActor
  }

}
