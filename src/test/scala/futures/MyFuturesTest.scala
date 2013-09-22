package test.scala.futures

import scala.concurrent.future
import scala.concurrent.Await
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

import work._
import futures.MyFutures
import funsuitehelper.FlatSpecHelper

import org.scalatest.matchers.ShouldMatchers

class MyFuturesTest extends FlatSpecHelper with ShouldMatchers {

  def delayFactorNumber(n: Long): FactorNumber = new FactorNumber(n, FlatSpecHelper.FUTURE_TIME_LIMIT * 2)

    it should "should compute square" in {
    checkImplemented {
      val future = time {
        MyFutures.computeSquare(4)
      }
      val result = Await.result(future, Duration.Inf)
      result should equal (16)
    }
  }

  it should "should compute square of future value" in {
    checkImplemented {
      val futureValue = future {
        Thread.sleep(FlatSpecHelper.FUTURE_TIME_LIMIT * 2)
        4
      }
      val futureResult = time {
        MyFutures.computeSquare(futureValue)
      }
      val result = Await.result(futureResult, Duration.Inf)
      result should equal (16)
    }
  }

  it should "should find max factor" in {
    checkImplemented {
      val work = delayFactorNumber(49L)
      val futureResult = time {
        MyFutures.findMaxFactor(work)
      }
      val result = Await.result(futureResult, Duration.Inf)
      result should equal (7L)
    }
  }

  it should "should find max factor of future factors" in {
    checkImplemented {
      val futureFactors = future {
        delayFactorNumber(49L)
      }
      val futureResult = time {
        MyFutures.findMaxFactor(futureFactors)
      }
      val result = Await.result(futureResult, Duration.Inf)
      result should equal (7L)
    }
  }

  it should "do risky work or fallback on safe work" in {
    checkImplemented{
      // Each work will exceed the time limit
      val shouldNotDoWork = new SumSequence(0, 4, FlatSpecHelper.FUTURE_TIME_LIMIT + 1)
      val safeWork = new SumSequence(0, 5, FlatSpecHelper.FUTURE_TIME_LIMIT + 1)
      val riskyWork = new SumSequence(-1, 6, FlatSpecHelper.FUTURE_TIME_LIMIT + 1)


      val futureSafeResult = time {
        MyFutures.computeRiskySumFallbackOnSafeSum(safeWork, shouldNotDoWork)
      }
      val futureSafeResult2 = time {
        MyFutures.computeRiskySumFallbackOnSafeSum(riskyWork, safeWork)
      }

      val result = Await.result(futureSafeResult, Duration.Inf)
      val result2 = Await.result(futureSafeResult2, Duration.Inf)
      result should equal (15)
      result2 should equal (15)
    }
  }

  it should "find sum of all max factors" in {
    checkImplemented {
      val work1 = Seq(delayFactorNumber(21L), delayFactorNumber(49L), delayFactorNumber(12L))
      val work2 = Seq(delayFactorNumber(51L), delayFactorNumber(81L))

      val futureResult1 = time {
        MyFutures.findSumOfAllMaxFactors(work1)
      }
      val futureResult2 = time {
        MyFutures.findSumOfAllMaxFactors(work2)
      }

      val result1 = Await.result(futureResult1, Duration.Inf)
      result1 should equal (20L)
      val result2 = Await.result(futureResult2, Duration.Inf)
      result2 should equal (44L)
    }
  }

  it should "find max factor of all max factors in parallel" in {
    checkImplemented {
      // Each work will take at least 100 milliseconds
      val work = Seq(delayFactorNumber(49L), delayFactorNumber(12L), delayFactorNumber(21L), delayFactorNumber(54L))

      val futureResult = time {
        MyFutures.findMaxFactorOfAllMaxFactorsInParallel(work)
      }
      val t1 = System.currentTimeMillis()
      val result = Await.result(futureResult, Duration.Inf)
      result should equal (27)
      val totalExecutionTime = System.currentTimeMillis() - t1
      totalExecutionTime should be < (FlatSpecHelper.FUTURE_TIME_LIMIT * 7)
      println("Parallel execution time: " + totalExecutionTime)
    }
  }
}


