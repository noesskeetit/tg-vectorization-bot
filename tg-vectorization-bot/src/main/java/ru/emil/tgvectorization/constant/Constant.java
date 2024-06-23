package ru.emil.tgvectorization.constant;

import com.pengrad.telegrambot.model.BotCommand;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Constant {
    public static int MESSAGES_BATCH_SIZE = 0;

    @Value("${ru.emil.messages.batch.size}")
    public void setMessagesBatchSize(int batch) {
        Constant.MESSAGES_BATCH_SIZE = batch;
    }

    @Getter
    public enum Command {
        LANGUAGE_COMMAND("/language","Swap language"),
        PRICE_LIST_COMMAND("/price_list", "Прайс-лист"),
        SWAP_ENCODER_COMMAND("/swap_encoder_model", "Сменить модель энкодера"),
        INVITE_USER_COMMAND("/invite_user", "Пригласить пользователя"),
        REFERAL_PROGRAMM_COMMAND("/referral_programm", "Реферальная программа"),
        SUBSCRIBITION_COMMAND("/subscribition", "Подписка"),
        SEARCH_COMMAND("/search", "Поиск по чату"),
        BALANCE_COMMAND("/balance", "Баланс"),
        FILTER_COMMAND("/filter", "Ограничить пространство поиска по дате"),
        PAYMENT_COMMAND("/payment", "Оплата"),
        SUPPORT_COMMAND("/support", "Поддержка"),
        ANALYZE_JSON_COMMAND("/analyze_json", "Загрузить диалог на анализ"),
        HELP_COMMAND("/language","Swap language"),
        START_COMMAND("/start", "Перезагрузить бота");

        private final String name;
        private final String description;
        private final BotCommand command;

        Command(String name, String description) {
            this.name = name;
            this.description = description;
            this.command = new BotCommand(name, description);
        }

        public static Command parseCommand(String name) {
            if (name.contains(" ")) {
                name = name.split(" ")[0];
            }
            for (Command command : Command.values()) {
                if (command.name.equals(name)) {
                    return command;
                }
            }
            return HELP_COMMAND;
        }
    }
}
