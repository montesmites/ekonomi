package se.montesmites.ekonomi.nikka;

import java.util.Arrays;
import java.util.List;
import static se.montesmites.ekonomi.nikka.NikkaAccountGroup.*;
import se.montesmites.ekonomi.report.CashflowDataFetcher;
import se.montesmites.ekonomi.report.DefaultSection;
import se.montesmites.ekonomi.report.Section;

enum NikkaSection {
    ARBETE_BOENDE_FRITID(
            "Arbete, boende, fritid",
            Arrays.asList(
                    LONEINBETALNINGAR,
                    NETTOOMSATTNING_OVR,
                    DAGLIGVAROR,
                    KLADER_OCH_SKOR,
                    BOENDE_EL_TELEKOM,
                    TRANSPORTER,
                    OVRIGT,
                    KORTFRISTIGA_PLACERINGAR,
                    KORTFRISTIGA_FORDRINGAR,
                    KORTFRISTIGA_SKULDER)),
    FINANSIELLA_OCH_EXTRAORDINARA_POSTER(
            "Finansiella och extraordinära poster",
            Arrays.asList(
                    FINANSIELLA_INTAKTER,
                    FINANSIELLA_KOSTNADER,
                    EXTRAORDINART_NETTO
            )),
    AGANDE_AV_BOSTAD(
            "Ägande av bostad",
            Arrays.asList(
                    LAN_FREDSGATAN_13,
                    INVESTERING_BOENDE
            )),
    LANGSIKTIGT_SPARANDE(
            "Långsiktigt sparande",
            Arrays.asList(
                    FRITT_SPARANDE,
                    PENSIONSAVSATTNINGAR)),
    UTLANINGSVERKSAMHET(
            "Utlåningsverksamhet",
            Arrays.asList(
                    NETTOUTLANING)),
    FORANDRING_LIKVIDA_MEDEL(
            "Förändring likvida medel (OBS! omvänt tecken)",
            Arrays.asList(
                    SBAB_SPAR_NETTO,
                    OVRIGA_LIKVIDA_MEDEL
            ));

    private final String title;
    private final List<NikkaAccountGroup> groups;

    private NikkaSection(
            String title,
            List<NikkaAccountGroup> groups) {
        this.title = title;
        this.groups = groups;
    }

    Section section(CashflowDataFetcher fetcher, java.time.Year year) {
        return new DefaultSection(
                title,
                () -> groups.stream().map(group -> group.bodyRow(fetcher, year)));
    }
}
