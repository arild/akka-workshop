package futures

import scala.concurrent.Future
import scala.concurrent.future
import scala.concurrent.Await
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import work._

object MyFutures {

  def computeSquare(n: Int): Future[Int] = {
    ???
  }

  def computeSquare(f: Future[Int]): Future[Int] = {
    ???
  }

  def findMaxFactor(work: FactorNumber): Future[Long] = {
    ???
  }

  def findMaxFactor(work: Future[FactorNumber]): Future[Long] = {
    ???
  }

  def computeRiskySumFallbackOnSafeSum(riskyWork: SumSequence, safeWork: SumSequence): Future[Int] = {
    ???
  }

  def findSumOfAllMaxFactors(work: Seq[FactorNumber]): Future[Long] = {
    ???
  }

  def findMaxFactorOfAllMaxFactorsInParallel(work: Seq[FactorNumber]): Future[Long] = {
    ???
  }
}