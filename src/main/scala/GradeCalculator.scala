
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.io.Source
import scala.util.{Failure, Success, Try}

object GradeCalculator {

  //ParseCsv perform the read operation on the score.csv file and convert it into the List[Map[String, String]]
  private def parseCsv(path: String): Future[List[Map[String, String]]] = Future {
    Try(Source.fromFile(path)) match {
      case Success(file) =>
        val scoreLines = file.getLines().toList
        val headerInfo = scoreLines.head.split(",").toList
        val marksList = scoreLines.tail.map { line =>
          val cols = line.split(",").toList
          headerInfo.zip(cols).toMap
        }
        marksList
      case Failure(ex) => throw new Exception(s"Error reading file $path ${ex.getMessage}")
    }
  }

  private def calculateStudentAverages(data: Future[List[Map[String, String]]]): Future[List[(String, Double)]] = {
    data.map { rows =>
      rows.map {
        row =>
          val studentId = row("StudentID")
          val grades = List(row("English"), row("Physics"), row("Chemistry"), row("Maths")).map(_.toDouble)
          val averageGrade = grades.sum / grades.length
          val mapping = (studentId, averageGrade)
          mapping
      }
    }
  }

  private def calculateClassAverage(studentAverages: Future[List[(String, Double)]]): Future[Double] = {
    studentAverages.map {
      studentAverage =>
        val sum = studentAverage.foldLeft(0.0)(_ + _._2)
        val classAverage = sum / studentAverage.length
        classAverage
    }
  }

  def calculateGrades(path: String): Future[Double] = {
    val data = parseCsv(path)
    val studentAverages = calculateStudentAverages(data)
    val classAverage = calculateClassAverage(studentAverages)
    classAverage
  }
}
