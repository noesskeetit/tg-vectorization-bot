package ru.emil.tgvectorization.service.impl;

import com.pengrad.telegrambot.TelegramBot;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.emil.tgvectorization.service.MessageService;
import ru.emil.tgvectorization.service.TelegramBotUpdatesService;
import ru.emil.tgvectorization.service.UserChatService;
import ru.emil.tgvectorization.updatelistener.DefaultUpdateListener;

import javax.annotation.PostConstruct;

@Service
@RequiredArgsConstructor
@Slf4j
public class TelegramBotUpdatesServiceImpl implements TelegramBotUpdatesService {
    private final TelegramBot telegramBot;
    private final UserChatService userChatService;
    private final MessageService messageService;
    private final DefaultUpdateListener defaultUpdateListener;

    @PostConstruct
    @Override
    public void subscribe() {
        telegramBot.setUpdatesListener(defaultUpdateListener);
    }
}
