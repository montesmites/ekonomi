package se.montesmites.ekonomi.jpa.migration;

import java.math.BigDecimal;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import se.montesmites.ekonomi.db.EntryEntity;

public interface MigrationVerifierRepository extends Repository<EntryEntity, Long> {

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
          GROUP BY
                year(event.date),
                month(event.date),
                account.qualifier,
                account.name
      """)
  List<AllMonthlyAccountSumProjection> fetchAllMonthlyAccountSumsAsProjection();

  default List<MonthlyAccountSum> fetchAllMonthlyAccountSums() {
    return this.fetchAllMonthlyAccountSumsAsProjection().stream()
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

  interface AllMonthlyAccountSumProjection {
    int getEventYear();

    int getEventMonth();

    String getAccountQualifier();

    String getAccountName();

    long getEntryCount();

    BigDecimal getTotalAmount();
  }
}
