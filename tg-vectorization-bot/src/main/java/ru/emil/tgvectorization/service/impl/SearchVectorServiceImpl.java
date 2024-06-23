package ru.emil.tgvectorization.service.impl;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.emil.tgvectorization.model.TgUserChat;
import ru.emil.tgvectorization.repository.TgUserChatDao;
import ru.emil.tgvectorization.service.SearchVectorService;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class SearchVectorServiceImpl implements SearchVectorService {
    private final TelegramBot telegramBot;
    private final RestTemplate restTemplate = new RestTemplate();
    private final TgUserChatDao tgUserChatDao;
    @Value("${ru.emil.vector-service.url}")
    String url;


    @Override
    public void searchAndSend(long userId, String input) {
        if (input.contains("/")) {
            input = input.substring(input.indexOf("/")+1);
        }


        String answer = "ERROR";
        SendMessage sendMessage;
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HashMap<String, Object> params = new HashMap<>();
            List<Long> chatIds = tgUserChatDao.findByUserId(userId).stream().map(TgUserChat::getChatId).collect(Collectors.toList());
            if (chatIds.isEmpty()) {
                sendMessage = new SendMessage(userId, "Вы не состоите ни в одном общем чате");
            } else {
                params.put("list_chat_id", chatIds);
                params.put("message", input);
                ResponseEntity<HashMap[]> responseEntity = restTemplate.postForEntity(new URI(url), params, HashMap[].class);
                HashMap[] response = responseEntity.getBody();


                if (Objects.nonNull(response)) {
                    answer = "";
                    for (HashMap map: response) {
                        String responseChatId = map.get("chatId").toString();
                        String responseMessageId = map.get("messageId").toString();
//                        String messages = map.get("messages").toString();
                        answer += "https://t.me/c/"+ responseChatId.substring(4) + "/" + responseMessageId;
                        answer += "\n";
                    }
                }

                sendMessage = new SendMessage(userId, answer.trim());
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendMessage = new SendMessage(userId, "ERROR");
        }
        telegramBot.execute(sendMessage);
    }
}
