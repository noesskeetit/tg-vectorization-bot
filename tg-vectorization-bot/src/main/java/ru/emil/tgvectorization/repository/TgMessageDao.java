package ru.emil.tgvectorization.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.emil.tgvectorization.model.TgMessage;

import java.util.List;

@Repository
public interface TgMessageDao extends JpaRepository<TgMessage, Long> {
    @Query(value =
            "select * " +
            "from base.message m " +
            "where m.date <= (select m2.date from base.message m2 where m2.message_id = :message_id) " +
            "and m.chat_id = (select m2.chat_id from base.message m2 where m2.message_id = :message_id) " +
            "order by m.message_id_tg desc " +
            "limit :limit", nativeQuery = true)
    List<TgMessage> findBatchMessagesByMessageId(@Param("message_id") Long messageId, @Param("limit") int limit);
}
