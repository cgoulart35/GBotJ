package com.stormerg.gbotj.services.discord.commands.exceptions;

public class AuthorNotAdmin extends GBotDiscordException {

    public AuthorNotAdmin() {
        super("Sorry, only administrators can execute this command.");
    }
}
