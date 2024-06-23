package ru.emil.tgvectorization.updatelistener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.*;
import com.pengrad.telegrambot.request.AnswerPreCheckoutQuery;
import com.pengrad.telegrambot.request.AnswerShippingQuery;
import com.pengrad.telegrambot.request.SendInvoice;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.BaseResponse;
import com.pengrad.telegrambot.response.SendResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.emil.tgvectorization.constant.Constant;
import ru.emil.tgvectorization.model.TgMessage;
import ru.emil.tgvectorization.model.TgUserChat;
import ru.emil.tgvectorization.service.MessageService;
import ru.emil.tgvectorization.service.SearchVectorService;
import ru.emil.tgvectorization.service.UserChatService;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static ru.emil.tgvectorization.constant.Constant.Command.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class DefaultUpdateListener implements UpdatesListener {
    private final TelegramBot telegramBot;
    private final UserChatService userChatService;
    private final MessageService messageService;
    private final OldMessagesUpdateListener oldMessagesUpdateListener;
    private final SearchVectorService searchVectorService;

    @Override
    public int process(List<Update> updates) {
        updates.forEach(
                update -> {
                    try {
                        if (update.myChatMember() != null) {
                            //добавили в чат
                            log.info(update.myChatMember().toString());
                        } else if (update.message() != null) {
                            if (update.message().text() != null && update.message().text().length() > 0 && update.message().text().charAt(0) != '/') {
                                //получение сообщения (ЧАТЫ И ГРУППЫ)
                                Long chatId = update.message().chat().id();
                                if (update.message().chat().type().equals(Chat.Type.Private)) {
                                    searchVectorService.searchAndSend(update.message().chat().id(), update.message().text());
                                } else if (update.message().newChatMembers() != null && update.message().newChatMembers().length != 0) {
                                    Arrays.asList(update.message().newChatMembers()).forEach(newMember -> {
                                        TgUserChat tgUserChat = new TgUserChat();
                                        tgUserChat.setChatId(chatId);
                                        tgUserChat.setUserId(newMember.id());
                                        userChatService.addUserToChat(tgUserChat);
                                    });
                                } else if (update.message().leftChatMember() != null) {
                                    Collections.singletonList(update.message().leftChatMember()).forEach(newMember -> {
                                        TgUserChat tgUserChat = new TgUserChat();
                                        tgUserChat.setChatId(chatId);
                                        tgUserChat.setUserId(newMember.id());
                                        userChatService.removeUserToChat(tgUserChat);
                                    });
                                } else if (update.message().caption() != null || update.message().text() != null) {

                                    TgUserChat tgUserChat = new TgUserChat();
                                    tgUserChat.setChatId(chatId);
                                    tgUserChat.setUserId(update.message().from().id());
                                    userChatService.addUserToChat(tgUserChat);

                                    TgMessage tgMessage = new TgMessage();
                                    tgMessage.setMessageIdTg(Long.valueOf(update.message().messageId()));
                                    tgMessage.setChatId(chatId);
                                    tgMessage.setUserId(update.message().from().id());
                                    tgMessage.setDate(OffsetDateTime.ofInstant(
                                            Instant.ofEpochSecond(update.message().date()),
                                            ZoneId.systemDefault())
                                    );
                                    tgMessage.setMessage(
                                            update.message().caption() != null ? update.message().caption() : update.message().text()
                                    );
                                    messageService.processNewMessage(tgMessage);
                                }
                            }  else if (update.message().successfulPayment() != null) {
                                SendMessage sendMessage = new SendMessage(update.message().chat().id(),
                                        "Вам успешно начислено " + update.message().successfulPayment().totalAmount()/100 + " бибизьянкоинов \uD83D\uDE48"
                                );
                                telegramBot.execute(sendMessage);
                            } else {
                                String message = update.message().text();
                                Constant.Command command = Constant.Command.parseCommand(message);
                                SendMessage sendMessage = null;
                                switch (command) {
                                    case HELP_COMMAND:
                                        sendMessage = new SendMessage(update.message().chat().id(),
                                                "\uD83C\uDF89 Друзья! Добро пожаловать в нашего бота! Наверное, вы уже спросили себя: \"Что этот замечательный бот может для меня сделать?\"\n" +
                                                        "\n" +
                                                        "1️⃣ Добавьте бота к себе в группу или канал. Это проще простого! \uD83D\uDE0A\n" +
                                                        "\n" +
                                                        "2️⃣ После добавления, наш бот будет следить за всем, что публикуют участники группы или канала. Это как ваш секретный агент - всегда на связи! \uD83D\uDD75️\u200D♂️\n" +
                                                        "\n" +
                                                        "3️⃣ Если вам нужно найти что-то среди огромного потока сообщений, просто напишите боту примерный смысл того, что вам нужно.\n" +
                                                        "Например, \"Помоги мне найти мемы про котиков!\" И бот волшебным образом подберет для вас 5 самых подходящий по смыслу сообщений по этой теме! ✨\n" +
                                                        "\n" +
                                                        "Вот и все! Просто и эффективно! Приятного использования! \uD83D\uDE80"
                                        );

                                        break;
                                    case PAYMENT_COMMAND:
                                        SendInvoice sendInvoice = new SendInvoice(update.message().chat().id(), "Покупка  бибизьянкоинов \uD83D\uDE48", "\uD83D\uDCBC Напоминаем что цена одного поиска: 1 бибизьянкоин \uD83D\uDE48", "my_payload",
                                                "381764678:TEST:83200", "my_start_param", "RUB",
                                                new LabeledPrice("100 бибизьянкоинов \uD83D\uDE48", 10000)
                                        )
                                                .needPhoneNumber(false)
                                                .needShippingAddress(false)
                                                .isFlexible(false)
                                                .replyMarkup(
                                                        new InlineKeyboardMarkup(
                                                                new InlineKeyboardButton("Оплатить").pay()
                                                        )
                                                );
                                        SendResponse response = telegramBot.execute(sendInvoice);
                                        log.info("pay response: " + response);
                                        break;
                                    case START_COMMAND:
                                        sendMessage = new SendMessage(update.message().chat().id(),
                                                "Привет! \uD83C\uDF89 \n" +
                                                        "\n" +
                                                        "Мы улучшаем поиск в Telegram, чтобы вы быстрее находили нужную ВАМ информацию!\uD83D\uDCA1 \n" +
                                                        "\n" +
                                                        "Мы гарантируем безопасность ваших данных. \n" +
                                                        "Присоединяйтесь к нам и делайте поиск легким и увлекательным! \uD83D\uDE0A\uD83D\uDE80"
                                        );
                                        break;
                                    case LANGUAGE_COMMAND:
                                        Keyboard replyKeyboardMarkup = new ReplyKeyboardMarkup(
                                                new String[]{"English", "Russian", "Ukrainian"},
                                                new String[]{"German", "French", "Spanish"})
                                                .oneTimeKeyboard(true)   // optional
                                                .resizeKeyboard(true)    // optional
                                                .selective(true);        // optional
                                        sendMessage = new SendMessage(update.message().chat().id(), "Avalible languages:").replyMarkup(replyKeyboardMarkup);
                                        break;
                                    case PRICE_LIST_COMMAND:
                                        sendMessage = new SendMessage(update.message().chat().id(),
                                                "Тариф 1:\n" +
                                                "Стоимость: 50 Бибизьянкоинов \uD83D\uDE48\n" +
                                                "Запросы-поиска: 100\n" +
                                                "Тариф 2:\n" +
                                                "Стоимость: 100 Бибизьянкоинов \uD83D\uDE48\n" +
                                                "Запросы-поиска: 250\n" +
                                                "Тариф 3:\n" +
                                                "Стоимость: 150 Бибизьянкоинов \uD83D\uDE48\n" +
                                                "Запросы-поиска: 400\n" +
                                                "Тариф 4:\n" +
                                                "Стоимость: 5000 Бибизьянкоинов \uD83D\uDE48\n" +
                                                "Запросы-поиска: Безлимит\n");
                                        break;
                                    case BALANCE_COMMAND:
                                        sendMessage = new SendMessage(update.message().chat().id(),
                                                "Ваш текущий баланс: 100 Бибизянкоинов \uD83D\uDE48");
                                        break;
                                    case INVITE_USER_COMMAND:
                                        sendMessage = new SendMessage(update.message().chat().id(),
                                                "Вот ваша личная ссылка для приглашения друзей: https://t.me/bibizy_bot?start=" + update.message().chat().id());
                                        break;
                                    case SWAP_ENCODER_COMMAND:
                                        sendMessage = new SendMessage(update.message().chat().id(),
                                                "Выберите модель энкодера:\n" +
                                                        "1. sentence-transformers/all-MiniLM-L6-v2\n" +
                                                        "2. BAAI/bge-m3\n" +
                                                        "3. intfloat/e5-large-v2\n\n" +
                                                        "Вы выбрали модель энкодера: sentence-transformers/all-MiniLM-L6-v2.\n\n" +
                                                        "Изменения сохранены.");
                                        break;
                                    case SUPPORT_COMMAND:
                                        sendMessage = new SendMessage(update.message().chat().id(),
                                                "Если у вас возникли вопросы или проблемы, свяжитесь с нами по следующим контактным данным:\n" +
                                                    "Email: support@example.com\n" +
                                                    "Телефон: +123456789\n"
                                        );
                                        break;
                                    case SEARCH_COMMAND:
                                        searchVectorService.searchAndSend(update.message().chat().id(), update.message().text());
                                        break;
                                    case FILTER_COMMAND:
                                        sendMessage = new SendMessage(update.message().chat().id(),
                                                    "Фильтр установлен по дате: от 2023-01-01 до 2023-12-31"
                                        );
                                        break;
                                    default:
                                        sendMessage = new SendMessage(update.message().chat().id(), "Я не понимаю эту команду");
                                        break;
                                }
                                if (sendMessage != null) {
                                    telegramBot.execute(sendMessage);
                                }
                            }

                            log.info(update.message().toString());
                        } else if (update.channelPost() != null) {
                            //получение поста (КАНАЛЫ)

                            TgMessage tgMessage = new TgMessage();
                            tgMessage.setMessageIdTg(Long.valueOf(update.channelPost().messageId()));
                            tgMessage.setChatId(update.channelPost().chat().id());
                            tgMessage.setUserId(null);
                            tgMessage.setDate(OffsetDateTime.ofInstant(
                                    Instant.ofEpochSecond(update.channelPost().date()),
                                    ZoneId.systemDefault())
                            );
                            tgMessage.setMessage(
                                    update.channelPost().caption() != null ? update.channelPost().caption() : update.channelPost().text()
                            );
                            messageService.processNewMessage(tgMessage);
                            log.info(
                                    update.channelPost().chat().title() + " " +
                                            update.channelPost().chat().id().toString()
                            );
                        } else if (update.chatMember() != null) {
                            //добавление-удаление пользователя из чата или канала
//                                update.chatMember().
                            log.info(update.chatMember().toString());
                        } else if (update.editedMessage() != null) {
                            log.info(update.editedMessage().toString());
                        } else if (update.editedChannelPost() != null) {
                            log.info(update.editedChannelPost().toString());
                        } else if (update.shippingQuery() != null) {
                            log.info(update.shippingQuery().toString());
                            AnswerShippingQuery answerShippingQuery = new AnswerShippingQuery(update.shippingQuery().id(),
                                    new ShippingOption("1", "FREE", new LabeledPrice("Хорошее настроение", 0))
                            );
                            BaseResponse response = telegramBot.execute(answerShippingQuery);
                            log.info("answerShippingQuery response: " + response);
                        } else if (update.preCheckoutQuery() != null) {
                            log.info("preCheckoutQuery {}", update.preCheckoutQuery().toString());
                            AnswerPreCheckoutQuery answerCheckout = new AnswerPreCheckoutQuery(update.preCheckoutQuery().id());
                            BaseResponse response = telegramBot.execute(answerCheckout);
                            log.info("preCheckoutQuery response: " + response);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
        );
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }
}
