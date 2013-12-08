import akka.actor.ActorSystem
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

  it("should handle division") {
      val actorRef = TestActorRef[ComputeActor]
      actorRef ! Division(9, 3)

      // This method assert that the `testActor` has received a specific message
      expectMsg(3)
    }
}