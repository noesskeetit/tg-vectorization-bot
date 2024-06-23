package ru.emil.tgvectorization.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.emil.tgvectorization.model.TgUserChat;

import java.util.List;
import java.util.Optional;

@Repository
public interface TgUserChatDao extends JpaRepository<TgUserChat, Long> {
    Optional<TgUserChat> findByChatIdAndUserId(Long chatId, Long userId);
    List<TgUserChat> findByUserId(Long userId);
}
