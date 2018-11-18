package se.montesmites.ekonomi.nikka;

import se.montesmites.ekonomi.report.*;

import java.time.Year;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static se.montesmites.ekonomi.nikka.NikkaAccountGroup.*;
import static se.montesmites.ekonomi.report.HeaderRow.HeaderType.HEADER_TYPE_SHORT_MONTHS;

enum NikkaSection {
    INKOMSTER(
            "Inkomster",
            List.of(LONEINBETALNINGAR,
                    NETTOOMSATTNING_OVRIGT
            )),
    BOENDE(
            "Boende",
            List.of(
                    MANADSAVGIFT,
                    AMORTERING_FREDSGATAN_13,
                    BOLAN_RANTA,
                    HEMFORSAKRING,
                    EL,
                    MOBIL_TV_BREDBAND
            )),
    FORNODENHETER(
            "Förnödenheter",
            List.of(
                    DAGLIGVAROR,
                    KLADER_OCH_SKOR,
                    KROPP_OCH_SJAL,
                    PERSONFORSAKRINGAR,
                    AKASSA_FACK_BANK_SKATT,
                    TRANSPORTER
            )),
    OVRIGT(
            "Övrigt",
            List.of(
                    BOENDE_DIVERSE,
                    NikkaAccountGroup.OVRIGT
            )),
    JAMFORELSESTORANDE_POSTER(
            "Jämförelsestörande poster",
            List.of(
                    KORTFRISTIGT_NETTO,
                    LANGSIKTIGT_NETTO,
                    FINANSIELLT_NETTO,
                    INVESTERING_BOENDE,
                    EXTRAORDINART_NETTO)),
    FORANDRING_LIKVIDA_MEDEL(
            "Förändring likvida medel",
            List.of(
                    LIKVIDA_MEDEL
            )) {
        @Override
        RowWithAccounts bodyRow(NikkaAccountGroup group, CashflowDataFetcher fetcher, Year year) {
            return new DefaultRowWithAccountsWithNegatedAmounts(
                    super.bodyRow(group, fetcher, year));
        }
    };

    private final String title;
    private final List<NikkaAccountGroup> groups;

    NikkaSection(String title, List<NikkaAccountGroup> groups) {
        this.title = title;
        this.groups = groups;
    }

    public String getTitle() {
        return title;
    }

    Section section(CashflowDataFetcher fetcher, java.time.Year year) {
        var bodyRows = bodyRows(fetcher, year);
        return Section.of(() -> title, () -> HEADER_TYPE_SHORT_MONTHS, bodyRows, () -> bodyRows);
    }

    private Supplier<Stream<Row>> bodyRows(CashflowDataFetcher fetcher, Year year) {
        return () -> groups.stream().map(group -> bodyRow(group, fetcher, year));
    }

    RowWithAccounts bodyRow(NikkaAccountGroup group, CashflowDataFetcher fetcher, java.time.Year year) {
        return group.bodyRow(fetcher, year);
    }
}
