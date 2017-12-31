package com.hypertino.langutils

class MapI18NNoTranslation(languageRanges: LanguageRanges) extends Exception(s"There is no key with localization to ${languageRanges.raw}")

object MapI18N {
  def localize[T](keys: Map[String, T], languageRanges: LanguageRanges): T = {
    languageRanges.straight.toStream.flatMap(keys.get).headOption.getOrElse {
      throw new MapI18NNoTranslation(languageRanges)
    }
  }
}
