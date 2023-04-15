package se.montesmites.ekonomi.i18n;

import java.util.Locale;

public interface Translator {

  String getTranslation(String key, Object... params);

  String getTranslation(Locale locale, String key, Object... params);

  default String t(TranslationType translation, Object... params) {
    return this.getTranslation(translation.json(), params);
  }

  default String t(Locale locale, TranslationType translation, Object... params) {
    return this.getTranslation(locale, translation.json(), params);
  }
}
