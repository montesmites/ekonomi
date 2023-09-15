package se.montesmites.ekonomi.sie.file;

import static java.util.Map.entry;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toMap;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import se.montesmites.ekonomi.model.Account;
import se.montesmites.ekonomi.model.AccountId;
import se.montesmites.ekonomi.model.AccountStatus;
import se.montesmites.ekonomi.model.Balance;
import se.montesmites.ekonomi.model.Entry;
import se.montesmites.ekonomi.model.EntryEvent;
import se.montesmites.ekonomi.model.EntryStatus;
import se.montesmites.ekonomi.model.EntryStatus.Status;
import se.montesmites.ekonomi.model.Event;
import se.montesmites.ekonomi.model.EventId;
import se.montesmites.ekonomi.model.Series;
import se.montesmites.ekonomi.model.Year;
import se.montesmites.ekonomi.model.YearId;
import se.montesmites.ekonomi.organization.EventManager;
import se.montesmites.ekonomi.organization.Organization;
import se.montesmites.ekonomi.sie.record.types.SieRecordType;
import se.montesmites.ekonomi.sie.record.types.TypeIB;
import se.montesmites.ekonomi.sie.record.types.TypeKONTO;
import se.montesmites.ekonomi.sie.record.types.TypeRAR;
import se.montesmites.ekonomi.sie.record.types.TypeTRANS;
import se.montesmites.ekonomi.sie.record.types.TypeVER;

public class SieToOrganizationConverter {

  private SieToOrganizationConverter() {}

  public static SieToOrganizationConverter of() {
    return new SieToOrganizationConverter();
  }

  public Organization convert(Path path) {
    var reader = new Sie4FileReader();
    var records = reader.read(path);
    var recordsByLabel =
        records.stream().collect(groupingBy(record -> SieRecordType.of(record.getLabel())));
    var yearsMap =
        recordsByLabel.getOrDefault(SieRecordType.RAR, List.of()).stream()
            .map(record -> (TypeRAR) record)
            .map(
                rar ->
                    entry(
                        rar.getYearId(),
                        new Year(
                            new YearId(Integer.toString(rar.getYearId())),
                            rar.getYear(),
                            rar.getStart(),
                            rar.getEnd())))
            .collect(toMap(Map.Entry::getKey, Map.Entry::getValue));
    var currentYearId = yearsMap.get(0).yearId();
    var accountsMap =
        recordsByLabel.getOrDefault(SieRecordType.KONTO, List.of()).stream()
            .map(record -> (TypeKONTO) record)
            .map(
                konto ->
                    new Account(
                        new AccountId(currentYearId, konto.getAccountId()),
                        konto.getDescription(),
                        AccountStatus.OPEN))
            .collect(toMap(account -> account.accountId().id(), account -> account));
    var balances =
        recordsByLabel.getOrDefault(SieRecordType.IB, List.of()).stream()
            .map(record -> (TypeIB) record)
            .map(
                ib ->
                    new Balance(
                        new AccountId(yearsMap.get(ib.getYearId()).yearId(), ib.getAccountId()),
                        ib.getBalance()))
            .toList();
    var events =
        recordsByLabel.getOrDefault(SieRecordType.VER, List.of()).stream()
            .map(record -> (TypeVER) record)
            .map(
                ver -> {
                  var entries = new ArrayList<Entry>();
                  var tally = 0;
                  for (var sieRecord : ver.getSubrecords()) {
                    switch (sieRecord) {
                      case TypeTRANS trans -> entries.add(
                          new Entry(
                              new EventId(
                                  currentYearId,
                                  ver.getEventId().orElseThrow(),
                                  new Series(ver.getSeries().orElseThrow())),
                              tally++,
                              new AccountId(currentYearId, trans.getAccountId()),
                              trans.getAmount(),
                              new EntryStatus(Status.ACTIVE, EntryEvent.ORIGINAL)));
                      default -> {}
                    }
                  }
                  return entry(
                      new Event(
                          new EventId(
                              currentYearId,
                              ver.getEventId().orElseThrow(),
                              new Series(ver.getSeries().orElseThrow())),
                          ver.getDate(),
                          ver.getDescription(),
                          ver.getRegistrationDate().orElse(ver.getDate())),
                      entries);
                })
            .collect(toMap(Map.Entry::getKey, Map.Entry::getValue));
    var entries = events.values().stream().flatMap(Collection::stream).toList();
    return new Organization(
        new EventManager(events.keySet().stream()),
        accountsMap.values().stream(),
        balances.stream(),
        entries.stream(),
        yearsMap.values().stream());
  }
}
