package com.stormerg.gbotj.services.discord.commands.impl;

import com.stormerg.gbotj.services.discord.commands.SlashCommandHandler;
import com.stormerg.gbotj.services.discord.commands.CommandModule;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class SlashCommandHandlerImpl implements SlashCommandHandler {

    private final List<CommandModule> commandModules;

    @Autowired
    public SlashCommandHandlerImpl(List<CommandModule> commandModules) {
        this.commandModules = commandModules;
    }

    public SlashCommandData[] getSupportedSlashCommands() {
        return commandModules.stream()
                .flatMap(cm -> Arrays.stream(cm.getSupportedCommands()))
                .toArray(SlashCommandData[]::new);
    }

    public Mono<Void> handleSlashCommand(SlashCommandInteractionEvent event) {
        String executedCmdName = event.getName();

        // Find the appropriate CommandModule and call handleCommand on it
        Optional<CommandModule> matchingModule = commandModules.stream()
                .filter(cm -> Arrays.stream(cm.getSupportedCommands())
                        .anyMatch(cmd -> cmd.getName().equalsIgnoreCase(executedCmdName)))
                .findFirst();

        // If a matching module is found, call handleCommand
        if (matchingModule.isPresent()) {
            CommandModule module = matchingModule.get();
            SlashCommandData command = Arrays.stream(module.getSupportedCommands())
                    .filter(cmd -> cmd.getName().equalsIgnoreCase(executedCmdName))
                    .findFirst()
                    .orElse(null); // Ensure command is not null, handle gracefully if not found
            if (command != null) {
                return module.handleCommand(command, event);
            }
        }

        // If no matching module or command found, return an empty Mono
        return Mono.empty();
    }
}
