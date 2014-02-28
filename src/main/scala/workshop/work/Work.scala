package workshop.work

abstract class HeavyWork {
  def perform(): HeavyWorkResult
}
abstract class HeavyWorkResult

case class HeavyWorkException(msg: String)  extends Exception
case class HeavyAddition(a: Int, b: Int, delay: Long = 0) extends HeavyWork {
  def perform() = {
    Thread.sleep(delay)
    HeavyAdditionResult(a + b)
  }
}
case class HeavyAdditionResult(result: Int) extends HeavyWorkResult
