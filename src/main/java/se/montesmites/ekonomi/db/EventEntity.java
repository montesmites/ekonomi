package se.montesmites.ekonomi.db;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "event")
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class EventEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "event_generator")
  @SequenceGenerator(name = "event_generator", sequenceName = "event_seq", allocationSize = 1)
  @Column(name = "event_id")
  private Long eventId;

  @ManyToOne
  @JoinColumn(name = "fiscal_year_id")
  private FiscalYearEntity fiscalYear;

  @Column(name = "event_no")
  private Integer eventNo;

  @Column(name = "event_date")
  private LocalDate date;

  @Column(name = "description")
  private String description;
}
