package tech.igorramazanov.coinkeeper.fetcher

import argonaut.Argonaut._
import com.softwaremill.sttp._
import com.softwaremill.sttp.quick.backend

import scala.scalanative.native._

object Main {
  val budget = sys.env.getOrElse("COINKEEPER_BUDGET", "250000").toInt
  val cookie = sys.env("COINKEEPER_COOKIE")
  val userId = sys.env("COINKEEPER_USER_ID")

  case class PingResponse(totalDays: Int, currentDay: Int, expenses: Int)
  case class Transaction(amount: Int, dayOfMonth: Int)

  def main(args: Array[String]): Unit = {
    val s =
      try {
        program()
      } catch {
        case e: Throwable => e.getMessage
      }
    println(s)
  }

  def program(): String = Zone { implicit z =>
    val pingResponse = ping()
    val tx           = transactions()
    val diff = tx.foldLeft(0) {
      case (acc, t) =>
        if (t.dayOfMonth == pingResponse.currentDay) {
          acc + t.amount
        } else {
          acc
        }
    }
    val totalAvailableToday = totalToday(pingResponse, diff)
    val availableNow        = totalAvailableToday - diff
    s"Total: $totalAvailableToday Available: $availableNow"
  }

  def ping(): PingResponse = {
    val request = sttp
      .post(uri"https://coinkeeper.me/Exchange/Ping")
      .header("Cookie", cookie)
      .body(
        """{"items":[{"key":0,"entityJson":null},{"key":1,"entityJson":null},{"key":2,"entityJson":null},{"key":3,"entityJson":null},{"key":4,"entityJson":null},{"key":5,"entityJson":null},{"key":6,"entityJson":null}]}"""
      )

    val response = request.send()
    val t        = response.body.right.get
    val j        = t.parseOption.get.objectOrEmpty
    val s = j("data").get
      .objectOrEmpty("items")
      .get
      .arrayOrEmpty(5)
      .objectOrEmpty("entityJson")
      .get
      .stringOrEmpty
    val j2         = s.parseOption.get.objectOrEmpty
    val expenses   = j2("expenseSpentBalance").get.numberOrZero.toDouble.get.round.toInt
    val currentDay = j2("currentNumberOfDaysInPeriod").get.numberOrZero.toInt.get
    val totalDays  = j2("totalNumberOfDaysInPeriod").get.numberOrZero.toInt.get
    PingResponse(totalDays, currentDay, expenses)
  }

  def transactions()(implicit z: Zone): List[Transaction] = {
    val request = sttp
      .post(uri"https://coinkeeper.me/api/transaction/get")
      .header("Content-Type", "application/json")
      .header("Cookie", cookie)
      .body(
        s"""{"userId":"$userId","skip":0,"take":40,"categoryIds":[],"tagIds":[],"period":{}}"""
      )
    val response = request.send()
    val t        = response.body.right.get
    val j        = t.parseOption.get.objectOrEmpty("transactions").get.arrayOrEmpty
    j.map(_.objectOrEmpty).flatMap { json =>
      val sourceType      = json("sourceType").get.numberOrZero.toInt.get
      val destinationType = json("destinationType").get.numberOrZero.toInt.get
      if (sourceType == 2 && destinationType == 3) {
        val destinationAmount =
          json("destinationAmount").get.numberOrZero.toDouble.get.round.toInt
        val dateTimestamp = json("dateTimestampISO").get.stringOrEmpty.toString
        val tm            = stackalloc[CStruct9[CInt, CInt, CInt, CInt, CInt, CInt, CInt, CInt, CInt]]
        CTime.parse(toCString(dateTimestamp), toCString("%FT%T%z"), tm)
        List(Transaction(destinationAmount, !tm._4))
      } else {
        Nil
      }
    }
  }

  def totalToday(pingResponse: PingResponse, diff: Int): Int = {
    val leftDays  = pingResponse.totalDays - pingResponse.currentDay + 1
    val leftTotal = budget - pingResponse.expenses + diff
    leftTotal / leftDays
  }
}

@link("curl")
@link("idn")
@extern
object CTime {
  @name("strptime")
  def parse(
      s: CString,
      format: CString,
      tm: Ptr[CStruct9[CInt, CInt, CInt, CInt, CInt, CInt, CInt, CInt, CInt]]
  ): CChar = extern
}
