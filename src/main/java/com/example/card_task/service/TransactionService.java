package com.example.card_task.service;

import com.example.card_task.dto.TransactionRequestDto;
import com.example.card_task.dto.TransactionResponseDto;
import com.example.card_task.exception.CardNotFoundException;
import com.example.card_task.exception.InactiveCardException;
import com.example.card_task.exception.InsufficientFundsException;
import com.example.card_task.model.Card;
import com.example.card_task.model.Transaction;
import com.example.card_task.repository.CardRepository;
import com.example.card_task.repository.TransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TransactionService {

    private static final Logger logger = LoggerFactory.getLogger(TransactionService.class);

    private final TransactionRepository transactionRepository;
    private final CardRepository cardRepository;
    private final CurrencyConversionService currencyConversionService;

    public TransactionService(TransactionRepository transactionRepository,
                              CardRepository cardRepository,
                              CurrencyConversionService currencyConversionService) {
        this.transactionRepository = transactionRepository;
        this.cardRepository = cardRepository;
        this.currencyConversionService = currencyConversionService;
    }

    public TransactionResponseDto debit(TransactionRequestDto request, UUID cardId) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new CardNotFoundException("Card not found with ID: " + cardId));

        if (card.getStatus() != Card.CardStatus.ACTIVE) {
            logger.warn("Attempted to debit from an inactive card with ID: {}", cardId);
            throw new InactiveCardException("Card with ID " + cardId + " is not active.");
        }

        if (transactionRepository.existsByExternalId(request.getExternalId())) {
            logger.warn("Duplicate transaction attempt with externalId: {}", request.getExternalId());
            return mapToDto(transactionRepository.findByExternalId(request.getExternalId()).get());
        }

        long amountToDebit = request.getAmount();
        if (!card.getCurrency().name().equals(request.getCurrency().name())) {
            BigDecimal conversionRate = currencyConversionService.getConversionRate(
                    Card.Currency.valueOf(request.getCurrency().name()),
                    Card.Currency.valueOf(card.getCurrency().name())
            );
            amountToDebit = currencyConversionService.convertAmount(request.getAmount(), conversionRate);
        }

        if (card.getBalance() < amountToDebit) {
            logger.warn("Insufficient funds for card with ID: {}", cardId);
            throw new InsufficientFundsException("Insufficient funds on card with ID: " + cardId);
        }

        card.setBalance(card.getBalance() - amountToDebit);
        cardRepository.save(card);

        Transaction transaction = new Transaction();
        transaction.setCard(card);
        transaction.setAmount(amountToDebit);
        transaction.setCurrency(Transaction.Currency.valueOf(card.getCurrency().name()));
        transaction.setType(Transaction.TransactionType.DEBIT);
        transaction.setExternalId(request.getExternalId());

        Transaction savedTransaction = transactionRepository.save(transaction);

        logger.info("Debited {} from card with ID: {}. New balance: {}", amountToDebit, cardId, card.getBalance());

        return mapToDto(savedTransaction);
    }

    public TransactionResponseDto credit(TransactionRequestDto request, UUID cardId) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new CardNotFoundException("Card not found with ID: " + cardId));

        if (card.getStatus() != Card.CardStatus.ACTIVE) {
            logger.warn("Attempted to credit to an inactive card with ID: {}", cardId);
            throw new InactiveCardException("Card with ID " + cardId + " is not active.");
        }

        // Check if a transaction with the same externalId already exists for idempotency
        if (transactionRepository.existsByExternalId(request.getExternalId())) {
            logger.warn("Duplicate transaction attempt with externalId: {}", request.getExternalId());
            return mapToDto(transactionRepository.findByExternalId(request.getExternalId()).get());
        }

        // Currency conversion if the transaction currency differs from the card's currency
        long amountToCredit = request.getAmount();
        if (!card.getCurrency().name().equals(request.getCurrency().name())) {
            BigDecimal conversionRate = currencyConversionService.getConversionRate(
                    Card.Currency.valueOf(request.getCurrency().name()),
                    Card.Currency.valueOf(card.getCurrency().name())
            );
            amountToCredit = currencyConversionService.convertAmount(request.getAmount(), conversionRate);
        }

        card.setBalance(card.getBalance() + amountToCredit);
        cardRepository.save(card);

        Transaction transaction = new Transaction();
        transaction.setCard(card);
        transaction.setAmount(amountToCredit);
        transaction.setCurrency(Transaction.Currency.valueOf(card.getCurrency().name()));
        transaction.setType(Transaction.TransactionType.CREDIT);
        transaction.setExternalId(request.getExternalId());

        Transaction savedTransaction = transactionRepository.save(transaction);

        logger.info("Credited {} to card with ID: {}. New balance: {}", amountToCredit, cardId, card.getBalance());

        return mapToDto(savedTransaction);
    }

    public List<TransactionResponseDto> getTransactionHistory(UUID cardId, Transaction.TransactionType type,
                                                              String transactionId, String externalId,
                                                              Transaction.Currency currency, int page, int size) {

        cardRepository.findById(cardId).orElseThrow(() -> new CardNotFoundException("Card not found"));

        Pageable pageable = PageRequest.of(page, size);
        Specification<Transaction> spec = buildTransactionSpecification(cardId, type, transactionId, externalId, currency);

        List<Transaction> transactions = transactionRepository.findAll(spec, pageable).getContent();

        return transactions.stream().map(transaction -> mapToDto(transaction)).collect(Collectors.toList());
    }

    private Specification<Transaction> buildTransactionSpecification(UUID cardId, Transaction.TransactionType type,
                                                                     String transactionId, String externalId,
                                                                     Transaction.Currency currency) {
        return (root, query, cb) -> {
            var predicates = cb.conjunction();

            if (type != null) {
                predicates = cb.and(predicates, cb.equal(root.get("type"), type));
            }

            if (transactionId != null && !transactionId.isEmpty()) {
                predicates = cb.and(predicates, cb.equal(root.get("transactionId"), UUID.fromString(transactionId)));
            }

            if (externalId != null && !externalId.isEmpty()) {
                predicates = cb.and(predicates, cb.equal(root.get("externalId"), externalId));
            }

            if (currency != null) {
                predicates = cb.and(predicates, cb.equal(root.get("currency"), currency));
            }

            predicates = cb.and(predicates, cb.equal(root.get("card").get("cardId"), cardId));
            query.orderBy(cb.desc(root.get("transactionId"))); // Sort by transaction ID in descending order
            return predicates;
        };
    }

    private TransactionResponseDto mapToDto(Transaction transaction) {
        TransactionResponseDto dto = new TransactionResponseDto();
        dto.setTransactionId(transaction.getTransactionId());
        dto.setCardId(transaction.getCard().getCardId());
        dto.setExternalId(transaction.getExternalId());
        dto.setAmount(transaction.getAmount());
        dto.setAfterBalance(transaction.getCard().getBalance());
        dto.setCurrency(transaction.getCurrency());
        dto.setType(transaction.getType());
        return dto;
    }
}
