package ru.emil.tgvectorization.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.emil.tgvectorization.model.TgUserChat;
import ru.emil.tgvectorization.repository.TgUserChatDao;
import ru.emil.tgvectorization.service.UserChatService;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserChatServiceImpl implements UserChatService {
    private final TgUserChatDao tgUserChatDao;

    @Override
    public void addUserToChat(TgUserChat tgUserChat) {
        Optional<TgUserChat> optional = tgUserChatDao.findByChatIdAndUserId(tgUserChat.getChatId(), tgUserChat.getUserId());
        if (optional.isEmpty()) {
            tgUserChatDao.save(tgUserChat);
        }
    }

    @Override
    public void removeUserToChat(TgUserChat tgUserChat) {
        Optional<TgUserChat> optional = tgUserChatDao.findByChatIdAndUserId(tgUserChat.getChatId(), tgUserChat.getUserId());
        optional.ifPresent(tgUserChatDao::delete);
    }
}
