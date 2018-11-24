package se.montesmites.ekonomi.i18n;

import java.text.NumberFormat;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.Locale;
import java.util.ResourceBundle;
import se.montesmites.ekonomi.model.Currency;

public class Messages {

  public enum Message {
    HEADER_ROW_TOTAL("header.row.total"),
    HEADER_ROW_AVERAGE("header.row.average"),
    ;

    private final String key;

    Message(String key) {
      this.key = key;
    }
  }

  private static final Locale LOCALE = Locale.getDefault();
  private static final ResourceBundle MESSAGES = ResourceBundle.getBundle("i18n.messages", LOCALE);

  public static String get(Message message) {
    return MESSAGES.getString(message.key);
  }

  public static String getShortMonth(Month month) {
    return month.getDisplayName(TextStyle.SHORT, LOCALE);
  }

  public static String formatNumber(Currency amount) {
    var numberFormat = NumberFormat.getInstance(LOCALE);
    numberFormat.setMinimumFractionDigits(amount.getDecimalPlaces());
    numberFormat.setMaximumFractionDigits(amount.getDecimalPlaces());
    return numberFormat.format(amount.toDouble());
  }
}
