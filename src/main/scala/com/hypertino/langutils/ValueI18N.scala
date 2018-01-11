package com.hypertino.langutils

import java.util.Locale

import com.hypertino.binders.value.{Lst, Obj, Value}

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
        val resultBuilder = Map.newBuilder[String, Value]

        inner.foreach { case (k, v) =>
          if (k.endsWith(postfix) && v.isInstanceOf[Obj]){
            val i18n = v.toMap

            firstSome(languages, lang => i18n.get(lang)).map { l10n =>
              resultBuilder += (k.substring(0, k.length - postfix.length) -> l10n)
            }

            if (!removeSourceFields) {
              resultBuilder += (k -> v)
            }
          }else{
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

  private def firstSome(l: Seq[String], p: (String) => Option[Value]): Option[Value] = {
    val iterator = l.iterator

    while (iterator.hasNext){
      val option = p(iterator.next())
      if (option.nonEmpty){
        return option
      }
    }

    None
  }
}

