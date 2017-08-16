package se.montesmites.ekonomi.nikka;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Year;
import java.util.Arrays;
import java.util.function.Supplier;
import static java.util.stream.Collectors.toList;
import java.util.stream.Stream;
import se.montesmites.ekonomi.organization.Organization;
import se.montesmites.ekonomi.report.AccountFilterByRegex;
import se.montesmites.ekonomi.report.AccumulatingRow;
import se.montesmites.ekonomi.report.AccumulatingSection;
import se.montesmites.ekonomi.report.CashflowDataFetcher;
import se.montesmites.ekonomi.report.CashflowReport;
import se.montesmites.ekonomi.report.Coefficient;
import se.montesmites.ekonomi.report.Section;
import se.montesmites.ekonomi.report.TotallingSection;

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

    private final Organization organization;
    private final CashflowDataFetcher fetcher;

    Main() {
        Path path = Paths.get(
                "C:\\ProgramData\\SPCS\\SPCS Administration\\Företag\\nikka");
        this.organization = Organization.fromPath(path);
        this.fetcher = new CashflowDataFetcher(this.organization);
    }

    private CashflowReport generateCashflowReport(Year year) {
        Supplier<Stream<Section>> sections
                = () -> Arrays.stream(NikkaSection.values())
                        .map(section -> section.section(fetcher, year));
        TotallingSection total
                = new TotallingSection(
                        "Kontrollsumma",
                        sections.get().collect(toList()));
        AccumulatingSection accumlation
                = new AccumulatingSection(
                        "Ackumulerade likvida medel (inkl. SBAB-konto)",
                        () -> Stream.of(
                                new AccumulatingRow(
                                        fetcher,
                                        () -> new AccountFilterByRegex(
                                                "1493|19\\d\\d")
                                                .filter(fetcher.streamAccountIds(
                                                        year)),
                                        year,
                                        Coefficient.NEGATE)));
        CashflowReport report
                = new CashflowReport(
                        fetcher,
                        year, () -> Stream.concat(
                                sections.get(),
                                Stream.of(total, accumlation)));
        return report;
    }

    private void renderToFile(CashflowReport report, Path path) throws IOException {
        Files.write(path, report.render());
    }
}
