package workshop

import akka.actor._
import akka.testkit.{TestActorRef, TestKit}
import scala.concurrent.duration._
import org.mockito.Matchers._
import org.mockito.Mockito._
import workshop.helpers._


class ComputeSupervisorTest extends AkkaSpec {

  override def afterAll {
    TestKit.shutdownActorSystem(system)
  }

  it should "start compute actor and return its reference" in {

    val computeActorFactory = mock[ComputeActorFactory]
    val computeActor: ActorRef = mock[ActorRef]
    when(computeActorFactory.create(any[ActorContext], anyString())).thenReturn(computeActor)

    val computeSupervisor = TestActorRef(Props(new ComputeSupervisor(computeActorFactory)))

    computeSupervisor ! StartComputeActor("computeActor-1")

    within(500 millis){
      val actor: ActorRef = expectMsgClass(classOf[ActorRef])
      actor shouldBe computeActor
    }
  }

  it should "should resume compute actor on arithmetic exception" in {

    val computeSupervisor = TestActorRef(Props(new ComputeSupervisor(new ComputeActorTestFactory)))
    computeSupervisor ! StartComputeActor("computeActor-1")

    val computeTestActor: ActorRef = expectMsgClass(classOf[ActorRef])
    val exception: ArithmeticException = new ArithmeticException
    computeTestActor ! exception

    computeTestActor ! IsRestarted
    expectMsg(false)
  }

  it should "should restart compute actor on any exception other than arithmetic exception" in {

    val computeSupervisor = TestActorRef(Props(new ComputeSupervisor(new ComputeActorTestFactory)))
    computeSupervisor ! StartComputeActor("computeActor-1")

    val computeTestActor: ActorRef = expectMsgClass(classOf[ActorRef])


      computeTestActor ! new NumberFormatException("test exception")

      computeTestActor ! IsRestarted
      expectMsg(true)
  }
}
