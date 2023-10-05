package se.montesmites.ekonomi.nikka;

import java.time.Year;

@SuppressWarnings("unused")
enum NikkaReport {
  RESULT_REPORT_2018(Year.of(2018)) {
    /*
        @Override
        Report generateReport(DataFetcher dataFetcher) {
          return new ReportBuilder(dataFetcher, dataFetcher, this.getYear())
              .tags(Set.of(Tag.of("Bruttoresultat")))
              .accounts("Intäkter", "3\\d\\d\\d", AmountsProvider::self)
              .accounts("Förnödenheter", "4\\d\\d\\d", AmountsProvider::self)
              .accounts("Boende", "50\\d\\d", AmountsProvider::self)
              .accounts("Övriga kostnader", "(5[1-9]|[67]\\d)\\d\\d", AmountsProvider::self)
              .accounts("Finansiellt netto", "8[3456]\\d\\d", AmountsProvider::self)
              .subtotal(
                  subtotal ->
                      subtotal
                          .description("BRUTTORESULTAT")
                          .tagFilter(TagFilter.isEqualTo(Tag.of("Bruttoresultat"))))
              .accounts("Extraordinärt netto", "87\\d\\d", AmountsProvider::self)
              .subtotal(subtotal -> subtotal.description("BERÄKNAT RESULTAT"))
              .subtotal(
                  subtotal ->
                      subtotal
                          .description("KONTROLLSUMMA")
                          .addenda(
                              List.of(
                                  AmountsProvider.of(
                                      dataFetcher,
                                      this.getYear(),
                                      AccountGroup.of("Kontrollsumma", "([3-7]\\d|8[1-7])\\d\\d")))))
              .accumulateAccountGroups(
                  "Ackumulerat resultat",
                  List.of(AccountGroup.of("Kontrollsumma", "([3-7]\\d|8[1-7])\\d\\d")))
              .report();
        }
    */
  },
  BALANCE_REPORT_2018(Year.of(2018)) {
    /*
        @Override
        Report generateReport(DataFetcher dataFetcher) {
          return new ReportBuilder(dataFetcher, dataFetcher, this.getYear())
              .accounts("Immateriella anläggningstillgångar", "10\\d\\d", AmountsProvider::self)
              .accounts("Materiella anläggningstillgångar", "11\\d\\d", AmountsProvider::self)
              .accounts("Finansiella anläggningstillgångar", "13\\d\\d", AmountsProvider::self)
              .accounts("Buffertsparande", "1493", AmountsProvider::self)
              .accounts("Fordringar", "17\\d\\d", AmountsProvider::self)
              .accounts("Kortfristiga placeringar", "18\\d\\d", AmountsProvider::self)
              .accounts("Kassa och bank", "19\\d\\d", AmountsProvider::self)
              .accounts("Eget kapital", "20\\d\\d", AmountsProvider::negate)
              .accounts("Obeskattade reserver", "21\\d\\d", AmountsProvider::negate)
              .accounts("ROT/RUT-avdrag", "22\\d\\d", AmountsProvider::negate)
              .accounts("Långfristiga skulder", "23\\d\\d", AmountsProvider::negate)
              .accounts("Kortfristiga skulder", "24\\d\\d", AmountsProvider::negate)
              .report();
        }
    */
  };

  private final Year year;

  NikkaReport(Year year) {
    this.year = year;
  }

  final Year getYear() {
    return year;
  }

  //  abstract Report generateReport(DataFetcher dataFetcher);
}
