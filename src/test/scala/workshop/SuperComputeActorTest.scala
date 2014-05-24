package workshop

import scala.language.postfixOps
import akka.actor._
import work._
import scala.concurrent.duration._
import scala.collection.immutable.Seq
import workshop.helpers.AkkaSpecHelper.supressStackTraceNoise

class SuperComputeActorTest extends AkkaSpec {

  val timeout: FiniteDuration = 150 millis

  it should "compute risky work without failures" in  {
    supressStackTraceNoise{
      val superComputeActor = system.actorOf(Props(classOf[SuperComputeActor]))

      superComputeActor ! RiskyAddition(1,3,0)

      expectMsg(timeout, RiskyAdditionResult(4))
    }
  }

  it should "compute risky work without failures in parallel" in  {
    supressStackTraceNoise{
      val superComputeActor = system.actorOf(Props(classOf[SuperComputeActor]))

      val workList = List(RiskyAddition(1, 3, 100), RiskyAddition(2,3,100),  RiskyAddition(4,2,100))
      val workListResults = List(RiskyAdditionResult(4),RiskyAdditionResult(5),RiskyAdditionResult(6))

      workList.foreach(
        w => superComputeActor ! w
      )

      val sequenceResult: Seq[RiskyAdditionResult] = expectMsgAllClassOf(timeout, classOf[RiskyAdditionResult])

      sequenceResult.seq.foreach(workListResults should contain (_))
    }
  }

  it should "compute risky work with failures in parallel" in  {
    supressStackTraceNoise{
      class WorkWithFailure extends RiskyWork {
        override def perform() = throw new RiskyWorkException("test exception")
      }
      val superComputeActor = system.actorOf(Props(classOf[SuperComputeActor]))

      val workList = List(RiskyAddition(1, 3, 100), new WorkWithFailure(),  RiskyAddition(4,2,100))
      val workListResults = List(RiskyAdditionResult(4),RiskyAdditionResult(6))

      workList.foreach(
        w => superComputeActor ! w
      )

      val sequenceResult: Seq[RiskyAdditionResult] = expectMsgAllClassOf(timeout, classOf[RiskyAdditionResult])

      sequenceResult.seq.foreach(workListResults should contain (_))
    }
  }

}
