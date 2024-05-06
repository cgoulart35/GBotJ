package com.stormerg.gbotj.services.discord.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import reactor.core.publisher.Mono;

public interface SlashCommandHandler {

    CustomSlashCommandData[] getSupportedSlashCommands();
    Mono<Void> handleSlashCommand(final SlashCommandInteractionEvent event);

}
