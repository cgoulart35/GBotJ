package com.stormerg.gbotj.services.discord.commands.exceptions;

public class AuthorNoAccess extends GBotDiscordException {

    public AuthorNoAccess(final String patreonUrl) {
        super("Sorry, you do not have access. Please register your server in the support server, or join a registered server. " +
              "Patreon: " + patreonUrl);
    }
}
