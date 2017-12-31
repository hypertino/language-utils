/*
 * Copyright (c) 2017 Magomed Abdurakhmanov, Hypertino
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 */

import com.hypertino.langutils.{LanguageRanges, MapI18N}
import org.scalatest.{FlatSpec, Matchers}

class MapI18NSpec extends FlatSpec with Matchers {
  "MapI18N" should "localize using provided language-ranges" in {
    val m = Map("en" -> "Color", "en-UK" -> "Colour", "ru" -> "Цвет")
    MapI18N.localize(m, LanguageRanges("en-UK")) shouldBe "Colour"
    MapI18N.localize(m, LanguageRanges("ru")) shouldBe "Цвет"
    MapI18N.localize(m, LanguageRanges("fr-CH, ru;q=0.9, en-UK;q=0.8, de;q=0.7, *;q=0.5")) shouldBe "Цвет"
    MapI18N.localize(m, LanguageRanges("ru-UA;q=0.9, en-UK;q=0.8, de;q=0.7, *;q=0.5")) shouldBe "Цвет"
  }
}
