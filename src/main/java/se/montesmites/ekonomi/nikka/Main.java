package se.montesmites.ekonomi.nikka;

import se.montesmites.ekonomi.organization.OrganizationBuilder;
import se.montesmites.ekonomi.report.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Year;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static se.montesmites.ekonomi.nikka.NikkaSection.*;
import static se.montesmites.ekonomi.report.Column.DESCRIPTION;

class Main {

    public static void main(String[] args) throws Exception {
        var year = Year.of(2017);
        var fmt = "c:/temp/nikka/Kassaflöde %d.txt";
        var fileName = String.format(fmt, year.getValue());
        var path = Paths.get(fileName);
        var main = new Main();
        var report = main.generateCashflowReport(year);
        main.renderToFile(report, path);
    }

    private final CashflowDataFetcher fetcher;

    private Main() {
        var path = Paths.get("C:\\ProgramData\\SPCS\\SPCS Administration\\Företag\\nikka");
        var organization = new OrganizationBuilder(path).build();
        this.fetcher = new CashflowDataFetcher(organization);
    }

    private CashflowReport generateCashflowReport(Year year) {
        var foreJamforelsestorandePoster
                = new TotallingSection(
                "Före jämförelsestörande poster",
                sections(year, INKOMSTER, BOENDE, FORNODENHETER, OVRIGT)
        );
        var total
                = new TotallingSection(
                        "Kontrollsumma",
                        sections(year, INKOMSTER, BOENDE, FORNODENHETER, OVRIGT, JAMFORELSESTORANDE_POSTER, FORANDRING_LIKVIDA_MEDEL)) {
            @Override
            public Row wrapSectionRow(Section section, Row row) {
                if (sectionEquals(section, FORANDRING_LIKVIDA_MEDEL)) {
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
        var accumulation
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
                year, () -> Stream.of(
                        s(year, INKOMSTER),
                s(year, BOENDE),
                s(year, FORNODENHETER),
                s(year, OVRIGT),
                new CompactSection(foreJamforelsestorandePoster),
                s(year, JAMFORELSESTORANDE_POSTER),
                new CompactSection(s(year, FORANDRING_LIKVIDA_MEDEL)),
                new CompactSection(total),
                accumulation));
    }

    private void renderToFile(CashflowReport report, Path path) throws IOException {
        Files.write(path, report.render());
    }

    private List<Section> sections(Year year, NikkaSection... sections) {
        return Arrays.stream(sections)
                .map(section -> section.section(fetcher, year))
                .collect(toList());

    }

    private Section s(Year year, NikkaSection section) {
        return section.section(fetcher, year);
    }
}
