package com.example.card_task.controller;

import com.example.card_task.dto.CardRequestDto;
import com.example.card_task.dto.CardResponseDto;
import com.example.card_task.service.CardService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/cards")
public class CardController {

    private final CardService cardService;

    public CardController(CardService cardService) {
        this.cardService = cardService;
    }

    @PostMapping
    public ResponseEntity<CardResponseDto> createCard(@RequestBody CardRequestDto request,
                                                      @RequestHeader("Idempotency-Key") String idempotencyKey) {
        CardResponseDto response = cardService.createCard(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{cardId}")
    public ResponseEntity<CardResponseDto> getCard(@PathVariable UUID cardId,
                                                   @RequestParam("userId") Long userId) {
        CardResponseDto response = cardService.getCard(cardId, userId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{cardId}/block")
    public ResponseEntity<Void> blockCard(@PathVariable UUID cardId,
                                          @RequestParam("userId") Long userId,
                                          @RequestHeader("If-Match") String eTag) {
        cardService.blockCard(cardId, userId, eTag);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{cardId}/unblock")
    public ResponseEntity<Void> unblockCard(@PathVariable UUID cardId,
                                            @RequestParam("userId") Long userId,
                                            @RequestHeader("If-Match") String eTag) {
        cardService.unblockCard(cardId, userId, eTag);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/by-id/{cardId}")
    public ResponseEntity<CardResponseDto> getCardById(@PathVariable UUID cardId) {
        CardResponseDto response = cardService.getCardById(cardId);
        return ResponseEntity.ok(response);
    }
}
