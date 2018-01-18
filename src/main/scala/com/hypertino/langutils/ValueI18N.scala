package com.hypertino.langutils

import java.util.Locale

import com.hypertino.binders.value.{Lst, Obj, Value}

import scala.collection.mutable

object ValueI18N {
  def localize(v: Value,
               languageRanges: LanguageRanges,
               defaultLanguage: String = Locale.getDefault.getLanguage,
               postfix: String = "~i18n",
               removeSourceFields: Boolean = false): Value = {
    recursiveFilterFields(v,
      languageRanges.straight,
      postfix,
      removeSourceFields)
  }

  private def recursiveFilterFields(original: Value,
                                    languages: Seq[String],
                                    postfix: String,
                                    removeSourceFields: Boolean): Value = {
    original match {
      case Obj(inner) ⇒
        val resultBuilder = new mutable.HashMap[String, Value]()

        inner.foreach { case (k, v) =>
          if (k.endsWith(postfix) && v.isInstanceOf[Obj]){
            val i18n = v.toMap

            languages
              .iterator.map(i18n.get)
              .find(_.isDefined)
              .map { l10n =>
                resultBuilder += (k.substring(0, k.length - postfix.length) -> l10n.get)
              }

            if (!removeSourceFields) {
              resultBuilder += (k -> v)
            }
          }else if (!resultBuilder.contains(k)) {
              resultBuilder += (k -> recursiveFilterFields(v, languages, postfix, removeSourceFields))
          }
        }

        Obj(resultBuilder.result())

      case Lst(inner) ⇒
        Lst(
          inner.map { i ⇒
            recursiveFilterFields(i, languages, postfix, removeSourceFields)
          }
        )

      case _ ⇒ original
    }
  }
}
