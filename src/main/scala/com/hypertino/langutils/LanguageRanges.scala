package com.hypertino.langutils

import java.util.Locale

case class LanguageRange(range: String, weight: Double, languageCode: Option[String], countryCode: Option[String]) {
  def isDefault: Boolean = range == "*"
}

case class LanguageRanges(raw: String, defaultLanguage: String = Locale.getDefault().getLanguage) {
  import scala.collection.JavaConverters._

  lazy val languages: Seq[LanguageRange] = Locale.LanguageRange.parse(raw)
    .asScala
    .map { l ⇒

      val range = l.getRange
        if (range != "*") {
          val i = range.indexOf("-")
          val languageCode: String = {
            if (i >= 0) {
              range.substring(0, i)
            }
            else {
              range
            }
          }

          val countryCode: Option[String] = {
            if (i >= 0) {
              Some(range.substring(i + 1, range.length).toUpperCase)
            }
            else {
              None
            }
          }
          val newRange = if (countryCode.isEmpty) languageCode else {
            languageCode + "-" + countryCode.get
          }
          LanguageRange(newRange, l.getWeight, Some(languageCode), countryCode)
        }
        else {
          LanguageRange(l.getRange, l.getWeight, None, None)
        }
    }

  lazy val straight: Seq[String] = {
    var noDefaultAtEnd = false
    val seq = languages.filterNot(r => r.range == "*").flatMap {
      lr =>
        if (lr.range == defaultLanguage) {
          noDefaultAtEnd = true
        }
        if (lr.countryCode.isDefined) {
          Seq(lr.range, lr.languageCode.get)
        }
        else {
          Seq(lr.range)
        }
    }
    if (noDefaultAtEnd)
      seq
    else
      seq :+ defaultLanguage
  }
}

object LanguageRanges {
  val default = LanguageRanges("*")
}