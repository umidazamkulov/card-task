package com.example.card_task.service;

import com.example.card_task.dto.CardRequestDto;
import com.example.card_task.dto.CardResponseDto;
import com.example.card_task.exception.CardNotFoundException;
import com.example.card_task.model.Card;
import com.example.card_task.repository.CardRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class CardService {

    private final CardRepository cardRepository;

    public CardService(CardRepository cardRepository) {
        this.cardRepository = cardRepository;
    }

    public CardResponseDto createCard(CardRequestDto request) {
        long activeCardsCount = cardRepository.countByUserIdAndStatusIn(
                request.getUserId(),
                List.of(Card.CardStatus.ACTIVE, Card.CardStatus.BLOCKED)
        );

        if (activeCardsCount >= 3) {
            throw new IllegalStateException("User already has 3 non-closed cards");
        }

        Card card = new Card();
        card.setUserId(request.getUserId());
        card.setStatus(request.getStatus() != null ? request.getStatus() : Card.CardStatus.ACTIVE);
        card.setBalance(request.getInitialAmount() != null ? request.getInitialAmount() : 0L);
        card.setCurrency(request.getCurrency() != null ? request.getCurrency() : Card.Currency.UZS);

        Card savedCard = cardRepository.save(card);

        return mapToDto(savedCard);
    }

    public CardResponseDto getCard(UUID cardId, Long userId) {
        Card card = cardRepository.findByCardIdAndUserId(cardId, userId)
                .orElseThrow(() -> new CardNotFoundException("Card not found for the specified user and card ID"));
        return mapToDto(card);
    }

    public void blockCard(UUID cardId, Long userId, String eTag) {
        Card card = cardRepository.findByCardIdAndUserId(cardId, userId)
                .orElseThrow(() -> new CardNotFoundException("Card not found for the specified user and card ID"));

        if (card.getStatus() != Card.CardStatus.ACTIVE) {
            throw new IllegalStateException("Only active cards can be blocked");
        }

        card.setStatus(Card.CardStatus.BLOCKED);
        cardRepository.save(card);
    }

    public void unblockCard(UUID cardId, Long userId, String eTag) {
        Card card = cardRepository.findByCardIdAndUserId(cardId, userId)
                .orElseThrow(() -> new CardNotFoundException("Card not found for the specified user and card ID"));

        if (card.getStatus() != Card.CardStatus.BLOCKED) {
            throw new IllegalStateException("Only blocked cards can be unblocked");
        }

        card.setStatus(Card.CardStatus.ACTIVE);
        cardRepository.save(card);
    }

    public CardResponseDto getCardById(UUID cardId) {
        Card card = cardRepository.findByCardId(cardId)
                .orElseThrow(() -> new CardNotFoundException("Card not found with ID: " + cardId));
        return mapToDto(card);
    }

    private CardResponseDto mapToDto(Card card) {
        CardResponseDto dto = new CardResponseDto();
        dto.setCardId(card.getCardId());
        dto.setUserId(card.getUserId());
        dto.setStatus(card.getStatus());
        dto.setBalance(card.getBalance());
        dto.setCurrency(card.getCurrency());
        return dto;
    }
}
