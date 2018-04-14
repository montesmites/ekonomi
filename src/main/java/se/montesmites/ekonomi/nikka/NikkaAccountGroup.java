package se.montesmites.ekonomi.nikka;

import se.montesmites.ekonomi.report.*;

enum NikkaAccountGroup {
    LONEINBETALNINGAR("Löner och arvoden", "(30|36)\\d\\d"),
    NETTOOMSATTNING_OVRIGT("Nettoomsättning övrigt", "3([1-5]|[7-9])\\d\\d"),
    DAGLIGVAROR("Dagligvaror", "40\\d\\d"),
    KLADER_OCH_SKOR("Kläder och skor", "41\\d\\d"),
    KROPP_OCH_SJAL("Kropp och själ", "42\\d\\d"),
    PERSONFORSAKRINGAR("Personförsäkringar", "43\\d\\d"),
    AKASSA_FACK_BANK_SKATT("A-kassa, fack, bank, skatt", "4[4-7]\\d\\d"),
    MANADSAVGIFT("Månadsavgift m.m.", "501\\d"),
    EL("El (förbrukning och nät)", "502\\d"),
    HEMFORSAKRING("Hemförsäkring", "5081"),
    MOBIL_TV_BREDBAND("Mobil, tv, bredband", "51\\d\\d"),
    BOENDE_DIVERSE("Boende diverse", "5060|5[458]\\d\\d"),
    BOLAN_RANTA("Bolån ränta", "84[01]\\d"),
    TRANSPORTER("Transporter", "56\\d\\d"),
    OVRIGT("Övrigt", "(4[89]|[67]\\d)\\d\\d"),
    KORTFRISTIGT_NETTO("Kortfristigt netto", "(1[5-8]|2[4-9])\\d\\d"),
    AMORTERING_FREDSGATAN_13("Amortering Fredsgatan 13", "2353"),
    INVESTERING_BOENDE("Investering boende", "11\\d\\d"),
    LANGSIKTIGT_NETTO("Långsiktigt netto", "(10\\d|13[456])\\d"),
    FINANSIELLT_NETTO("Finansiellt netto", "8(3\\d|4[2-9])\\d"),
    EXTRAORDINART_NETTO("Extraordinärt netto", "87\\d\\d"),
    LIKVIDA_MEDEL("Likvida medel", "1493|19\\d\\d");

    private final String description;
    private final String regex;

    NikkaAccountGroup(String description, String regex) {
        this.description = description;
        this.regex = regex;
    }

    RowWithAccounts bodyRow(CashflowDataFetcher fetcher, java.time.Year year) {
        final AccountFilter filter = new AccountFilterByRegex(regex);
        return new DefaultRowWithAccounts(fetcher, () -> filter.filter(fetcher.streamAccountIds(year)), year, description);
    }
}
