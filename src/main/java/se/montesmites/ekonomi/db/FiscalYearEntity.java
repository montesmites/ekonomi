package se.montesmites.ekonomi.db;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.time.Year;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "fiscal_year")
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class FiscalYearEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "fiscal_year_generator")
  @SequenceGenerator(
      name = "fiscal_year_generator",
      sequenceName = "fiscal_year_seq",
      allocationSize = 1)
  @Column(name = "fiscal_year_id")
  private Long fiscalYearId;

  @Column(name = "fiscal_calendar_year")
  private Year calendarYear;
}
