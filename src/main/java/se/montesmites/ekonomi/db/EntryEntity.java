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
@Table(name = "entry")
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class EntryEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "entry_generator")
  @SequenceGenerator(name = "entry_generator", sequenceName = "entry_seq", allocationSize = 1)
  @Column(name = "entry_id")
  private Long entryId;

  @ManyToOne
  @JoinColumn(name = "event_id")
  private EventEntity event;

  @ManyToOne
  @JoinColumn(name = "account_id")
  private AccountEntity account;

  @Column(name = "amount")
  private BigDecimal amount;
}
