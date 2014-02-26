package workshop.work

abstract class HeavyWork {
  def perform(): Any
}

class HeavyAddition(a: Int, b: Int, delay: Long = 0) extends HeavyWork {
  def perform(): Int = {
    Thread.sleep(delay)
    a + b
  }
}

case class HeavyWorkException(msg: String)  extends Exception
