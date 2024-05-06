package com.stormerg.gbotj.services.discord.commands.exceptions;

public class GBotDiscordException extends Exception {

    public GBotDiscordException() {
        super("An error occurred. Please inform your GBot administrator.");
    }

    public GBotDiscordException(String message) {
        super(message);
    }
}
