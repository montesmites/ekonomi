package se.montesmites.ekonomi.i18n;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaadin.flow.i18n.I18NProvider;
import java.text.MessageFormat;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.stream.Stream;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@AllArgsConstructor
public class TranslationProvider implements I18NProvider {

  private static final List<Locale> PROVIDED_LOCALES =
      Stream.of(new LanguageAndRegion("sv", "SE"), new LanguageAndRegion("en", "GB"))
          .map(LanguageAndRegion::toLocale)
          .toList();

  private final ObjectMapper objectMapper;

  @Override
  public List<Locale> getProvidedLocales() {
    return PROVIDED_LOCALES;
  }

  @Override
  public String getTranslation(String key, Locale locale, Object... params) {
    if (key == null) {
      log.warn("Translation request where key is null", new IllegalArgumentException());
      return "";
    } else {
      try {
        var translation = this.parseTranslationJson(key, locale);
        if ("en".equals(locale.getLanguage()) && translation.en() != null) {
          return buildTranslation(translation.en(), params);
        } else if (translation.sv() != null) {
          return buildTranslation(translation.sv(), params);
        } else {
          throw new MissingResourceException(
              "Missing resource", TranslationProvider.class.getName(), key);
        }
      } catch (MissingResourceException e) {
        log.warn("Missing translation: key={}, locale={}", key, locale, e);
        return key;
      } catch (Exception e) {
        return key;
      }
    }
  }

  private Translation parseTranslationJson(String key, Locale locale) {
    try {
      return this.objectMapper.readValue(key, Translation.class);
    } catch (JsonProcessingException e) {
      log.warn("Key is not json: key={}, locale={}", key, locale, e);
      throw new RuntimeException(e);
    }
  }

  private String buildTranslation(Object translation, Object[] params) {
    return params.length > 0
        ? MessageFormat.format(translation.toString(), params)
        : translation.toString();
  }

  private record LanguageAndRegion(String language, String region) {

    Locale toLocale() {
      return new Locale.Builder().setLanguage(this.language).setRegion(this.region).build();
    }
  }
}
