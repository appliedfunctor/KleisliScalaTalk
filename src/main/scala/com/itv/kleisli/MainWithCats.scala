package com.itv.kleisli

import cats.data.Kleisli
import cats.effect.IO
import com.typesafe.scalalogging.StrictLogging

import scala.util.{Failure, Success, Try}


object MainWithCats extends App with StrictLogging {

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


  val getNumberFromDb: Unit => IO[Int] = Unit => IO.pure(5)
  val processNumber: Int => IO[Int] = number => IO.pure(number * 2)
  val writeNumberToDb: Int => IO[Boolean] = number => IO.pure(true)


  val getNumberFromDbK: Kleisli[IO, Unit, Int] = Kleisli(Unit => IO.pure(5))
  val processNumberK: Kleisli[IO, Int, Int] = Kleisli(number => IO.pure(number * 2))
  val writeNumberToDbK: Kleisli[IO, Int, Boolean] = Kleisli(number => IO.pure(true))

  val handleRequestK: Kleisli[IO, Unit, Boolean] = getNumberFromDbK andThen processNumberK andThen writeNumberToDbK
  val handleRequest: Unit => IO[Boolean] = handleRequestK.run

  val handleRequestF: () => IO[Boolean] = () => for {
    number <- getNumberFromDb(())
    processed <- processNumber(number)
    written <- writeNumberToDb(processed)
  } yield written

  logger.info("For comprehension flatmap composition: " + handleRequestF().unsafeRunSync)
  logger.info("Cats Kleisli andThen combination: " + handleRequest().unsafeRunSync)



}
