package ru.bot_hak.bot_tictac.bot.registration.impl;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import ru.bot_hak.bot_tictac.bot.Figure;
import ru.bot_hak.bot_tictac.bot.registration.BotRegistrationService;
import ru.bot_hak.bot_tictac.bot.registration.RegistrationRequest;
import ru.bot_hak.bot_tictac.bot.registration.RegistrationResponse;
import ru.bot_hak.bot_tictac.bot.registration.exception.BotRegistrationException;
import ru.bot_hak.bot_tictac.config.BotConfig;


import java.util.Map;
import java.util.UUID;

import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpStatus.OK;

@RequiredArgsConstructor
@Slf4j
@Service
public class BotRegistrationServiceImpl implements BotRegistrationService {

    private final RestTemplate restTemplate;
    private final BotConfig botConfig;
    @Getter
    private Figure figure;

    @PostConstruct
    public void init() {
        String botUrl = botConfig.botUrl();
        UUID sessionUUID = botConfig.sessionUUID();
        log.info("Попытка зарегистрировать бота it_ri_ppers с url {} в сессии {}", botUrl, sessionUUID);
        log.debug("Отправляем запрос для регистрации бота в сессии");
        final var uri = UriComponentsBuilder
            .fromUriString("{basePath}/sessions/{sessionId}/registration")
            .buildAndExpand(Map.of(
                "basePath", botConfig.mediatorAddress(),
                "sessionId", sessionUUID
            ))
            .encode()
            .toUri();
        final var body = RegistrationRequest.builder()
            .botUrl(botUrl)
            .botId("it_ri_ppers")
            .password("ffN@a'Vx")
            .build();
        ResponseEntity<RegistrationResponse> response = restTemplate.exchange(
            uri,
            POST,
            new HttpEntity<>(body),
            RegistrationResponse.class
        );
        log.debug("Ответ получен {}", response);
        if (response.getStatusCode() != OK) {
            throw new BotRegistrationException();
        }
        this.figure = response.getBody().figure();
        log.debug("Бот успешно зарегистрирован в сессии {}", sessionUUID);
        log.info("Успешно зарегистрирован. Буду ходить фигурой {}", figure);
    }

}
