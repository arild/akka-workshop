import akka.actor._
import akka.testkit.{TestActorRef, ImplicitSender, TestKit}
import org.scalatest.{BeforeAndAfterAll, FunSpecLike}


class ComputeActorTest extends TestKit(ActorSystem("testSystem"))
with FunSpecLike
with ImplicitSender
with BeforeAndAfterAll {

  override def afterAll {
    TestKit.shutdownActorSystem(system)
  }

  it("should compute addition") {
    val computeActor = TestActorRef[ComputeActor]

    computeActor ! Addition(9, 3)
    expectMsg(12)
  }

  it("should compute division") {
    val computeActor = TestActorRef[ComputeActor]

    computeActor ! Division(9, 3)
    expectMsg(3)
    }

  it("should initially have zero completed tasks") {
    val computeActor = TestActorRef[ComputeActor]

    computeActor ! GetNumCompletedTasks
    expectMsg(0)
  }

  it("should increment number of completed tasks") {
    val computeActor = TestActorRef[ComputeActor]

    computeActor ! Addition(1, 1)
    expectMsgClass(classOf[Integer]) // Result from addition

    computeActor ! GetNumCompletedTasks
    expectMsg(1)

    computeActor ! Division(1, 1)
    expectMsgClass(classOf[Integer]) // Result from division

    computeActor ! GetNumCompletedTasks
    expectMsg(2)
  }

  it("should not increment number of completed tasks when division fails with arithmetic exception") {
    val computeActor = TestActorRef[ComputeActor]

    // Prevents stack trace from displaying when running tests
    intercept[ArithmeticException] {
      computeActor.receive(Division(1, 0))
    }

    computeActor ! GetNumCompletedTasks
    expectMsg(0)
  }
}