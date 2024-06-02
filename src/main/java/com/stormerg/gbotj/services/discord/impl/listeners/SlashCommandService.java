package com.stormerg.gbotj.services.discord.impl.listeners;

import com.stormerg.gbotj.services.discord.impl.exceptions.FeatureNotEnabledForGuild;
import com.stormerg.gbotj.services.discord.impl.exceptions.GBotDiscordException;
import com.stormerg.gbotj.services.discord.impl.exceptions.MessageNotSentFromGuild;
import com.stormerg.gbotj.services.discord.impl.exceptions.MessageNotSentFromPrivateMessage;
import com.stormerg.gbotj.services.discord.impl.models.CommandLog;
import com.stormerg.gbotj.services.discord.impl.helpers.InteractionHelper;
import com.stormerg.gbotj.services.discord.impl.models.CustomSlashCommandData;
import com.stormerg.gbotj.services.discord.impl.commands.AbstractCommandModule;
import com.stormerg.gbotj.services.firebase.impl.business.ConfigQueries;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class SlashCommandService {

    private final List<AbstractCommandModule> commandModules;
    private final ConfigQueries configQueries;

    @Autowired
    public SlashCommandService(final List<AbstractCommandModule> commandModules,
                               final ConfigQueries configQueries) {
        this.commandModules = commandModules;
        this.configQueries = configQueries;
    }

    public CustomSlashCommandData[] getSupportedSlashCommands() {
        return commandModules.stream()
                .flatMap(cm -> Arrays.stream(cm.getSupportedCommands()))
                .toArray(CustomSlashCommandData[]::new);
    }

    public void setJda(final JDA jda) {
        commandModules.forEach(commandModule -> {
            commandModule.setJda(jda);
        });
    }

    public Mono<Void> handleSlashCommand(final SlashCommandInteractionEvent event) {
        try {
            String executedCmdName = event.getName();

            // Find the appropriate CommandModule and call handleCommand on it
            Optional<AbstractCommandModule> matchingModule = commandModules.stream()
                    .filter(cm -> Arrays.stream(cm.getSupportedCommands())
                            .anyMatch(cmd -> cmd.getName().equalsIgnoreCase(executedCmdName)))
                    .findFirst();

            // If a matching module is found, call handleCommand
            if (matchingModule.isPresent()) {
                AbstractCommandModule module = matchingModule.get();
                CustomSlashCommandData command = Arrays.stream(module.getSupportedCommands())
                        .filter(cmd -> cmd.getName().equalsIgnoreCase(executedCmdName))
                        .findFirst()
                        .orElse(null);
                // Ensure command is not null, handle gracefully if not found
                if (command != null) {
                    Mono<CommandLog> getCommandLog = validateCommandEvent(module, command, event);
                    return getCommandLog.flatMap(commandLog ->
                            Mono.fromRunnable(() -> {
                                log.info("Forwarding slash command -> {}: {}", module.getClass().getName(), commandLog);
                            })
                            .then(module.handleCommand(command, event)
                                .doOnTerminate(() -> log.info("Slash command completed successfully: {}", commandLog)))
                            .onErrorResume(GBotDiscordException.class, error -> {
                                // if it is a known exception, log stacktrace and return expected error message
                                handleKnownException(event, error);
                                return Mono.empty();
                            })
                            .onErrorResume(Throwable.class, error -> {
                                // if it is not a known exception, log stacktrace and return general error message
                                handleUnknownException(event, error);
                                return Mono.empty();
                            })
                    );
                }
            }
        } catch (GBotDiscordException error) {
            // if it is a known exception, log stacktrace and return expected error message
            handleKnownException(event, error);
        } catch (Throwable error) {
            // if it is not a known exception, log stacktrace and return general error message
            handleUnknownException(event, error);
        }

        // If no matching module or command found, return an empty Mono
        return Mono.empty();
    }

    private Mono<CommandLog> validateCommandEvent(final AbstractCommandModule module,
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

    private void handleKnownException(final SlashCommandInteractionEvent event, final GBotDiscordException error) {
        log.info("Known exception occurred during slash command interaction.", error);
        InteractionHelper.sendMessage(event, true, error.getMessage());
    }

    private void handleUnknownException(final SlashCommandInteractionEvent event, final Throwable error) {
        log.error("Exception occurred during slash command interaction.", error);
        GBotDiscordException g = new GBotDiscordException();
        InteractionHelper.sendMessage(event, true, g.getMessage());
    }
}
