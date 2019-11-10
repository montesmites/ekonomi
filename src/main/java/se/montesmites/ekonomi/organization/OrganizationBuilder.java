package se.montesmites.ekonomi.organization;

import static se.montesmites.ekonomi.parser.vismaadmin200.v2015_0.BinaryFile_2015_0.ACCOUNTS;
import static se.montesmites.ekonomi.parser.vismaadmin200.v2015_0.BinaryFile_2015_0.BALANCES;
import static se.montesmites.ekonomi.parser.vismaadmin200.v2015_0.BinaryFile_2015_0.ENTRIES;
import static se.montesmites.ekonomi.parser.vismaadmin200.v2015_0.BinaryFile_2015_0.EVENTS;
import static se.montesmites.ekonomi.parser.vismaadmin200.v2015_0.BinaryFile_2015_0.YEARS;

import java.nio.file.Path;
import java.util.stream.Stream;
import se.montesmites.ekonomi.model.Account;
import se.montesmites.ekonomi.model.Balance;
import se.montesmites.ekonomi.model.Entry;
import se.montesmites.ekonomi.model.Event;
import se.montesmites.ekonomi.model.Year;
import se.montesmites.ekonomi.parser.vismaadmin200.Parser;

public class OrganizationBuilder {

  private final Stream<Account> accounts;
  private final Stream<Balance> balances;
  private final Stream<Entry> entries;
  private final Stream<Year> years;

  private final EventManager eventManager;

  public OrganizationBuilder(Path path) {
    this(path, Filter.get());
  }

  public OrganizationBuilder(Path path, Filter filter) {
    var parser = new Parser(path);
    this.accounts = parser.parse(ACCOUNTS).filter(filter.accountFilter());
    this.balances = parser.parse(BALANCES).filter(filter.balanceFilter());
    this.entries = parser.parse(ENTRIES).filter(filter.entryFilter());
    this.years = parser.parse(YEARS).filter(filter.yearFilter());
    this.eventManager = new EventManager(parser.parse(EVENTS).filter(filter.eventFilter()));
  }

  public OrganizationBuilder(
      Stream<Year> years,
      Stream<Account> accounts,
      Stream<Balance> balances,
      Stream<Event> events,
      Stream<Entry> entries) {
    this.accounts = accounts;
    this.balances = balances;
    this.entries = entries;
    this.years = years;
    this.eventManager = new EventManager(events);
  }

  public EventManager getEventManager() {
    return this.eventManager;
  }

  public Organization build() {
    return new Organization(eventManager, accounts, balances, entries, years);
  }
}
