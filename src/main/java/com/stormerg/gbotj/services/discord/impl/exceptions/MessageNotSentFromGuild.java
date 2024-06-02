package com.stormerg.gbotj.services.discord.impl.exceptions;

public class MessageNotSentFromGuild extends GBotDiscordException {

    public MessageNotSentFromGuild() {
        super("Sorry, this command needs to be executed in a server.");
    }
}
