package workshop.helpers

object AkkaSpecHelper {

    def supressStackTraceNoise[R](block: => R): R = {
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



}


