package com.stormerg.gbotj.services.discord.commands.impl;

import com.stormerg.gbotj.services.discord.commands.CommandModule;
import com.stormerg.gbotj.services.discord.commands.SlashCommandHandler;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class SlashCommandHandlerImpl implements SlashCommandHandler {

    private final List<CommandModule> commandModules;

    public SlashCommandHandlerImpl(List<CommandModule> commandModules) {
        this.commandModules = commandModules;
    }

    public SlashCommandData[] getSupportedSlashCommands() {
        return commandModules.stream()
                .flatMap(cm -> Arrays.stream(cm.getSupportedCommands()))
                .toArray(SlashCommandData[]::new);
    }

    public void handleSlashCommand(SlashCommandInteractionEvent event) {
        String executedCmdName = event.getName();
        for (CommandModule cm: commandModules) {
            SlashCommandData[] supportedCommands = cm.getSupportedCommands();
            Optional<SlashCommandData> foundCommand = Arrays.stream(supportedCommands)
                    .filter(cmd -> cmd.getName().equalsIgnoreCase(executedCmdName))
                    .findFirst();
            foundCommand.ifPresent(command -> cm.handleCommand(command, event));
        }
    }
}
