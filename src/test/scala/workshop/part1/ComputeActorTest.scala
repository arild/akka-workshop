package workshop.part1

import scala.concurrent.duration.Duration.Zero
import scala.language.postfixOps
import workshop.work._
import akka.actor._
import akka.testkit.{TestProbe, EventFilter, TestActorRef}
import scala.concurrent.duration._
import workshop.AkkaSpec

class ComputeActorTest extends AkkaSpec {

  trait Actor {
    val computeActor = TestActorRef(Props(classOf[ComputeActor], TestProbe().ref, 1 second))
  }

  it should "compute length of a string and return the result" in new Actor {
    suppressStackTraceNoise {
      computeActor ! "abc"
      expectMsg(Zero, 3)
    }
  }

  it should "compute division and return the result" in new Actor {
    suppressStackTraceNoise {
      computeActor ! Division(9, 3)
      expectMsg(Zero, 3)
    }
  }

  it should "perform risky work and return the result" in new Actor {
    suppressStackTraceNoise {
      computeActor ! new RiskyAddition(3, 2)
      expectMsg(Zero, RiskyAdditionResult(5))
    }
  }

  it should "initially have zero completed tasks" in new Actor {
    suppressStackTraceNoise {
      computeActor ! GetNumCompletedTasks
      expectMsg(Zero, NumCompletedTasks(0))
    }
  }

  it should "increment number of completed tasks for each task sent" in new Actor {
    suppressStackTraceNoise {
      computeActor ! "abc"
      expectMsgClass(Zero, classOf[Int]) // Result from length of string

      computeActor ! GetNumCompletedTasks
      expectMsg(Zero, NumCompletedTasks(1))

      computeActor ! Division(1, 1)
      expectMsgClass(Zero, classOf[Int]) // Result from division

      computeActor ! GetNumCompletedTasks
      expectMsg(Zero, NumCompletedTasks(2))

      computeActor ! new RiskyAddition(3, 5)
      expectMsgClass(Zero, classOf[RiskyAdditionResult]) // Result from risky addition

      computeActor ! GetNumCompletedTasks
      expectMsg(Zero, NumCompletedTasks(3))
    }
  }

  it should "not increment number of completed tasks when division fails with arithmetic exception" in new Actor {
    suppressStackTraceNoise {
      // Prevents stack trace from displaying when running tests
      intercept[ArithmeticException] {
        computeActor.receive(Division(1, 0))
      }

      computeActor ! GetNumCompletedTasks
      expectMsg(Zero, NumCompletedTasks(0))
    }
  }

  it should "send number of completed tasks to the numCompletedTaskActor every interval given by the logCompletedTasksInterval" in {
    suppressStackTraceNoise {
      val numCompletedTasksActor = TestProbe()
      val computeActor = TestActorRef(Props(classOf[ComputeActor], numCompletedTasksActor.ref, 100 millis))

      numCompletedTasksActor.expectMsg(150 millis, NumCompletedTasks(0))

      computeActor ! Division(1, 1)
      expectMsgClass(Zero, classOf[Int]) // Result from division

      numCompletedTasksActor.expectMsg(150 millis, NumCompletedTasks(1))
    }
  }
}
