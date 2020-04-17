package main.scala.com.kc.ark.model

case class CalendarInput(startDateFormatted:String,
                          endDateFormatted:String,
                          id:String,
                          events:List[CalendarEvents]
                          )

case class CalendarEvents(id:String,
                          hour:String,
                          currency:String,
                          event: String,
                          importance:String,
                          actual:String,
                          forecast:String,
                          previous:String,
                          rawEventDate:String,
                          eventDateFormatted:String)
