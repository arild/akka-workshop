package workshop

import scala.language.postfixOps
import akka.actor._
import scala.concurrent.duration._
import workshop.work.{RiskyAdditionResult, RiskyAddition}
import scala.util.Random

class ComputeRouterTest extends AkkaSpec {

  val timeout: FiniteDuration = 200 millis

  it should "compute 5 risky additions where noone failes with work distributed on 5 routers" in  {
    suppressStackTraceNoise {
      val computeRouter = system.actorOf(Props(classOf[ComputeRouter]))

      val workList = Vector.fill(5) {
          RiskyAddition(1,1,50)
      }

      workList.foreach(computeRouter ! _)

      expectParallel(200) {
        var i = 0
        while(i < 5){
          expectMsgClass(timeout, classOf[RiskyAdditionResult])
          i = i + 1
        }
      }
    }
  }

}
