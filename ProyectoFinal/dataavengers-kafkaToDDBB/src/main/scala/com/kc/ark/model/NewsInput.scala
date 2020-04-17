package main.scala.com.kc.ark.model;

case class NewsInput (dateFormatted:String,
                      Summary:String,
                      FullUrl:String,
                      PublicationTime:String,
                      AuthorName:String,
                      Tags:List[String],
                      Article:String,
                      objectID:String)
