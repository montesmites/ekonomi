package se.montesmites.ekonomi.db;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import se.montesmites.ekonomi.db.model.EntryDataAndAccountQualifierAndName;

public interface EntryRepository extends JpaRepository<EntryEntity, Long> {

  @Query(
      """
              SELECT
                      new se.montesmites.ekonomi.db.model.EntryDataAndAccountQualifierAndName(
                          entry.entryId,
                          entry.event.eventId,
                          entry.rowNo,
                          account.accountId,
                          entry.amount,
                          account.qualifier,
                          account.name
                      )
              FROM
                      EntryEntity entry
                      JOIN entry.account account
              WHERE
                      entry.event.eventId = :eventId
        """)
  List<EntryDataAndAccountQualifierAndName> findEntriesWithAccountsBy(Long eventId);
}
