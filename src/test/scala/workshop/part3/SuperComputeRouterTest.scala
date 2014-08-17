package workshop.part3

import scala.language.postfixOps
import akka.actor._
import scala.concurrent.duration._
import workshop.work.{RiskyAdditionResult, RiskyAddition}
import workshop.AkkaSpec

class SuperComputeRouterTest extends AkkaSpec {

  val timeout: FiniteDuration = 1000 millis

  it should "compute 5 risky additions where none fail and where work is distributed on 5 routers" in  {
    suppressStackTraceNoise {
      val computeRouter = system.actorOf(Props(classOf[SuperComputeRouter]))

      val workList = Vector.fill(5) {
          RiskyAddition(1,1,150)
      }

      workList.foreach(computeRouter ! _)

      expectParallel(700) {
        var i = 0
        while(i < 5){
          expectMsgClass(timeout, classOf[RiskyAdditionResult])
          i = i + 1
        }
      }
    }
  }

}
