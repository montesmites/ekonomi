package se.montesmites.ekonomi.nikka;

import se.montesmites.ekonomi.organization.Organization;
import se.montesmites.ekonomi.organization.OrganizationBuilder;
import se.montesmites.ekonomi.report.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Year;
import java.util.Arrays;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static se.montesmites.ekonomi.report.Column.DESCRIPTION;

public class Main {

    public static void main(String[] args) throws Exception {
        final Year year = Year.of(2017);
        final String fmt = "c:/temp/nikka/Kassaflöde %d.txt";
        final String fileName = String.format(fmt, year.getValue());
        final Path path = Paths.get(fileName);
        Main main = new Main();
        CashflowReport report = main.generateCashflowReport(year);
        main.renderToFile(report, path);
    }

    private final CashflowDataFetcher fetcher;

    private Main() {
        Path path = Paths.get(
                "C:\\ProgramData\\SPCS\\SPCS Administration\\Företag\\nikka");
        Organization organization = new OrganizationBuilder(path).build();
        this.fetcher = new CashflowDataFetcher(organization);
    }

    private CashflowReport generateCashflowReport(Year year) {
        Supplier<Stream<Section>> sections
                = () -> Arrays.stream(NikkaSection.values())
                        .map(section -> section.section(fetcher, year));
        TotallingSection total
                = new TotallingCompactSection(
                        "Kontrollsumma",
                        sections.get().collect(toList())) {
            @Override
            public Row wrapSectionRow(Section section, Row row) {
                if (sectionEquals(section, NikkaSection.FORANDRING_LIKVIDA_MEDEL)) {
                    return new DefaultRowWithAccountsWithNegatedAmounts(
                            row.asRowWithAccounts().get());
                } else {
                    return row;
                }
            }

            private boolean sectionEquals(Section section, NikkaSection nikkaSection) {
                return section.streamTitle().findFirst().get().formatText(
                        DESCRIPTION).trim().toUpperCase().equals(
                                nikkaSection.getTitle().trim().toUpperCase());
            }
        };
        AccumulatingSection accumulation
                = new AccumulatingSection(
                        "Ackumulerade likvida medel",
                        () -> Stream.of(new AccumulatingNegatedRow(
                                fetcher,
                                ()
                                -> new AccountFilterByRegex("1493|19\\d\\d")
                                        .filter(fetcher.streamAccountIds(
                                                year)),
                                year)));
        return new CashflowReport(
                fetcher,
                year, () -> Stream.concat(
                        sections.get(),
                        Stream.of(
                                total,
                                accumulation)));
    }

    private void renderToFile(CashflowReport report, Path path) throws IOException {
        Files.write(path, report.render());
    }
}
