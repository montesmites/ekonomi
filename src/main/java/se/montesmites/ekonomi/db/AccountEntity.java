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
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "account")
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class AccountEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "account_generator")
  @SequenceGenerator(name = "account_generator", sequenceName = "account_seq", allocationSize = 1)
  @Column(name = "account_id")
  private Long accountId;

  @ManyToOne
  @JoinColumn(name = "fiscal_year_id")
  private FiscalYearEntity fiscalYear;

  @Column(name = "qualifier")
  private String qualifier;

  @Column(name = "name")
  private String name;

  @Column(name = "active")
  private boolean active;
}
