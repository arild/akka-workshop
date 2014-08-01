package workshop.part2

import scala.language.postfixOps
import scala.concurrent.duration._
import akka.actor.{Props, Terminated, ActorRef}
import workshop.work.{RiskyWorkException, RiskyWork}
import workshop.part1.{NumCompletedTasks, GetNumCompletedTasks, Division}
import workshop.AkkaSpec


class ComputeSupervisorIntegrationTest extends AkkaSpec {
  val timeout: FiniteDuration = 100 millis

  it should "resume compute actor on arithmetic exception" in {
    suppressStackTraceNoise {
      val computeActor: ActorRef = createAndWatchComputeActor()

      computeActor ! "abc"
      expectMsg(timeout, 3) // Result from length of string

      computeActor ! Division(1, 0)

      // Should maintain state when being resumed
      computeActor ! GetNumCompletedTasks
      expectMsg(timeout, NumCompletedTasks(1))
    }
  }

  it should "restart compute actor on risky work exception" in {
    suppressStackTraceNoise {
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
    }
  }

  it should "stop compute actor on any exception other than arithmetic and risky work exception" in {
    suppressStackTraceNoise {
      class TestWork extends RiskyWork {
        override def perform() = throw new NumberFormatException("test exception")
      }

      val computeActor: ActorRef = createAndWatchComputeActor()

      watch(computeActor)
      computeActor ! new TestWork

      expectMsgClass(timeout, classOf[Terminated])
    }
  }

  def createAndWatchComputeActor() = {
    suppressStackTraceNoise {
      val computeSupervisor = system.actorOf(Props(classOf[ComputeSupervisor], new ComputeActorFactory))
      computeSupervisor ! StartComputeActor("computeActor-1")

      val computeActor: ActorRef = expectMsgClass(timeout, classOf[ActorRef])

      computeActor
    }
  }
}
