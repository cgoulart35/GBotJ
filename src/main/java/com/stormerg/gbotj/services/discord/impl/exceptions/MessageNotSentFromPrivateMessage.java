package com.stormerg.gbotj.services.discord.impl.exceptions;

public class MessageNotSentFromPrivateMessage extends GBotDiscordException {

    public MessageNotSentFromPrivateMessage() {
        super("Sorry, this command needs to be executed in a private message.");
    }
}
