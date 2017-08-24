package se.montesmites.ekonomi.organization;

import se.montesmites.ekonomi.model.*;
import se.montesmites.ekonomi.parser.vismaadmin200.Parser;

import java.nio.file.Path;
import java.util.stream.Stream;

import static se.montesmites.ekonomi.parser.vismaadmin200.v2015_0.BinaryFile_2015_0.*;

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
        Parser p = new Parser(path);
        this.accounts = p.parse(ACCOUNTS).filter(filter.accountFilter());
        this.balances = p.parse(BALANCES).filter(filter.balanceFilter());
        this.entries = p.parse(ENTRIES).filter(filter.entryFilter());
        this.years = p.parse(YEARS).filter(filter.yearFilter());
        this.eventManager = new EventManager(p.parse(EVENTS).filter(filter.eventFilter()));
    }

    public EventManager getEventManager() {
        return this.eventManager;
    }

    public Organization build() {
        return new Organization(eventManager, accounts, balances, entries, years);
    }
}
