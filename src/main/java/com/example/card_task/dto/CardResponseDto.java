package com.example.card_task.dto;

import com.example.card_task.model.Card.CardStatus;
import com.example.card_task.model.Card.Currency;

import java.util.UUID;

public class CardResponseDto {

    private UUID cardId;
    private Long userId;
    private CardStatus status;
    private Long balance;
    private Currency currency;

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
