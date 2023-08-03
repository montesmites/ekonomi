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
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "balance")
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class BalanceEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "balance_generator")
  @SequenceGenerator(name = "balance_generator", sequenceName = "balance_seq", allocationSize = 1)
  @Column(name = "balance_id")
  private Long balanceId;

  @ManyToOne
  @JoinColumn(name = "account_id")
  private AccountEntity account;

  @Column(name = "balance")
  private BigDecimal balance;
}
