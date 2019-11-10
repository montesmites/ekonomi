package se.montesmites.ekonomi.datasource;

import org.springframework.stereotype.Component;
import se.montesmites.ekonomi.jpa.model.Bokfaar;
import se.montesmites.ekonomi.jpa.model.Konto;
import se.montesmites.ekonomi.jpa.model.Reing;
import se.montesmites.ekonomi.jpa.model.Ver;
import se.montesmites.ekonomi.jpa.model.Verrad;
import se.montesmites.ekonomi.jpa.repository.BokfaarRepository;
import se.montesmites.ekonomi.jpa.repository.KontoRepository;
import se.montesmites.ekonomi.jpa.repository.ReingRepository;
import se.montesmites.ekonomi.jpa.repository.VerRepository;
import se.montesmites.ekonomi.jpa.repository.VerradRepository;
import se.montesmites.ekonomi.organization.Organization;
import se.montesmites.ekonomi.organization.OrganizationBuilder;

@Component
public class DatabaseDatasource {

  private final BokfaarRepository bokfaarRepository;
  private final KontoRepository kontoRepository;
  private final ReingRepository reingRepository;
  private final VerRepository verRepository;
  private final VerradRepository verradRepository;

  public DatabaseDatasource(
      BokfaarRepository bokfaarRepository,
      KontoRepository kontoRepository,
      ReingRepository reingRepository,
      VerRepository verRepository,
      VerradRepository verradRepository) {
    this.bokfaarRepository = bokfaarRepository;
    this.kontoRepository = kontoRepository;
    this.reingRepository = reingRepository;
    this.verRepository = verRepository;
    this.verradRepository = verradRepository;
  }

  public Organization fetchOrganization() {
    var years = bokfaarRepository.findAll().stream().map(Bokfaar::toYear);
    var accounts = kontoRepository.findAll().stream().map(Konto::toAccount);
    var balances =
        reingRepository.findByKontoIdIsNotNullAndIngaendeBalansIsNotNull().stream()
            .map(Reing::toBalance);
    var events = verRepository.findAll().stream().map(Ver::toEvent);
    var entries =
        verradRepository.findByDefinitivAndStruken(true, false).stream().map(Verrad::toEntry);
    return new OrganizationBuilder(years, accounts, balances, events, entries).build();
  }
}
