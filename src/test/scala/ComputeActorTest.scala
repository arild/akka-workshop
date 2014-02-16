import akka.actor.{ActorSystem}
import akka.testkit.{TestActorRef, ImplicitSender, TestKit}
import org.scalatest.{BeforeAndAfterAll, FunSpecLike}


class ComputeActorTest(_system: ActorSystem) extends TestKit(_system)
with FunSpecLike
with ImplicitSender
with BeforeAndAfterAll {

  def this() = this(ActorSystem("testSystem"))

  override def afterAll {
    TestKit.shutdownActorSystem(system)
  }

  it("should handle addition") {
    val computeActor = TestActorRef[ComputeActor]

    computeActor ! Addition(9, 3)
    expectMsg(12)
    }

  it("should handle division") {
    val computeActor = TestActorRef[ComputeActor]

    computeActor ! Division(5, 2)
    expectMsg(2.5);
    }

  it("should return number of completed tasks") {
    val computeActor = TestActorRef[ComputeActor]

    computeActor ! GetNumCompletedTasks
    expectMsg(0)

    computeActor ! Addition(1, 1)
    expectMsgClass(classOf[Integer]) // Result from addition

    computeActor ! GetNumCompletedTasks
    expectMsg(1)

    computeActor ! Division(1, 1)
    expectMsgClass(classOf[Float]) // Result from division

    computeActor ! GetNumCompletedTasks
    expectMsg(2)
  }
}