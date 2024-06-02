package com.stormerg.gbotj.services.discord.impl.exceptions;

public class FeatureNotEnabledForGuild extends GBotDiscordException {

    public FeatureNotEnabledForGuild() {
        super("Sorry, this feature is not enabled in this server.");
    }
}