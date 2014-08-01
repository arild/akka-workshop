package workshop.part1

import scala.language.postfixOps
import workshop.work._
import akka.actor._
import akka.testkit.{EventFilter, TestActorRef}
import scala.concurrent.duration._
import workshop.AkkaSpec

class ComputeActorTest extends AkkaSpec {

  trait Actor {
    val computeActor = TestActorRef(Props(classOf[ComputeActor], 1 second))
  }

  it should "compute length of a string and return the result" in new Actor {
    suppressStackTraceNoise {
      computeActor ! "abc"
      expectMsg(3)
    }
  }

  it should "compute division and return the result" in new Actor {
    suppressStackTraceNoise {
      computeActor ! Division(9, 3)
      expectMsg(3)
    }
  }

  it should "perform risky work and return the result" in new Actor {
    suppressStackTraceNoise {
      computeActor ! new RiskyAddition(3, 2)
      expectMsg(RiskyAdditionResult(5))
    }
  }

  it should "initially have zero completed tasks" in new Actor {
    suppressStackTraceNoise {
      computeActor ! GetNumCompletedTasks
      expectMsg(NumCompletedTasks(0))
    }
  }

  it should "increment number of completed tasks for each task sent" in new Actor {
    suppressStackTraceNoise {
      computeActor ! "abc"
      expectMsgClass(classOf[Int]) // Result from length of string

      computeActor ! GetNumCompletedTasks
      expectMsg(NumCompletedTasks(1))

      computeActor ! Division(1, 1)
      expectMsgClass(classOf[Int]) // Result from division

      computeActor ! GetNumCompletedTasks
      expectMsg(NumCompletedTasks(2))

      computeActor ! new RiskyAddition(3, 5)
      expectMsgClass(classOf[RiskyAdditionResult]) // Result from risky addition

      computeActor ! GetNumCompletedTasks
      expectMsg(NumCompletedTasks(3))
    }
  }

  it should "not increment number of completed tasks when division fails with arithmetic exception" in new Actor {
    suppressStackTraceNoise {
      // Prevents stack trace from displaying when running tests
      intercept[ArithmeticException] {
        computeActor.receive(Division(1, 0))
      }

      computeActor ! GetNumCompletedTasks
      expectMsg(NumCompletedTasks(0))
    }
  }

  it should "log num completed tasks every configured interval on format 'Num completed tasks: <num_completed>'" in {
    suppressStackTraceNoise {
      TestActorRef(Props(new ComputeActor(100 millis)))

      // Throws timeout exception after 3 seconds if filter does not match
      EventFilter.info(start = "Num completed tasks", occurrences = 2).intercept( {

      })
    }
  }
}
