package com.stormerg.gbotj.services.discord.commands.exceptions;

public class AuthorNotSubscriber extends GBotDiscordException {

    public AuthorNotSubscriber(final String patreonUrl) {
        super("Sorry, you need to be a subscriber to execute this command. " +
              "Make sure your Discord and subscription accounts are linked to get the required role in the support server. " +
              "Patreon: " + patreonUrl);
    }
}
