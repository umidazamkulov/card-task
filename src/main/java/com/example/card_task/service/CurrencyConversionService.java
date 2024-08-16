package com.example.card_task.service;

import com.example.card_task.model.Card;
import com.example.card_task.model.Transaction;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Service
public class CurrencyConversionService {

    private static final Map<String, BigDecimal> exchangeRates = new HashMap<>();

    static {
        // Test uchun: 1 USD = 12000 UZS, 1 UZS = 0.000083 USD
        exchangeRates.put("USD_TO_UZS", new BigDecimal("12000"));
        exchangeRates.put("UZS_TO_USD", new BigDecimal("0.000083"));
    }

    public long convertAmount(long amount, BigDecimal conversionRate) {
        return conversionRate.multiply(new BigDecimal(amount)).longValue();
    }


    public BigDecimal getConversionRate(Card.Currency fromCurrency, Card.Currency toCurrency) {
        if (fromCurrency == toCurrency) {
            return BigDecimal.ONE;
        }

        String conversionKey = fromCurrency + "_TO_" + toCurrency;
        BigDecimal conversionRate = exchangeRates.get(conversionKey);

        if (conversionRate == null) {
            throw new IllegalStateException("Conversion rate not available for " + fromCurrency + " to " + toCurrency);
        }

        return conversionRate;
    }

}
