package com.stormerg.gbotj.services.discord.commands.exceptions;

public class MessageNotSentFromPatreonGuild extends GBotDiscordException {

    public MessageNotSentFromPatreonGuild() {
        super("Sorry, this command needs to be executed in the support server.");
    }
}
