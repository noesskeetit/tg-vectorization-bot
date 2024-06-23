package ru.emil.tgvectorization.service;

public interface SearchVectorService {
    void searchAndSend(long userId, String input);
}
