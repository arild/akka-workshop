import akka.actor.{ActorRef, Props, ActorSystem}
import akka.testkit.{TestActorRef, ImplicitSender, TestKit}
import org.scalatest.{BeforeAndAfterAll, FunSpecLike}
import scala.concurrent.duration._

class ComputeSupervisorTest(_system: ActorSystem) extends TestKit(_system)
with FunSpecLike
with ImplicitSender
with BeforeAndAfterAll {

  def this() = this(ActorSystem("testSystem"))

  override def afterAll {
    TestKit.shutdownActorSystem(system)
  }

  it("should start ComputeActor and return an ActorRef") {
    val computeSupervisor = TestActorRef(Props(new ComputeSupervisor(new ComputeActorFactory)))

    computeSupervisor ! StartComputeActor("compueActor1")

    within(500 millis){
      expectMsgClass(classOf[ActorRef])
    }

  }
}
