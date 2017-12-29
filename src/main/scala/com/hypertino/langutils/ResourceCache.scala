package com.hypertino.langutils

import java.util.concurrent.Callable
import java.util.{Locale, ResourceBundle}

import com.google.common.cache.{Cache, CacheBuilder}
import com.hypertino.langutils.resources.Utf8Control

import scala.util.Try

class ResourceNotFoundException(bundleName: String, acceptLanguage: String) extends Exception(
  s"Resource bundle $bundleName for languages '$acceptLanguage' is not found"
)

trait ResourceCache {
  protected def bundleName: String
  protected val defaultLocale: Locale = Locale.getDefault
  protected val resourceCache: Cache[String, Option[ResourceBundle]] = CacheBuilder
    .newBuilder()
    .build[String, Option[ResourceBundle]]()

  def locale(implicit languageRanges: LanguageRanges): Locale = lookupBundle.getLocale

  def r(s: String)(implicit languageRanges: LanguageRanges): String = lookupBundle.getString(s)

  protected def lookupBundle(implicit languageRanges: LanguageRanges): ResourceBundle = {
    val allBundles = languageRanges
      .languages
      .flatMap { l ⇒
        val lang = if (l.range == "*") defaultLocale.getLanguage else l.range
        resourceCache.get(lang, new Callable[Option[ResourceBundle]] {
          override def call() = loadBundle(lang, defaultIfNotMatched = false)
        })
      }

    allBundles.headOption
      .getOrElse {
        loadBundle(defaultLocale.getLanguage, defaultIfNotMatched = true).getOrElse {
          throw new ResourceNotFoundException(bundleName, languageRanges.raw)
        }
      }
  }

  protected def loadBundle(language: String, defaultIfNotMatched: Boolean): Option[ResourceBundle] = {
    val locale = Locale.forLanguageTag(language)
    Try {
      val b = ResourceBundle.getBundle(bundleName, locale, Utf8Control)
      if (b.getLocale.getLanguage.isEmpty && !defaultIfNotMatched) {
        None
      }
      else {
        Some(b)
      }
    }.toOption.flatten
  }
}
