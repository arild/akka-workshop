package workshop

import scala.language.postfixOps
import akka.actor._
import scala.concurrent.duration._
import workshop.work.{RiskyAdditionResult, RiskyAddition}
import scala.util.Random

class ComputeRouterTest extends AkkaSpec {

  val timeout: FiniteDuration = 20 millis

  it should "compute 100 risky additions where noone failes with work distributed on 5 routers" in  {
    suppressStackTraceNoise {
      val computeRouter = system.actorOf(Props(classOf[ComputeRouter]))

      val nrOfWork: Int = 100
      
      val workList = Vector.fill(nrOfWork) {
        val rnd: Float = Random.nextFloat()
        if (rnd < 0.2)
          RiskyAddition(1,1,1)
        else if (rnd < 0.4)
          RiskyAddition(1,1,2)
        else if (rnd < 0.6)
          RiskyAddition(1,1,3)
        else if (rnd < 0.8)
          RiskyAddition(1,1,4)
        else
          RiskyAddition(1,1,5)
      }

      workList.foreach(
        w => {
          computeRouter ! w
        }
      )

      expectParallel(700) {
        var i = 0
        while(i < nrOfWork){
          expectMsgClass(timeout, classOf[RiskyAdditionResult])
          i = i + 1
        }
      }
    }
  }

}
