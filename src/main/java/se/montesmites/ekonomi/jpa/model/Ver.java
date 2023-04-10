package se.montesmites.ekonomi.jpa.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import java.time.LocalDate;
import se.montesmites.ekonomi.model.Event;
import se.montesmites.ekonomi.model.EventId;
import se.montesmites.ekonomi.model.Series;
import se.montesmites.ekonomi.model.YearId;

@Entity
@Table(name = "VER")
@IdClass(VerId.class)
public class Ver {

  @Id
  @Column(name = "AAR_ID")
  private String bokfaarId;

  @Id
  @Column(name = "VER_SERIE")
  private String verserie;

  @Id
  @Column(name = "VER_NR")
  private Integer vernr;

  @Column(name = "TDATUM")
  private LocalDate transaktionsdatum;

  @Column(name = "TXT")
  private String beskrivning;

  @Column(name = "RDATUM")
  private LocalDate registreringsdatum;

  public Event toEvent() {
    return new Event(
        new EventId(new YearId(bokfaarId), vernr, new Series(verserie)),
        transaktionsdatum,
        beskrivning,
        registreringsdatum);
  }
}
