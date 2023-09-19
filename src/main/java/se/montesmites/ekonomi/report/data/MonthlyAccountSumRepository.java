package se.montesmites.ekonomi.report.data;

import java.math.BigDecimal;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import se.montesmites.ekonomi.db.EntryEntity;
import se.montesmites.ekonomi.jpa.migration.MonthlyAccountSum;

public interface MonthlyAccountSumRepository extends Repository<EntryEntity, Long> {

  @Query(
      """
          SELECT
                year(event.date) as eventYear,
                month(event.date) as eventMonth,
                account.qualifier as accountQualifier,
                account.name as accountName,
                count(entry.entryId) as entryCount,
                sum(entry.amount) as totalAmount
          FROM
                EntryEntity entry
                JOIN entry.event event
                JOIN entry.account account
          WHERE
                year(event.date) = :calendarYear
          GROUP BY
                year(event.date),
                month(event.date),
                account.qualifier,
                account.name
      """)
  List<MonthlyAccountSumProjection> fetchAllMonthlyAccountSumProjection(int calendarYear);

  default List<MonthlyAccountSum> fetchAllMonthlyAccountSums(int calendarYear) {
    return this.fetchAllMonthlyAccountSumProjection(calendarYear).stream()
        .map(
            monthlyAccountSum ->
                new MonthlyAccountSum(
                    monthlyAccountSum.getEventYear(),
                    monthlyAccountSum.getEventMonth(),
                    monthlyAccountSum.getAccountQualifier(),
                    monthlyAccountSum.getAccountName(),
                    monthlyAccountSum.getEntryCount(),
                    monthlyAccountSum.getTotalAmount()))
        .toList();
  }

  interface MonthlyAccountSumProjection {
    int getEventYear();

    int getEventMonth();

    String getAccountQualifier();

    String getAccountName();

    long getEntryCount();

    BigDecimal getTotalAmount();
  }
}
