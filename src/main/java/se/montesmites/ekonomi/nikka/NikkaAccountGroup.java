package se.montesmites.ekonomi.nikka;

import static java.util.Comparator.comparing;
import java.util.List;
import static java.util.stream.Collectors.toList;
import se.montesmites.ekonomi.model.AccountId;
import se.montesmites.ekonomi.report.AccountFilter;
import se.montesmites.ekonomi.report.AccountFilterByRegex;
import se.montesmites.ekonomi.report.CashflowDataFetcher;
import se.montesmites.ekonomi.report.DefaultRowWithAccounts;
import se.montesmites.ekonomi.report.RowWithAccounts;

enum NikkaAccountGroup {
    LONEINBETALNINGAR("Löner och arvoden", "(30|36)\\d\\d"),
    NETTOOMSATTNING_OVRIGT("Nettoomsättning övrigt", "3([1-5]|[7-9])\\d\\d"),
    DAGLIGVAROR("Dagligvaror", "40\\d\\d"),
    KLADER_OCH_SKOR("Kläder och skor", "41\\d\\d"),
    MANADSAVGIFT("Månadsavgift m.m.", "501\\d"),
    EL("El (förbrukning och nät)", "502\\d"),
    HEMFORSAKRING("Hemförsäkring", "5081"),
    MOBIL_TV_BREDBAND("Mobil, tv, bredband", "51\\d\\d"),
    BOENDE_DIVERSE("Boende diverse", "5060|5[458]\\d\\d"),
    BOLAN_RANTA("Bolån ränta", "84[01]\\d"),
    TRANSPORTER("Transporter", "56\\d\\d"),
    OVRIGT("Övrigt", "(4[2-9]|[67]\\d)\\d\\d"),
    KORTFRISTIGT_NETTO("Kortfristigt netto", "(1[5-8]|2[4-9])\\d\\d"),
    AMORTERING_FREDSGATAN_13("Amortering Fredsgatan 13", "2353"),
    INVESTERING_BOENDE("Investering boende", "11\\d\\d"),
    LANGSIKTIGT_NETTO("Långsiktigt netto", "(10\\d|13[456])\\d"),
    FINANSIELLT_NETTO("Finansiellt netto", "8[34][2-9]\\d"),
    EXTRAORDINART_NETTO("Extraordinärt netto", "87\\d\\d"),
    SBAB_SPAR_NETTO("SBAB-spar netto", "1493"),
    OVRIGA_LIKVIDA_MEDEL("Övriga likvida medel", "19\\d\\d");

    private final String description;
    private final String regex;

    private NikkaAccountGroup(String description, String regex) {
        this.description = description;
        this.regex = regex;
    }

    RowWithAccounts bodyRow(CashflowDataFetcher fetcher, java.time.Year year) {
        final AccountFilter filter = new AccountFilterByRegex(regex);
        List<AccountId> accountIds
                = filter
                        .filter(fetcher.streamAccountIds(year))
                        .distinct()
                        .sorted(comparing(AccountId::getId))
                        .collect(toList());
        return new DefaultRowWithAccounts(
                fetcher,
                () -> accountIds.stream(),
                year,
                description);
    }
}
