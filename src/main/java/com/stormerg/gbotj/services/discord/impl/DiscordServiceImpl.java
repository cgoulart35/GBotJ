package com.stormerg.gbotj.services.discord.impl;

import com.stormerg.gbotj.services.discord.DiscordService;
import com.stormerg.gbotj.services.discord.commands.SlashCommandHandler;
import com.stormerg.gbotj.services.firebase.FirebaseService;
import com.stormerg.gbotj.services.logging.LoggingService;
import com.stormerg.gbotj.services.properties.PropertiesManager;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.stream.Collectors;

@Service
public class DiscordServiceImpl extends ListenerAdapter implements DiscordService {

    private final String DISCORD_TOKEN;
    private final String[] SLASH_COMMAND_TEST_GUILDS;
    private final SlashCommandData[] REGISTERED_SLASH_COMMANDS;

    private final Logger LOGGER;
    private final FirebaseService firebaseService;
    private final SlashCommandHandler slashCommandHandler;

    @Autowired
    public DiscordServiceImpl(final PropertiesManager propertiesManager,
                              final LoggingService loggingService,
                              final FirebaseService firebaseService,
                              final SlashCommandHandler slashCommandHandler) {
        LOGGER = loggingService.getLogger(DiscordServiceImpl.class);
        this.firebaseService = firebaseService;
        this.slashCommandHandler = slashCommandHandler;

        DISCORD_TOKEN = propertiesManager.getDiscordToken();
        SLASH_COMMAND_TEST_GUILDS = propertiesManager.getDiscordDevSlashCmdGuildIdsArray();
        REGISTERED_SLASH_COMMANDS = slashCommandHandler.getSupportedSlashCommands();
    }

    public void init() {
        try {
            JDA jda = JDABuilder.createDefault(DISCORD_TOKEN)
                    .addEventListeners(this)
                    .build()
                    .awaitReady();
            registerCommands(jda);
        } catch (Exception e) {
            LOGGER.error("Error initializing Discord bot: {}", e.getMessage());
        }
    }

    private void registerCommands(final JDA jda) {
        LOGGER.info("Registering the following slash commands: {}",
                Arrays.stream(REGISTERED_SLASH_COMMANDS).map(SlashCommandData::getName).collect(Collectors.toSet()));

        // Register guild-specific slash commands
        for (String guildId : SLASH_COMMAND_TEST_GUILDS) {
            CommandListUpdateAction guildCommands =
                    jda.getGuildById(guildId).updateCommands();

            guildCommands.addCommands(
                    REGISTERED_SLASH_COMMANDS
            ).queue();
        }

        // Register global slash commands
        CommandListUpdateAction globalCommands =
                jda.updateCommands();

        globalCommands.addCommands(
                REGISTERED_SLASH_COMMANDS
        ).queue();
    }

    @Override
    public void onMessageReceived(final MessageReceivedEvent event) {
        // Implement message received handler
    }

    @Override
    public void onSlashCommandInteraction(final SlashCommandInteractionEvent event) {
        slashCommandHandler.handleSlashCommand(event);
    }
}
