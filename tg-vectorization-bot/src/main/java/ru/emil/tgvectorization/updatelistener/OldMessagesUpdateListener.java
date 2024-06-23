package ru.emil.tgvectorization.updatelistener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SetMyCommands;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class OldMessagesUpdateListener implements UpdatesListener {

    private final TelegramBot bot;

    @Override
    public int process(List<Update> updates) {
//        updates.forEach(update -> {
//            if (update )
//
//        });
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }
}
