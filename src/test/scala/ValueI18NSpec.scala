/*
 * Copyright (c) 2017 Magomed Abdurakhmanov, Hypertino
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 */

import com.hypertino.binders.value.{Lst, Obj}
import com.hypertino.langutils.{BundleResourceCache, LanguageRanges, ValueI18N}
import org.scalatest.{FlatSpec, Matchers}

class ValueI18NSpec extends FlatSpec with Matchers {
  "ValueI18N" should "localize Value fields using provided language-ranges" in {
    val o = Obj.from("color~i18n" -> Obj.from("en" -> "Color", "en-UK" -> "Colour", "ru" -> "Цвет"))

    // todo: (LanguageRanges("*")) shouldBe "color"
    ValueI18N.localize(o, LanguageRanges("en-UK")) shouldBe Obj.from(
      "color~i18n" -> Obj.from("en" -> "Color", "en-UK" -> "Colour", "ru" -> "Цвет"),
      "color" -> "Colour"
    )

    ValueI18N.localize(o, LanguageRanges("ru")) shouldBe Obj.from(
      "color~i18n" -> Obj.from("en" -> "Color", "en-UK" -> "Colour", "ru" -> "Цвет"),
      "color" -> "Цвет"
    )

    ValueI18N.localize(o, LanguageRanges("fr-CH, ru;q=0.9, en-UK;q=0.8, de;q=0.7, *;q=0.5")) shouldBe Obj.from(
      "color~i18n" -> Obj.from("en" -> "Color", "en-UK" -> "Colour", "ru" -> "Цвет"),
      "color" -> "Цвет"
    )

    ValueI18N.localize(o, LanguageRanges("ru-UA;q=0.9, en-UK;q=0.8, de;q=0.7, *;q=0.5")) shouldBe Obj.from(
      "color~i18n" -> Obj.from("en" -> "Color", "en-UK" -> "Colour", "ru" -> "Цвет"),
      "color" -> "Цвет"
    )
  }

  it should "localize Value fields using provided language-ranges and remove source fields" in {
    val o = Obj.from("color~i18n" -> Obj.from("en" -> "Color", "en-UK" -> "Colour", "ru" -> "Цвет"))

    // todo: (LanguageRanges("*")) shouldBe "color"
    ValueI18N.localize(o, LanguageRanges("en-UK"), removeSourceFields = true) shouldBe Obj.from(
      "color" -> "Colour"
    )
  }

  it should "localize Value of type Lst" in {
    val lst = Lst(Seq(Obj.from("color~i18n" -> Obj.from("en" -> "Color", "en-UK" -> "Colour", "ru" -> "Цвет"))))

    ValueI18N.localize(lst, LanguageRanges("en"), removeSourceFields = true) shouldBe Lst(Seq(Obj.from(
      "color" -> "Color"
    )))
  }

  it should "localize Value of type Obj recursively" in {
    val lst = Obj.from(
      "item" -> Obj.from("color~i18n" -> Obj.from("en" -> "Color", "en-UK" -> "Colour", "ru" -> "Цвет")),
      "list" -> Lst(Seq(Obj.from("color~i18n" -> Obj.from("en" -> "Color", "en-UK" -> "Colour", "ru" -> "Цвет"))))
    )

    ValueI18N.localize(lst, LanguageRanges("en"), removeSourceFields = true) shouldBe Obj.from(
      "item" -> Obj.from("color" -> "Color"),
      "list" -> Lst(Seq(Obj.from("color" -> "Color")))
    )
  }
}
