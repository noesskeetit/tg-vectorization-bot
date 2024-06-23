package ru.emil.tgvectorization.service;

import ru.emil.tgvectorization.model.TgMessage;

public interface MessageService {
    void processNewMessage(TgMessage tgMessage);
}
