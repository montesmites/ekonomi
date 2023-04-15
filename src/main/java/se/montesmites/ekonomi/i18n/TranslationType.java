package se.montesmites.ekonomi.i18n;

import java.util.Map;

public sealed interface TranslationType {

  static Canonical canonical(String en, String sv) {
    return new Canonical(en, sv);
  }

  static <T extends Enum<T>> Parameterized<T> parameterized(Map<T, Canonical> translations) {
    return new Parameterized<>(translations);
  }

  default String json() {
    return """
          {
            "en": "%s",
            "sv": "%s"
          }
        """
        .formatted(en(), sv());
  }

  String en();

  String sv();

  record Canonical(String en, String sv) implements TranslationType {}

  record Parameterized<T extends Enum<T>>(Map<T, Canonical> translations) {

    public TranslationType forParameter(T parameter) {
      return this.translations.getOrDefault(
          parameter, TranslationType.canonical(parameter.name(), parameter.name()));
    }
  }
}
