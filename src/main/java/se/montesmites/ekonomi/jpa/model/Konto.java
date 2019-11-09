package se.montesmites.ekonomi.jpa.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import se.montesmites.ekonomi.model.Account;
import se.montesmites.ekonomi.model.AccountId;
import se.montesmites.ekonomi.model.AccountStatus;
import se.montesmites.ekonomi.model.YearId;

@Entity
@Table(name = "KONTO")
@IdClass(KontoId.class)
public class Konto {

  @Id
  @Column(name = "AAR_ID")
  private String bokfaarId;

  @Id
  @Column(name = "KONTO")
  private String kontoId;

  @Column(name = "TXT")
  private String beskrivning;

  @Column(name = "EJ_AKTIVT")
  private Boolean inaktivt;

  public Account toAccount() {
    return new Account(
        new AccountId(new YearId(bokfaarId), kontoId),
        beskrivning,
        inaktivt ? AccountStatus.CLOSED : AccountStatus.OPEN);
  }
}
