package se.montesmites.ekonomi.jpa.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import se.montesmites.ekonomi.model.AccountId;
import se.montesmites.ekonomi.model.Balance;
import se.montesmites.ekonomi.model.Currency;
import se.montesmites.ekonomi.model.YearId;

@Entity
@Table(name = "REING")
public class Reing {

  @Id
  @Column(name = "ID")
  private Integer id;

  @Column(name = "AAR_ID")
  private String bokfaarId;

  @Column(name = "KONTO")
  private String kontoId;

  @Column(name = "ING_BAL")
  private Long ingaendeBalans;

  public Balance toBalance() {
    return new Balance(new AccountId(new YearId(bokfaarId), kontoId), new Currency(ingaendeBalans));
  }
}
