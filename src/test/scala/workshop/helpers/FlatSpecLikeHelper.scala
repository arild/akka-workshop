package workshop.helpers

import org.scalatest.FlatSpecLike

trait FlatSpecLikeHelper extends FlatSpecLike {

  val PARALLEL_EXECUTION_LIMIT = 190

  def suppressStackTraceNoise[R](block: => R): R = {
    try {
      block
    } catch {
      case ae: AssertionError => {
        ae.setStackTrace(Array())
        throw ae
      }
      case e: Exception => throw e
    }
  }

  def expectParallel[R](block: => R): R = {
    val t0 = System.currentTimeMillis()
    val result = block
    val t1 = System.currentTimeMillis()
    if (t1 - t0 > PARALLEL_EXECUTION_LIMIT)
      fail("Did not compute work in parallel!")
    result
  }
}


