package com.stormerg.gbotj.services.discord.commands.impl;

import com.stormerg.gbotj.services.discord.commands.CustomSlashCommandData;
import com.stormerg.gbotj.services.discord.commands.SlashCommandHandler;
import com.stormerg.gbotj.services.discord.commands.CommandModule;
import com.stormerg.gbotj.services.discord.commands.exceptions.FeatureNotEnabledForGuild;
import com.stormerg.gbotj.services.discord.commands.exceptions.GBotDiscordException;
import com.stormerg.gbotj.services.discord.commands.exceptions.MessageNotSentFromGuild;
import com.stormerg.gbotj.services.discord.commands.exceptions.MessageNotSentFromPrivateMessage;
import com.stormerg.gbotj.services.discord.commands.models.CommandLog;
import com.stormerg.gbotj.services.discord.commands.helpers.InteractionHelper;
import com.stormerg.gbotj.services.firebase.business.ConfigQueries;
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
    private final ConfigQueries configQueries;

    @Autowired
    public SlashCommandHandlerImpl(final List<CommandModule> commandModules,
                                   final ConfigQueries configQueries) {
        this.commandModules = commandModules;
        this.configQueries = configQueries;
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
                    Mono<CommandLog> getCommandLog = validateCommandEvent(module, command, event);

                    Mono<Void> logCommandInfo = getCommandLog.doOnNext(commandLog ->
                            log.info("Forwarding slash command -> {}: {}", module.getClass().getName(), commandLog)).then();

                    Mono<Void> handleCommand = module.handleCommand(command, event);

                    return logCommandInfo
                            .then(handleCommand)
                            .onErrorResume(GBotDiscordException.class, error -> {
                                // if it is a known exception, skip stack trace and return expected error message
                                InteractionHelper.sendMessage(event, true, error.getMessage());
                                return Mono.empty();
                            })
                            .onErrorResume(Throwable.class, error -> {
                                // if it is not a known exception, log stacktrace and return general error message
                                handleUnknownException(event, error);
                                return Mono.empty();
                            });
                }
            }
        } catch (Throwable error) {
            // if it is not a known exception, log stacktrace and return general error message
            handleUnknownException(event, error);
        }

        // If no matching module or command found, return an empty Mono
        return Mono.empty();
    }

    private Mono<CommandLog> validateCommandEvent(final CommandModule module,
                                                  final CustomSlashCommandData command,
                                                  final SlashCommandInteractionEvent event) throws Exception {
        CommandLog commandLog = new CommandLog(command, event);
        log.info("Validating slash command: {}", commandLog);

        if (command.isGuildOnly() && !event.isFromGuild()) {
            throw new MessageNotSentFromGuild();
        }
        if (command.isPrivateMessageOnly() && event.isFromGuild()) {
            throw new MessageNotSentFromPrivateMessage();
        }

        if (event.isFromGuild() && module.getFeatureToggleName() != null) {
            Mono<Boolean> getFeatureToggle = configQueries.getFeatureToggleValue(event.getGuild().getId(), module.getFeatureToggleName());

            // Validate feature toggle asynchronously
            return getFeatureToggle.flatMap(featureToggle -> {
                if (!featureToggle) {
                    return Mono.error(new FeatureNotEnabledForGuild());
                }
                return Mono.just(commandLog); // No error, continue execution
            });
        }

        // Return the command log directly if no feature toggle validation is needed
        return Mono.just(commandLog);
    }

    private void handleUnknownException(final SlashCommandInteractionEvent event, final Throwable e) {
        log.error("Exception occurred during slash command interaction.", e);
        GBotDiscordException g = new GBotDiscordException();
        InteractionHelper.sendMessage(event, true, g.getMessage());
    }
}
