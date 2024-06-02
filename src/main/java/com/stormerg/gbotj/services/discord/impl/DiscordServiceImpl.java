package com.stormerg.gbotj.services.discord.impl;

import com.stormerg.gbotj.services.discord.DiscordService;
import com.stormerg.gbotj.services.discord.impl.models.CustomSlashCommandData;
import com.stormerg.gbotj.services.discord.impl.listeners.PaginationService;
import com.stormerg.gbotj.services.discord.impl.listeners.SlashCommandService;
import com.stormerg.gbotj.services.properties.PropertiesManager;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.stream.Collectors;

@Slf4j
@Service
public class DiscordServiceImpl extends ListenerAdapter implements DiscordService {

    private final String DISCORD_TOKEN;
    private final String[] SLASH_COMMAND_TEST_GUILDS;
    private final CustomSlashCommandData[] REGISTERED_SLASH_COMMANDS;

    private final SlashCommandService slashCommandService;
    private final PaginationService paginationService;

    private JDA jda;

    @Autowired
    public DiscordServiceImpl(final PropertiesManager propertiesManager,
                              final SlashCommandService slashCommandService,
                              final PaginationService paginationService) {
        this.slashCommandService = slashCommandService;
        this.paginationService = paginationService;

        DISCORD_TOKEN = propertiesManager.getDiscordToken();
        SLASH_COMMAND_TEST_GUILDS = propertiesManager.getDiscordDevSlashCmdGuildIdsArray();
        REGISTERED_SLASH_COMMANDS = slashCommandService.getSupportedSlashCommands();
    }

    @PostConstruct
    @Async
    public void init() {
        try {
            jda = JDABuilder.createDefault(DISCORD_TOKEN, EnumSet.allOf(GatewayIntent.class))
                    .addEventListeners(this)
                    .build()
                    .awaitReady();
            slashCommandService.setJda(jda);
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
                Arrays.stream(REGISTERED_SLASH_COMMANDS).map(CustomSlashCommandData::getName).collect(Collectors.toSet()));
    }

    @Override
    public void onSlashCommandInteraction(final SlashCommandInteractionEvent event) {
        // Call handleSlashCommand and subscribe to the Mono to trigger the operation
        slashCommandService.handleSlashCommand(event).subscribe();
    }

    @Override
    public void onMessageReactionAdd(final MessageReactionAddEvent event) {
        // Every reaction check to see if registered for pagination
        paginationService.onMessageReactionAdd(event);
    }

    @Override
    public void onCommandAutoCompleteInteraction(final CommandAutoCompleteInteractionEvent event) {
        // TODO
    }

    @Override
    public void onMessageReceived(final MessageReceivedEvent event) {
        // TODO
    }

    @Override
    public void onReady(final ReadyEvent event) {
        // TODO
    }

    @Override
    public void onGuildJoin(final GuildJoinEvent event) {
        // TODO
    }

    @Override
    public void onGuildLeave(final GuildLeaveEvent event) {
        // TODO
    }
}
