package com.stormerg.gbotj.services.discord.commands.impl;

import com.stormerg.gbotj.services.discord.commands.CommandModule;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.internal.interactions.CommandDataImpl;
import org.springframework.stereotype.Service;

@Service
public class ConfigCommandsImpl implements CommandModule {

    private static final SlashCommandData[] supportedCommands = new SlashCommandData[] {
        new CommandDataImpl("ping", "Ping command"),
        new CommandDataImpl("hello", "Hello command")
    };

    public ConfigCommandsImpl() {

    }

    public SlashCommandData[] getSupportedCommands() {
        return supportedCommands;
    }

    public void handleCommand(SlashCommandData command, SlashCommandInteractionEvent event) {
//        switch (command.getName()) {
//            case "ping":
//                break;
//            case "hello":
//                break;
//            case "global-ping":
//                break;
//            case "global-hello":
//                break;
//            default:
//                // Unknown command
//                break;
//        }
    }
}
