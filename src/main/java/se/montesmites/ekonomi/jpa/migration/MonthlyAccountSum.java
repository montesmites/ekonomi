package se.montesmites.ekonomi.jpa.migration;

import java.math.BigDecimal;
import java.time.Month;
import java.time.YearMonth;
import org.springframework.data.annotation.PersistenceCreator;
import se.montesmites.ekonomi.db.AccountWithQualifierAndName;

public record MonthlyAccountSum(
    YearMonth yearMonth,
    AccountWithQualifierAndName account,
    long entryCount,
    BigDecimal totalAmount) {

  @PersistenceCreator
  public MonthlyAccountSum(
      int eventYear,
      int eventMonth,
      String accountQualifier,
      String accountName,
      long entryCount,
      BigDecimal totalAmount) {
    this(
        YearMonth.of(eventYear, eventMonth),
        new AccountWithQualifierAndName(accountQualifier, accountName),
        entryCount,
        totalAmount);
  }

  public int year() {
    return this.yearMonth().getYear();
  }

  public Month month() {
    return this.yearMonth.getMonth();
  }

  public String accountQualifier() {
    return this.account.qualifier();
  }

  public String accountName() {
    return this.account.name();
  }
}
