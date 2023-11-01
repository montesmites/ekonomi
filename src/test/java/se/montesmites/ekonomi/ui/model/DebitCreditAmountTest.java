package se.montesmites.ekonomi.ui.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Optional;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import se.montesmites.ekonomi.db.model.Amount;
import se.montesmites.ekonomi.ui.model.DebitCreditAmount.Side;

class DebitCreditAmountTest {

  @ParameterizedTest
  @CsvSource(
      nullValues = "<null>",
      delimiter = ';',
      value = {"<null>;PENDING;", ";PENDING;", "0;DEBIT;0,00;", "1;DEBIT;1,00", "-1;CREDIT;1,00"})
  void from(String amountAsText, Side expectedSide, String expectedAmount) {
    var expected = new DebitCreditAmount(expectedSide, Amount.parse(expectedAmount));
    var actual =
        Amount.parse(amountAsText).map(DebitCreditAmount::from).orElse(DebitCreditAmount.empty());

    assertEquals(expected, actual);
  }

  @ParameterizedTest
  @CsvSource(
      nullValues = "<null>",
      delimiter = ';',
      value = {"<null>;''", "'';''", "0;0,00", "1;1,00", "-1;''"})
  void formatDebit(String amountAsText, String expected) {
    var amount =
        Optional.ofNullable(amountAsText)
            .map(_amount -> DebitCreditAmount.parse(Side.DEBIT, _amount))
            .orElse(DebitCreditAmount.empty());
    var actual = amount.format(Side.DEBIT).orElse("");

    assertEquals(expected, actual);
  }

  @ParameterizedTest
  @CsvSource(
      nullValues = "<null>",
      delimiter = ';',
      value = {"<null>;''", "'';''", "0;0,00", "1;1,00", "-1;''"})
  void formatCredit(String amountAsText, String expected) {
    var debitCreditAmount =
        Optional.ofNullable(amountAsText)
            .map(_amount -> DebitCreditAmount.parse(Side.CREDIT, _amount))
            .orElse(DebitCreditAmount.empty());
    var actual = debitCreditAmount.format(Side.CREDIT).orElse("");

    assertEquals(expected, actual);
  }

  @ParameterizedTest
  @CsvSource(
      nullValues = "<null>",
      value = {"<null>,PENDING,", ",PENDING,", "0,DEBIT,0", "1,DEBIT,1", "-1,CREDIT,1"})
  void parseDebit(String input, Side expectedSide, String expectedAmount) {
    var expected = new DebitCreditAmount(expectedSide, Amount.parse(expectedAmount));
    var actual = DebitCreditAmount.parse(Side.DEBIT, input);

    assertEquals(expected, actual);
  }

  @ParameterizedTest
  @CsvSource(
      nullValues = "<null>",
      value = {"<null>,PENDING,", ",PENDING,", "0,CREDIT,0", "1,CREDIT,1", "-1,DEBIT,1"})
  void parseCredit(String input, Side expectedSide, String expectedAmount) {
    var expected = new DebitCreditAmount(expectedSide, Amount.parse(expectedAmount));
    var actual = DebitCreditAmount.parse(Side.CREDIT, input);

    assertEquals(expected, actual);
  }

  @ParameterizedTest
  @CsvSource(
      delimiter = ';',
      value = {
        "PENDING;;;;PENDING;",
        "PENDING;;DEBIT;-1;DEBIT;-1",
        "PENDING;;DEBIT;0;DEBIT;0",
        "PENDING;;DEBIT;1;DEBIT;1",
        "DEBIT;1;PENDING;;DEBIT;1",
        "DEBIT;1;DEBIT;;DEBIT;1",
        "DEBIT;1;DEBIT;-1;DEBIT;-1",
        "DEBIT;1;DEBIT;0;DEBIT;0",
        "DEBIT;1;DEBIT;1;DEBIT;1",
        "DEBIT;1;CREDIT;;DEBIT;1",
        "DEBIT;1;CREDIT;-1;CREDIT;-1",
        "DEBIT;1;CREDIT;0;CREDIT;0",
        "DEBIT;1;CREDIT;1;CREDIT;1",
        "CREDIT;1;PENDING;;CREDIT;1",
        "CREDIT;1;DEBIT;;CREDIT;1",
        "CREDIT;1;DEBIT;-1;DEBIT;-1",
        "CREDIT;1;DEBIT;0;DEBIT;0",
        "CREDIT;1;DEBIT;1;DEBIT;1",
        "CREDIT;1;CREDIT;;CREDIT;1",
        "CREDIT;1;CREDIT;-1;CREDIT;-1",
        "CREDIT;1;CREDIT;0;CREDIT;0",
        "CREDIT;1;CREDIT;1;CREDIT;1",
      })
  void merge(
      Side incumbentSide,
      String incumbentAmount,
      Side otherSide,
      String otherAmount,
      Side expectedSide,
      String expectedAmount) {
    var incumbent = new DebitCreditAmount(incumbentSide, Amount.parse(incumbentAmount));
    var other = new DebitCreditAmount(otherSide, Amount.parse(otherAmount));

    var expected = new DebitCreditAmount(expectedSide, Amount.parse(expectedAmount));
    var actual = incumbent.merge(other);

    assertEquals(expected, actual);
  }
}
