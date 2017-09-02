package se.montesmites.ekonomi.nikka;

import se.montesmites.ekonomi.report.*;

import java.time.Year;
import java.util.Arrays;
import java.util.List;

import static se.montesmites.ekonomi.nikka.NikkaAccountGroup.*;

enum NikkaSection {
    INKOMSTER(
            "Inkomster",
            Arrays.asList(LONEINBETALNINGAR,
                    NETTOOMSATTNING_OVRIGT
            )),
    BOENDE(
            "Boende",
            Arrays.asList(
                    MANADSAVGIFT,
                    AMORTERING_FREDSGATAN_13,
                    BOLAN_RANTA,
                    HEMFORSAKRING,
                    EL,
                    MOBIL_TV_BREDBAND
            )),
    FORNODENHETER(
            "Förnödenheter",
            Arrays.asList(
                    DAGLIGVAROR,
                    KLADER_OCH_SKOR,
                    KROPP_OCH_SJAL,
                    AKASSA_FACK_BANK_SKATT,
                    TRANSPORTER
            )),
    OVRIGT(
            "Övrigt",
            Arrays.asList(
                    BOENDE_DIVERSE,
                    NikkaAccountGroup.OVRIGT,
                    KORTFRISTIGT_NETTO
            )),
    JAMFORELSESTORANDE_POSTER(
            "Jämförelsestörande poster",
            Arrays.asList(
                    INVESTERING_BOENDE,
                    FINANSIELLT_NETTO,
                    EXTRAORDINART_NETTO,
                    LANGSIKTIGT_NETTO)),
    FORANDRING_LIKVIDA_MEDEL(
            "Förändring likvida medel",
            Arrays.asList(
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

    private NikkaSection(
            String title,
            List<NikkaAccountGroup> groups) {
        this.title = title;
        this.groups = groups;
    }

    public String getTitle() {
        return title;
    }

    Section section(CashflowDataFetcher fetcher, java.time.Year year) {
        return new DefaultSection(
                title,
                () -> groups.stream().map(group -> bodyRow(group, fetcher, year)));
    }

    RowWithAccounts bodyRow(NikkaAccountGroup group, CashflowDataFetcher fetcher, java.time.Year year) {
        return group.bodyRow(fetcher, year);
    }
}
