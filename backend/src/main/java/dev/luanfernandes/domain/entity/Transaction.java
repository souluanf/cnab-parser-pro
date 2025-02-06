package dev.luanfernandes.domain.entity;

import static jakarta.persistence.GenerationType.IDENTITY;

import dev.luanfernandes.domain.enums.TransactionType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(
        name = "transactions",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"type", "date", "value", "cpf", "card", "hour"})})
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Transaction {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private TransactionType type;

    private LocalDate date;
    private BigDecimal value;
    private String cpf;
    private String card;
    private LocalTime hour;
    private String storeOwner;
    private String storeName;
}
