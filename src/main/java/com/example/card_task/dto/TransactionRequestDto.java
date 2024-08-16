package com.example.card_task.dto;


import com.example.card_task.model.Transaction.Currency;
import com.example.card_task.model.Transaction.TransactionType;

public class TransactionRequestDto {

    private String externalId;
    private Long amount;
    private Currency currency;
    private TransactionType type;

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }
}
