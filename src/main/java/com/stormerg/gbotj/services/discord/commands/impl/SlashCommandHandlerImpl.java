package com.stormerg.gbotj.services.discord.commands.impl;

import com.stormerg.gbotj.services.discord.commands.CustomSlashCommandData;
import com.stormerg.gbotj.services.discord.commands.SlashCommandHandler;
import com.stormerg.gbotj.services.discord.commands.CommandModule;
import com.stormerg.gbotj.services.discord.commands.exceptions.GBotDiscordException;
import com.stormerg.gbotj.services.discord.commands.exceptions.MessageNotSentFromGuild;
import com.stormerg.gbotj.services.discord.commands.exceptions.MessageNotSentFromPrivateMessage;
import com.stormerg.gbotj.services.discord.commands.models.CommandLog;
import com.stormerg.gbotj.services.discord.commands.helpers.InteractionHelper;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class SlashCommandHandlerImpl implements SlashCommandHandler {

    private final List<CommandModule> commandModules;

    @Autowired
    public SlashCommandHandlerImpl(final List<CommandModule> commandModules) {
        this.commandModules = commandModules;
    }

    public CustomSlashCommandData[] getSupportedSlashCommands() {
        return commandModules.stream()
                .flatMap(cm -> Arrays.stream(cm.getSupportedCommands()))
                .toArray(CustomSlashCommandData[]::new);
    }

    public Mono<Void> handleSlashCommand(final SlashCommandInteractionEvent event) {
        try {
            String executedCmdName = event.getName();

            // Find the appropriate CommandModule and call handleCommand on it
            Optional<CommandModule> matchingModule = commandModules.stream()
                    .filter(cm -> Arrays.stream(cm.getSupportedCommands())
                            .anyMatch(cmd -> cmd.getName().equalsIgnoreCase(executedCmdName)))
                    .findFirst();

            // If a matching module is found, call handleCommand
            if (matchingModule.isPresent()) {
                CommandModule module = matchingModule.get();
                CustomSlashCommandData command = Arrays.stream(module.getSupportedCommands())
                        .filter(cmd -> cmd.getName().equalsIgnoreCase(executedCmdName))
                        .findFirst()
                        .orElse(null);
                // Ensure command is not null, handle gracefully if not found
                if (command != null) {
                    CommandLog commandLog = validateCommandEvent(command, event);
                    logCommandEvent(module, commandLog);
                    return module.handleCommand(command, event);
                }
            }
        } catch (GBotDiscordException e) {
            // if it is a known exception, skip stack trace and return expected error message
            InteractionHelper.sendMessage(event, true, e.getMessage());
        } catch (Throwable e) {
            // if it is not a known exception, log stacktrace and return general error message
            log.error("Exception occurred during slash command interaction.", e);
            GBotDiscordException g = new GBotDiscordException();
            InteractionHelper.sendMessage(event, true, g.getMessage());
        }

        // If no matching module or command found, return an empty Mono
        return Mono.empty();
    }

    private void logCommandEvent(final CommandModule module, final CommandLog commandLog) {
        log.info("Forwarding slash command -> {}: {}", module.getClass().getName(), commandLog);
    }

    // TODO - also validate feature switch being on with module
    private CommandLog validateCommandEvent(final CustomSlashCommandData command,
                                            final SlashCommandInteractionEvent event) throws Exception {
        CommandLog commandLog = new CommandLog(command, event);
        log.info("Validating slash command: {}", commandLog);

        if (command.isGuildOnly() && !event.isFromGuild()) {
            throw new MessageNotSentFromGuild();
        }
        if (command.isPrivateMessageOnly() && event.isFromGuild()) {
            throw new MessageNotSentFromPrivateMessage();
        }
        return commandLog;
    }
}
