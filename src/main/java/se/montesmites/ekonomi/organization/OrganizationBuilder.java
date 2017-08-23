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
    private final Stream<Event> events;
    private final Stream<Year> years;

    public OrganizationBuilder(Path path) {
        this(path, Filter.get());
    }

    public OrganizationBuilder(Path path, Filter filter) {
        Parser p = new Parser(path);
        accounts = p.parse(ACCOUNTS).filter(filter.accountFilter());
        balances = p.parse(BALANCES).filter(filter.balanceFilter());
        entries = p.parse(ENTRIES).filter(filter.entryFilter());
        events = p.parse(EVENTS).filter(filter.eventFilter());
        years = p.parse(YEARS).filter(filter.yearFilter());
    }

    public Organization build() {
        return new Organization(accounts, balances, entries, events, years);
    }
}
