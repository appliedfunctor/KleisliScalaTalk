package com.itv.kleisli

import scalaz._
import scalaz.concurrent.Task
import com.typesafe.scalalogging.StrictLogging

import scala.util.{Failure, Success, Try}


object MainWithScalaz extends App with StrictLogging {

  val parseNumber: String => Int = Integer.parseInt
  val withinBounds: Int => Boolean = number => number < 25
  val bToMessage: Boolean => String = {
    case true => "Input is Valid"
    case _ => "Input is Invalid"
  }

  val checkNumber: String => String = bToMessage compose
                                        withinBounds compose
                                          parseNumber

  val altCheckNumber: String => String = parseNumber andThen // String => Int
                                          withinBounds andThen // Int => Int
                                            bToMessage // Int => Boolean

  assert(checkNumber("5") == altCheckNumber("5"))
  logger.info(s"checkNumber and altCheckNumber are equal. ")

  val safeParseNumber: String => Option[Int] = num => Try { Integer.parseInt(num) } match {
    case Success(number) => Some(number)
    case Failure(_) => None
  }
  val safeWithinBounds: Option[Int] => Boolean = optNumber => optNumber.fold(false)(n => n < 25)
  val toMessage: Boolean => String = {
    case true => "Input is Valid"
    case _ => "Input is Invalid"
  }

  val safeCheckNumber = safeParseNumber andThen safeWithinBounds andThen toMessage


  val getNumberFromDb: Unit => Task[Int] = Unit => Task(5)
  val processNumber: Int => Task[Int] = number => Task(number * 2)
  val writeNumberToDb: Int => Task[Boolean] = number => Task(true)


  val getNumberFromDbK: Kleisli[Task, Unit, Int] = Kleisli(Unit => Task(5))
  val processNumberK: Kleisli[Task, Int, Int] = Kleisli(number => Task(number * 2))
  val writeNumberToDbK: Kleisli[Task, Int, Boolean] = Kleisli(number => Task(true))

  val handleRequestK: Kleisli[Task, Unit, Boolean] = getNumberFromDbK andThen processNumberK andThen writeNumberToDbK
  val handleRequestAltK: Kleisli[Task, Unit, Boolean] = getNumberFromDbK >=> processNumberK >=> writeNumberToDbK
  val handleRequest: Unit => Task[Boolean] = handleRequestK.run
  val handleRequestAlt: Unit => Task[Boolean] = handleRequestAltK.run

  val handleRequestF: () => Task[Boolean] = () => for {
    number <- getNumberFromDb(())
    processed <- processNumber(number)
    written <- writeNumberToDb(processed)
  } yield written

  logger.info("For comprehension flatmap composition: " + handleRequestF().unsafePerformSync)
  logger.info("Scalaz Kleisli andThen combination: " + handleRequest().unsafePerformSync)
  logger.info("Scalaz Kleisli >=> combination: " + handleRequestAlt().unsafePerformSync)



}
