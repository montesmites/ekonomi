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
    NETTOOMSATTNING_OVR("Nettoomsättning övr.", "3([1-5]|[7-9])\\d\\d"),
    DAGLIGVAROR("Dagligvaror", "40\\d\\d"),
    KLADER_OCH_SKOR("Kläder och skor", "41\\d\\d"),
    BOENDE_EL_TELEKOM("Boende, el, telekom.", "5([0-5]|[7-9])\\d\\d"),
    TRANSPORTER("Transporter", "56\\d\\d"),
    OVRIGT("Övrigt", "(4[2-9]|[67]\\d)\\d\\d"),
    KORTFRISTIGA_PLACERINGAR("Kortfr. placeringar", "18\\d\\d"),
    KORTFRISTIGA_FORDRINGAR("Kortfr. fordringar", "1[567]\\d\\d"),
    KORTFRISTIGA_SKULDER("Kortfr. skulder", "2[4-9]\\d\\d"),
    LAN_FREDSGATAN_13("Lån Fredsgatan 13", "2353"),
    INVESTERING_BOENDE("Investering boende", "11\\d\\d"),
    FRITT_SPARANDE("Långsiktigt sparande", "13[45]\\d"),
    PENSIONSAVSATTNINGAR("Pensionsavsättningar", "10\\d\\d"),
    NETTOUTLANING("Nettoutlåning", "136\\d"),
    FINANSIELLA_INTAKTER("Finansiella intäkter", "83\\d\\d"),
    FINANSIELLA_KOSTNADER("Finansiella kostn.", "84\\d\\d"),
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
