package ru.bot_hak.bot_tictac.bot.registration;

import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;

@Builder
@JsonNaming(SnakeCaseStrategy.class)
public record RegistrationRequest(
    String botUrl,
    String botId,
    String password
) {}
