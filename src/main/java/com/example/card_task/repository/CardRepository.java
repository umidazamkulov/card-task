package com.example.card_task.repository;

import com.example.card_task.model.Card;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CardRepository extends JpaRepository<Card, UUID> {

    List<Card> findByUserId(Long userId);

    Optional<Card> findByCardIdAndUserId(UUID cardId, Long userId);

    long countByUserIdAndStatusIn(Long userId, List<Card.CardStatus> statuses);

    Optional<Card> findByCardId(UUID cardId);
}
