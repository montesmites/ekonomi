package se.montesmites.ekonomi.i18n;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;
import se.montesmites.ekonomi.db.model.Amount;
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
    numberFormat.setMinimumFractionDigits(amount.decimalPlaces());
    numberFormat.setMaximumFractionDigits(amount.decimalPlaces());
    return numberFormat.format(amount.toDouble());
  }

  public static NumberFormat numberFormat() {
    var numberFormat = NumberFormat.getInstance(LOCALE);
    numberFormat.setMinimumFractionDigits(Amount.NUMBER_OF_DECIMALS);
    numberFormat.setMaximumFractionDigits(Amount.NUMBER_OF_DECIMALS);
    return numberFormat;
  }

  public static String formatNumber(Amount amount) {
    return numberFormat().format(amount.amount());
  }

  public static Optional<Amount> parseAmount(String text) {
    try {
      if (text != null && numberFormat() instanceof DecimalFormat decimalFormat) {
        var minusSign = decimalFormat.getDecimalFormatSymbols().getMinusSign();
        var washedText = text.replace('-', minusSign);
        var number = numberFormat().parse(washedText);
        var amount =
            number instanceof Byte
                    || number instanceof Short
                    || number instanceof Integer
                    || number instanceof Long
                ? BigDecimal.valueOf(number.longValue())
                : BigDecimal.valueOf(number.doubleValue());
        return Optional.of(new Amount(amount));
      } else {
        return Optional.empty();
      }
    } catch (ParseException e) {
      return Optional.empty();
    }
  }
}
