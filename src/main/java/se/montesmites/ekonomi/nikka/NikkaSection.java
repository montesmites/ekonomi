package se.montesmites.ekonomi.nikka;

import static se.montesmites.ekonomi.nikka.NikkaAccountGroup.AKASSA_FACK_BANK_SKATT;
import static se.montesmites.ekonomi.nikka.NikkaAccountGroup.AMORTERING_FREDSGATAN_13;
import static se.montesmites.ekonomi.nikka.NikkaAccountGroup.BOENDE_DIVERSE;
import static se.montesmites.ekonomi.nikka.NikkaAccountGroup.BOLAN_RANTA;
import static se.montesmites.ekonomi.nikka.NikkaAccountGroup.DAGLIGVAROR;
import static se.montesmites.ekonomi.nikka.NikkaAccountGroup.EL;
import static se.montesmites.ekonomi.nikka.NikkaAccountGroup.EXTRAORDINART_NETTO;
import static se.montesmites.ekonomi.nikka.NikkaAccountGroup.FINANSIELLT_NETTO;
import static se.montesmites.ekonomi.nikka.NikkaAccountGroup.HEMFORSAKRING;
import static se.montesmites.ekonomi.nikka.NikkaAccountGroup.INVESTERING_BOENDE;
import static se.montesmites.ekonomi.nikka.NikkaAccountGroup.KLADER_OCH_SKOR;
import static se.montesmites.ekonomi.nikka.NikkaAccountGroup.KORTFRISTIGT_NETTO;
import static se.montesmites.ekonomi.nikka.NikkaAccountGroup.KROPP_OCH_SJAL;
import static se.montesmites.ekonomi.nikka.NikkaAccountGroup.LANGSIKTIGT_NETTO;
import static se.montesmites.ekonomi.nikka.NikkaAccountGroup.LIKVIDA_MEDEL;
import static se.montesmites.ekonomi.nikka.NikkaAccountGroup.LONEINBETALNINGAR;
import static se.montesmites.ekonomi.nikka.NikkaAccountGroup.MANADSAVGIFT;
import static se.montesmites.ekonomi.nikka.NikkaAccountGroup.MOBIL_TV_BREDBAND;
import static se.montesmites.ekonomi.nikka.NikkaAccountGroup.NETTOOMSATTNING_OVRIGT;
import static se.montesmites.ekonomi.nikka.NikkaAccountGroup.PERSONFORSAKRINGAR;
import static se.montesmites.ekonomi.nikka.NikkaAccountGroup.TRANSPORTER;
import static se.montesmites.ekonomi.report.HeaderRow.SHORT_MONTHS_HEADER;

import java.time.Year;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;
import se.montesmites.ekonomi.report.Body;
import se.montesmites.ekonomi.report.CashflowDataFetcher;
import se.montesmites.ekonomi.report.DefaultRowWithAccountsWithNegatedAmounts;
import se.montesmites.ekonomi.report.Header;
import se.montesmites.ekonomi.report.RowWithAccounts;
import se.montesmites.ekonomi.report.RowWithAmounts;
import se.montesmites.ekonomi.report.Section;

enum NikkaSection {
  INKOMSTER("Inkomster", List.of(LONEINBETALNINGAR, NETTOOMSATTNING_OVRIGT)),
  BOENDE(
      "Boende",
      List.of(
          MANADSAVGIFT,
          AMORTERING_FREDSGATAN_13,
          BOLAN_RANTA,
          HEMFORSAKRING,
          EL,
          MOBIL_TV_BREDBAND)),
  FORNODENHETER(
      "Förnödenheter",
      List.of(
          DAGLIGVAROR,
          KLADER_OCH_SKOR,
          KROPP_OCH_SJAL,
          PERSONFORSAKRINGAR,
          AKASSA_FACK_BANK_SKATT,
          TRANSPORTER)),
  OVRIGT("Övrigt", List.of(BOENDE_DIVERSE, NikkaAccountGroup.OVRIGT)),
  JAMFORELSESTORANDE_POSTER(
      "Jämförelsestörande poster",
      List.of(
          KORTFRISTIGT_NETTO,
          LANGSIKTIGT_NETTO,
          FINANSIELLT_NETTO,
          INVESTERING_BOENDE,
          EXTRAORDINART_NETTO)),
  FORANDRING_LIKVIDA_MEDEL("Förändring likvida medel", List.of(LIKVIDA_MEDEL)) {
    @Override
    RowWithAccounts bodyRow(NikkaAccountGroup group, CashflowDataFetcher fetcher, Year year) {
      return new DefaultRowWithAccountsWithNegatedAmounts(super.bodyRow(group, fetcher, year));
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
    var header = Header.of(() -> title).add(SHORT_MONTHS_HEADER);
    var body = Body.of(bodyRows(fetcher, year));
    return Section.of(header, body);
  }

  private Supplier<Stream<? extends RowWithAmounts>> bodyRows(CashflowDataFetcher fetcher,
      Year year) {
    return () -> groups.stream().map(group -> bodyRow(group, fetcher, year));
  }

  RowWithAccounts bodyRow(
      NikkaAccountGroup group, CashflowDataFetcher fetcher, java.time.Year year) {
    return group.bodyRow(fetcher, year);
  }
}
