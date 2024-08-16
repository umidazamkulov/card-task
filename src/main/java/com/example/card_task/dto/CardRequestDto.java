package com.example.card_task.dto;

import com.example.card_task.model.Card.CardStatus;
import com.example.card_task.model.Card.Currency;

public class CardRequestDto {

    private Long userId;
    private CardStatus status;
    private Long initialAmount;
    private Currency currency;

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

    public Long getInitialAmount() {
        return initialAmount;
    }

    public void setInitialAmount(Long initialAmount) {
        this.initialAmount = initialAmount;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }
}
