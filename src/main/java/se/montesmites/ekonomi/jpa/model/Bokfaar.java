package se.montesmites.ekonomi.jpa.model;

import java.time.LocalDate;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import se.montesmites.ekonomi.model.Year;
import se.montesmites.ekonomi.model.YearId;

@Entity
@Table(name = "BOKFAAR")
public class Bokfaar {

  @Id
  @Column(name = "AAR_ID")
  private String aarId;

  @Column(name = "BOERJAN")
  private LocalDate boerjan;

  @Column(name = "SLUT")
  private LocalDate slut;

  public Year toYear() {
    return new Year(new YearId(aarId), java.time.Year.of(boerjan.getYear()), boerjan, slut);
  }
}
