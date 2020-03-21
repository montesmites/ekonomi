package se.montesmites.ekonomi.jpa.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import se.montesmites.ekonomi.model.AccountId;
import se.montesmites.ekonomi.model.Currency;
import se.montesmites.ekonomi.model.Entry;
import se.montesmites.ekonomi.model.EntryStatus;
import se.montesmites.ekonomi.model.EventId;
import se.montesmites.ekonomi.model.Series;
import se.montesmites.ekonomi.model.YearId;

@Entity
@Table(name = "VERRAD")
@IdClass(VerradId.class)
public class Verrad {

  @Id
  @Column(name = "AAR_ID")
  private String bokfaarId;

  @Id
  @Column(name = "VER_SERIE")
  private String verserie;

  @Id
  @Column(name = "VER_NR")
  private Integer vernr;

  @Id
  @Column(name = "RAD")
  private Integer rad;

  @Column(name = "KONTO")
  private String kontoId;

  @Column(name = "BELOPP")
  private Long belopp;

  @Column(name = "DEFINITIV")
  private Boolean definitiv;

  @Column(name = "STRUKEN")
  private Boolean struken;

  @Column(name = "TILLAGD")
  private Boolean tillagd;

  public Entry toEntry() {
    return new Entry(
        new EventId(new YearId(bokfaarId), vernr, new Series(verserie)),
        new AccountId(new YearId(bokfaarId), kontoId),
        new Currency(belopp),
        new EntryStatus(definitiv, struken, tillagd));
  }
}
