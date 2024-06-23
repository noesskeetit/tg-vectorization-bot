package ru.emil.tgvectorization.service;

import ru.emil.tgvectorization.model.TgUserChat;

public interface UserChatService {
    void addUserToChat(TgUserChat tgUserChat);
    void removeUserToChat(TgUserChat tgUserChat);
}
