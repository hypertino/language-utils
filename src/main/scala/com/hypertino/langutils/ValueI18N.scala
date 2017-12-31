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
        val l10nFields = inner.filter(kv ⇒ kv._1.endsWith(postfix) && kv._2.isInstanceOf[Obj]).flatMap { case (k, v) ⇒
          val i = v.asInstanceOf[Obj]
          var l10n: Value = null
          val it = languages.iterator
          while (it.hasNext && l10n == null) {
            val lang = it.next()
            i.v.get(lang).foreach { v ⇒
              l10n = v
            }
          }
          if (l10n != null) {
            Some(k.substring(0, k.length - postfix.length) → l10n)
          }
          else {
            None
          }
        }

        if (removeSourceFields) {
          Obj(inner.filterNot(_._1.endsWith(postfix)) ++ l10nFields)
        }
        else {
          Obj(inner ++ l10nFields)
        }

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

