package workshop.work

abstract class RiskyWork {
  def perform(): RiskyWorkResult
}
abstract class RiskyWorkResult

case class RiskyWorkException(msg: String)  extends Exception
case class RiskyAddition(a: Int, b: Int, delay: Long = 0) extends RiskyWork {
  def perform() = {
    Thread.sleep(delay)
    RiskyAdditionResult(a + b)
  }
}
case class RiskyAdditionResult(result: Int) extends RiskyWorkResult
