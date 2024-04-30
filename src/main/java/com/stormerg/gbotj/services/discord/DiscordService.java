package com.stormerg.gbotj.services.discord;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public interface DiscordService {

    void init();
    void onMessageReceived(final MessageReceivedEvent event);
    void onSlashCommandInteraction(final SlashCommandInteractionEvent event);

}
