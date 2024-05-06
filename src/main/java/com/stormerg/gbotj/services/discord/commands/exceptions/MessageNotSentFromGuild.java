package com.stormerg.gbotj.services.discord.commands.exceptions;

public class MessageNotSentFromGuild extends GBotDiscordException {

    public MessageNotSentFromGuild() {
        super("Sorry, this command needs to be executed in a server.");
    }
}
