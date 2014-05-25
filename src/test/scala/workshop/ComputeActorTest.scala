package workshop

import scala.language.postfixOps
import workshop.work._
import akka.actor._
import akka.testkit.{EventFilter, TestActorRef}
import scala.concurrent.duration._
import workshop.companion.ComputeActor

class ComputeActorTest extends AkkaSpec {

  val timeout: FiniteDuration = 50 millis

  trait Actor {
    val computeActor = TestActorRef(ComputeActor.props(1 second))
  }

  it should "compute length of string" in new Actor {
    suppressStackTraceNoise {
      computeActor ! "abc"
      expectMsg(timeout, 3)
    }
  }

  it should "compute division" in new Actor {
    suppressStackTraceNoise {
      computeActor ! Division(9, 3)
      expectMsg(timeout, 3)
    }
  }

  it should "perform risky work" in new Actor {
    suppressStackTraceNoise {
      computeActor ! new RiskyAddition(3, 2)
      expectMsg(timeout, RiskyAdditionResult(5))
    }
  }

  it should "initially have zero completed tasks" in new Actor {
    suppressStackTraceNoise {
      computeActor ! GetNumCompletedTasks
      expectMsg(timeout, NumCompletedTasks(0))
    }
  }

  it should "increment number of completed tasks" in new Actor {
    suppressStackTraceNoise {
      computeActor ! "abc"
      expectMsgClass(timeout, classOf[Int]) // Result from length of string

      computeActor ! GetNumCompletedTasks
      expectMsg(timeout, NumCompletedTasks(1))

      computeActor ! Division(1, 1)
      expectMsgClass(timeout, classOf[Int]) // Result from division

      computeActor ! GetNumCompletedTasks
      expectMsg(timeout, NumCompletedTasks(2))

      computeActor ! new RiskyAddition(3, 5)
      expectMsgClass(timeout, classOf[RiskyAdditionResult]) // Result from risky addition

      computeActor ! GetNumCompletedTasks
      expectMsg(timeout, NumCompletedTasks(3))
    }
  }

  it should "not increment number of completed tasks when division fails with arithmetic exception" in new Actor {
    suppressStackTraceNoise {
      // Prevents stack trace from displaying when running tests
      intercept[ArithmeticException] {
        computeActor.receive(Division(1, 0))
      }

      computeActor ! GetNumCompletedTasks
      expectMsg(timeout, NumCompletedTasks(0))
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
