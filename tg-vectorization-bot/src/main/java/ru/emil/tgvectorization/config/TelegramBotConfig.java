package ru.emil.tgvectorization.config;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.BotCommand;
import com.pengrad.telegrambot.request.SetMyCommands;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.emil.tgvectorization.constant.Constant;

import java.util.concurrent.TimeUnit;

@Configuration
public class TelegramBotConfig {

    @Value("${ru.emil.bot.tg.token}")
    private String token;

    @Bean
    public TelegramBot generateTelegramBot() {
        TelegramBot telegramBot = new TelegramBot.Builder(token).okHttpClient(
                new OkHttpClient.Builder().
                        callTimeout(5, TimeUnit.MINUTES)
                        .connectTimeout(5, TimeUnit.MINUTES)
                        .readTimeout(5, TimeUnit.MINUTES)
                        .writeTimeout(5, TimeUnit.MINUTES).build()
        ).build();

        BotCommand[] botCommands = new BotCommand[Constant.Command.values().length];
        for (int i = 0; i < botCommands.length; i++) {
            botCommands[i] = Constant.Command.values()[i].getCommand();
        }

        telegramBot.execute(new SetMyCommands(botCommands));
        return telegramBot;
    }
}
