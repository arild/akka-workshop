package workshop

import akka.testkit.TestActorRef


class ComputeActorTest extends AkkaSpec {

  trait Actor {
    val computeActor = TestActorRef[ComputeActor]
  }

  it should "compute addition" in new Actor {
    computeActor ! Addition(9, 3)
    expectMsg(12)
  }

  it should "compute division" in new Actor {
    computeActor ! Division(9, 3)
    expectMsg(3)
  }

  it should "initially have zero completed tasks" in new Actor {
    computeActor ! GetNumCompletedTasks
    expectMsg(0)
  }

  it should "increment number of completed tasks" in new Actor {
    computeActor ! Addition(1, 1)
    expectMsgClass(classOf[Integer]) // Result from addition

    computeActor ! GetNumCompletedTasks
    expectMsg(1)

    computeActor ! Division(1, 1)
    expectMsgClass(classOf[Integer]) // Result from division

    computeActor ! GetNumCompletedTasks
    expectMsg(2)
  }

  it should "not increment number of completed tasks when division fails with arithmetic exception" in new Actor {
    // Prevents stack trace from displaying when running tests
    intercept[ArithmeticException] {
      computeActor.receive(Division(1, 0))
    }

    computeActor ! GetNumCompletedTasks
    expectMsg(0)
  }
}