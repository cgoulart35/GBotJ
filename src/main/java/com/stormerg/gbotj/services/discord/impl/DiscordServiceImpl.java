package com.stormerg.gbotj.services.discord.impl;

import com.stormerg.gbotj.services.discord.DiscordService;
import com.stormerg.gbotj.services.discord.commands.SlashCommandHandler;
import com.stormerg.gbotj.services.properties.PropertiesManager;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.stream.Collectors;

@Slf4j
@Service
public class DiscordServiceImpl extends ListenerAdapter implements DiscordService {

    private final String DISCORD_TOKEN;
    private final String[] SLASH_COMMAND_TEST_GUILDS;
    private final SlashCommandData[] REGISTERED_SLASH_COMMANDS;

    private final SlashCommandHandler slashCommandHandler;

    @Autowired
    public DiscordServiceImpl(final PropertiesManager propertiesManager,
                              final SlashCommandHandler slashCommandHandler) {
        this.slashCommandHandler = slashCommandHandler;

        DISCORD_TOKEN = propertiesManager.getDiscordToken();
        SLASH_COMMAND_TEST_GUILDS = propertiesManager.getDiscordDevSlashCmdGuildIdsArray();
        REGISTERED_SLASH_COMMANDS = slashCommandHandler.getSupportedSlashCommands();
    }

    @PostConstruct
    @Async
    public void init() {
        try {
            JDA jda = JDABuilder.createDefault(DISCORD_TOKEN)
                    .addEventListeners(this)
                    .build()
                    .awaitReady();
            registerCommands(jda);
        } catch (Exception e) {
            log.error("Error initializing Discord bot: {}", e.getMessage());
        }
    }

    private void registerCommands(final JDA jda) {
        // Register guild-specific slash commands
        for (String guildId : SLASH_COMMAND_TEST_GUILDS) {
            CommandListUpdateAction guildCommands =
                    jda.getGuildById(guildId).updateCommands();

            guildCommands.addCommands(
                    REGISTERED_SLASH_COMMANDS
            ).queue();
        }

        // Register global slash commands
        String registeredGuilds = "";
        if (SLASH_COMMAND_TEST_GUILDS.length == 0) {
            CommandListUpdateAction globalCommands =
                    jda.updateCommands();

            globalCommands.addCommands(
                    REGISTERED_SLASH_COMMANDS
            ).queue();
        } else
            registeredGuilds = " in guilds " + Arrays.toString(SLASH_COMMAND_TEST_GUILDS);

        log.info("Registered the following slash commands{}: {}",
                registeredGuilds,
                Arrays.stream(REGISTERED_SLASH_COMMANDS).map(SlashCommandData::getName).collect(Collectors.toSet()));
    }

    @Override
    public void onMessageReceived(final MessageReceivedEvent event) {
        // Implement message received handler
    }

    @Override
    public void onSlashCommandInteraction(final SlashCommandInteractionEvent event) {
        try {
            // Call handleSlashCommand and subscribe to the Mono to trigger the operation
            slashCommandHandler.handleSlashCommand(event).subscribe();
        } catch (Throwable e) {
            log.error("Exception occurred during slash command interaction.", e);
        }
    }
}
