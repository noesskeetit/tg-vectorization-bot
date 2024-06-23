package ru.emil.tgvectorization.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.emil.tgvectorization.model.TgMessage;
import ru.emil.tgvectorization.repository.TgMessageDao;
import ru.emil.tgvectorization.service.MessageService;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static ru.emil.tgvectorization.config.RabbitMqConfig.INBOUND_RK;
import static ru.emil.tgvectorization.config.RabbitMqConfig.TG_EXCHANGE;
import static ru.emil.tgvectorization.constant.Constant.MESSAGES_BATCH_SIZE;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageServiceImpl implements MessageService {
    private final TgMessageDao tgMessageDao;
    private final RabbitTemplate rabbitTemplate;
    @Value("${ru.emil.big-text-word}")
    private Integer bigTextWord;
    @Value("${ru.emil.lil-text-word}")
    private Integer lilTextWord;
    private final Integer batchSize = 100;
    @Override
    public void processNewMessage(TgMessage tgMessage) {
        TgMessage savedTgMessage = tgMessageDao.saveAndFlush(tgMessage);
        tgMessageDao.flush();

        if (getWordCount(tgMessage.getMessage()) >= bigTextWord) {
            this.preprocessBig(tgMessage).forEach(map -> {
                rabbitTemplate.convertAndSend(TG_EXCHANGE, INBOUND_RK, map);
                log.info("big send: " + tgMessage.getMessageIdTg() + " " + map.get("message"));
            });
        } else if (getWordCount(tgMessage.getMessage()) <= lilTextWord) {
            List<TgMessage> list = tgMessageDao.findBatchMessagesByMessageId(savedTgMessage.getMessageId(), MESSAGES_BATCH_SIZE);
            if (list.size() == MESSAGES_BATCH_SIZE) {
                Map<String, Object> map = this.preprocessLil(list);
                rabbitTemplate.convertAndSend(TG_EXCHANGE, INBOUND_RK, map);
                log.info("lil send: " + tgMessage.getMessageIdTg() + " " + Arrays.toString(list.stream().map(TgMessage::getMessageIdTg).toArray()));
            }
        } else {
            rabbitTemplate.convertAndSend(TG_EXCHANGE, INBOUND_RK, this.preprocessMedium(tgMessage));
            log.info("medium send: " + tgMessage.getMessageIdTg() + " " + tgMessage.getMessage());
        }


    }


    private Map<String, Object> preprocessLil(List<TgMessage> list) {
        TgMessage firstMessage = list.get(0);
        Map<String, Object> map = new HashMap<>();
        AtomicReference<StringBuilder> stringBuilder = new AtomicReference<>(new StringBuilder());
        Collections.reverse(list);
        list.forEach(tgMessage -> {
            if (getWordCount(tgMessage.getMessage()) >= bigTextWord) {
                stringBuilder.set(new StringBuilder());
            } else if (getWordCount(tgMessage.getMessage()) > lilTextWord) {
                stringBuilder.set(new StringBuilder());
                stringBuilder.get().append(tgMessage.getMessage());
                stringBuilder.get().append("\n");
            } else {
                stringBuilder.get().append(tgMessage.getMessage());
                stringBuilder.get().append("\n");
            }
        });
        map.put("message", stringBuilder.get().toString().trim());
        map.put("chat_id", firstMessage.getChatId());
        map.put("message_id", firstMessage.getMessageIdTg());
        return map;
    }
    public Map<String, Object> preprocessMedium(TgMessage tgMessage) {
        Map<String, Object> map = new HashMap<>();
        map.put("message", tgMessage.getMessage().trim());
        map.put("chat_id", tgMessage.getChatId());
        map.put("message_id", tgMessage.getMessageIdTg());
        return map;
    }

    public List<Map<String, Object>> preprocessBig(TgMessage tgMessage) {
        List<String> messages = new ArrayList<>();
        for (int i = 0; i < tgMessage.getMessage().length() / batchSize; i++) {
            if (i + 1 == tgMessage.getMessage().length() / batchSize) {
                messages.add(tgMessage.getMessage().substring(i * batchSize));

            } else {
                messages.add(tgMessage.getMessage().substring(i * batchSize, (i + 1) * batchSize));
            }
        }

        for (int i = 0; i < tgMessage.getMessage().length()  / batchSize - 1; i++) {
            if (i + 2 == tgMessage.getMessage().length()  / batchSize) {
                messages.add(tgMessage.getMessage().substring(i * batchSize + batchSize/2));
            } else {
                messages.add(tgMessage.getMessage().substring(i * batchSize + batchSize/2, (i + 1) * batchSize + batchSize/2));
            }
        }

        return messages.stream().map(m -> {
            Map<String, Object> map = new HashMap<>();
            map.put("message", m);
            map.put("chat_id", tgMessage.getChatId());
            map.put("message_id", tgMessage.getMessageIdTg());
            return map;
        }).collect(Collectors.toList());
    }
    private static int getWordCount(String str) {
        String[] words = str.split("\\s+");
        return words.length;
    }
}
