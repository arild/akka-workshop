package workshop.part3

import scala.language.postfixOps
import akka.actor._
import scala.concurrent.duration._
import workshop.AkkaSpec
import workshop.work.{RiskyWorkException, RiskyWork, RiskyAdditionResult, RiskyAddition}


class SuperComputeActorTest extends AkkaSpec {
  val timeout: FiniteDuration = 200 millis

  it should "compute risky work without failures" in  {
    suppressStackTraceNoise {
      val superComputeActor = system.actorOf(Props(classOf[SuperComputeActor]))

      superComputeActor ! RiskyAddition(1,3,0)
      expectMsg(RiskyAdditionResult(4))
    }
  }

  it should "compute risky work without failures in parallel" in  {
    suppressStackTraceNoise {
      val superComputeActor = system.actorOf(Props(classOf[SuperComputeActor]))

      val workList = List(RiskyAddition(1,3,100), RiskyAddition(2,3,100),  RiskyAddition(4,2,100))
      val workListResults = List(RiskyAdditionResult(4),RiskyAdditionResult(5),RiskyAdditionResult(6))

      workList.foreach(
        w => superComputeActor ! w
      )

      expectParallel(200) {
        val result1 = expectMsgClass(timeout, classOf[RiskyAdditionResult])
        val result2 = expectMsgClass(timeout, classOf[RiskyAdditionResult])
        val result3 = expectMsgClass(timeout, classOf[RiskyAdditionResult])
        List(result1, result2, result3).foreach(workListResults should contain (_))
      }
    }
  }

  it should "compute risky work with failures in parallel" in  {
    suppressStackTraceNoise {
      class WorkWithFailure extends RiskyWork {
        override def perform() = throw new RiskyWorkException("test exception")
      }
      val superComputeActor = system.actorOf(Props(classOf[SuperComputeActor]))

      val workList = List(RiskyAddition(1, 3, 100), new WorkWithFailure(),  RiskyAddition(4,2,100))
      val workListResults = List(RiskyAdditionResult(4),RiskyAdditionResult(6))

      workList.foreach(
        w => superComputeActor ! w
      )

      expectParallel(180) {
        val result1 = expectMsgClass(timeout, classOf[RiskyAdditionResult])
        val result2 = expectMsgClass(timeout, classOf[RiskyAdditionResult])
        List(result1, result2).foreach(workListResults should contain (_))
      }
    }
  }
}
