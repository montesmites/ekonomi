package se.montesmites.ekonomi.jpa.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.experimental.Accessors;
import se.montesmites.ekonomi.model.Account;
import se.montesmites.ekonomi.model.AccountId;
import se.montesmites.ekonomi.model.AccountStatus;
import se.montesmites.ekonomi.model.YearId;

@Entity
@Table(name = "KONTO")
@IdClass(KontoId.class)
@Getter
@Accessors(fluent = true)
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
