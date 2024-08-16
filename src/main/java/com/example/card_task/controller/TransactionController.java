package com.example.card_task.controller;

import com.example.card_task.dto.TransactionRequestDto;
import com.example.card_task.dto.TransactionResponseDto;
import com.example.card_task.model.Transaction;
import com.example.card_task.service.TransactionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/cards/{cardId}/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping("/debit")
    public ResponseEntity<TransactionResponseDto> debit(@PathVariable UUID cardId,
                                                        @RequestBody TransactionRequestDto request,
                                                        @RequestHeader("Idempotency-Key") String idempotencyKey) {
        TransactionResponseDto response = transactionService.debit(request, cardId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/credit")
    public ResponseEntity<TransactionResponseDto> credit(@PathVariable UUID cardId,
                                                         @RequestBody TransactionRequestDto request,
                                                         @RequestHeader("Idempotency-Key") String idempotencyKey) {
        TransactionResponseDto response = transactionService.credit(request, cardId);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<TransactionResponseDto>> getTransactionHistory(
            @PathVariable UUID cardId,
            @RequestParam(required = false) Transaction.TransactionType type,
            @RequestParam(required = false) String transaction_id,
            @RequestParam(required = false) String external_id,
            @RequestParam(required = false) Transaction.Currency currency,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        List<TransactionResponseDto> transactions = transactionService.getTransactionHistory(
                cardId, type, transaction_id, external_id, currency, page, size);
        return ResponseEntity.ok(transactions);
    }
}
