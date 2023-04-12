import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

object GradeCalculatorDriver extends App {

  val path = GradeCalculator.calculateGrades("/home/knoldus/Scala Assignment-KUP/Session-5/Average/src/main/scala/Scores.csv")
  path.onComplete {
    case Success(classAverage) => println(s"Class average: $classAverage")
    case Failure(ex) => println(s"Error calculating grades: ${ex.getMessage}")
  }
  Thread.sleep(100)
}
