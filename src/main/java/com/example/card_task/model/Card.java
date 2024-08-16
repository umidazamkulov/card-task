package com.example.card_task.model;


import jakarta.persistence.*;
import java.util.UUID;

@Entity
public class Card {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID cardId;

    private Long userId;

    @Enumerated(EnumType.STRING)
    private CardStatus status;

    private Long balance;

    @Enumerated(EnumType.STRING)
    private Currency currency;

    public enum CardStatus {
        ACTIVE, BLOCKED, CLOSED
    }

    public enum Currency {
        UZS, USD
    }

    public UUID getCardId() {
        return cardId;
    }

    public void setCardId(UUID cardId) {
        this.cardId = cardId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public CardStatus getStatus() {
        return status;
    }

    public void setStatus(CardStatus status) {
        this.status = status;
    }

    public Long getBalance() {
        return balance;
    }

    public void setBalance(Long balance) {
        this.balance = balance;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }
}

