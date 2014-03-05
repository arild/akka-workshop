package workshop

import workshop.work._
import akka.actor._
import akka.testkit.{EventFilter, TestActorRef}
import scala.concurrent.duration._
import workshop.companion.ComputeActor


class ComputeActorTest extends AkkaSpec {

  trait Actor {
    val computeActor = TestActorRef(ComputeActor.props(1 second))
  }

  it should "compute addition" in new Actor {
    computeActor ! Addition(9, 3)
    expectMsg(12)
  }

  it should "compute division" in new Actor {
    computeActor ! Division(9, 3)
    expectMsg(3)
  }

  it should "perform heavy work" in new Actor {
    computeActor ! new HeavyAddition(3, 2)
    expectMsg(HeavyAdditionResult(5))
  }

  it should "initially have zero completed tasks" in new Actor {
    computeActor ! GetNumCompletedTasks
    expectMsg(NumCompletedTasks(0))
  }

  it should "increment number of completed tasks" in new Actor {
    computeActor ! Addition(1, 1)
    expectMsgClass(classOf[Int]) // Result from addition

    computeActor ! GetNumCompletedTasks
    expectMsg(NumCompletedTasks(1))

    computeActor ! Division(1, 1)
    expectMsgClass(classOf[Int]) // Result from division

    computeActor ! GetNumCompletedTasks
    expectMsg(NumCompletedTasks(2))

    computeActor ! new HeavyAddition(3, 5)
    expectMsgClass(classOf[HeavyAdditionResult]) // Result from heavy addition

    computeActor ! GetNumCompletedTasks
    expectMsg(NumCompletedTasks(3))
  }

  it should "not increment number of completed tasks when division fails with arithmetic exception" in new Actor {
    // Prevents stack trace from displaying when running tests
    intercept[ArithmeticException] {
      computeActor.receive(Division(1, 0))
    }

    computeActor ! GetNumCompletedTasks
    expectMsg(NumCompletedTasks(0))
  }

  it should "log num completed tasks every configured interval on format 'Num completed tasks: <num_completed>'" in {

    TestActorRef(Props(new ComputeActor(100 millis)))

    // Throws timeout exception after 3 seconds if filter does not match
    EventFilter.info(start = "Num completed tasks", occurrences = 2).intercept( {

    })
  }
}