package com.stormerg.gbotj.services.discord.commands;

import com.stormerg.gbotj.services.discord.commands.models.CustomSlashCommandData;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import reactor.core.publisher.Mono;

public interface CommandModule {

    Mono<Void> handleCommand(final CustomSlashCommandData command, final SlashCommandInteractionEvent event);
    CustomSlashCommandData[] getSupportedCommands();
    String getFeatureToggleName();
    void setJda(final JDA jda);

}
