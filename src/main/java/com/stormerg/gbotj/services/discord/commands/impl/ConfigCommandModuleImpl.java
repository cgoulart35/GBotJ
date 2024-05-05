package com.stormerg.gbotj.services.discord.commands.impl;

import com.stormerg.gbotj.services.discord.commands.AbstractCommandModule;
import com.stormerg.gbotj.services.firebase.FirebaseService;
import com.stormerg.gbotj.services.properties.PropertiesManager;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.internal.interactions.CommandDataImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class ConfigCommandModuleImpl extends AbstractCommandModule {

    @Autowired
    public ConfigCommandModuleImpl(final PropertiesManager propertiesManager,
                                   final FirebaseService firebaseService) {
        super(propertiesManager, firebaseService);
    }

    @Override
    protected void setSupportedCommands(){
        supportedCommands = new SlashCommandData[] {
                new CommandDataImpl("config", "Config command")
        };
    }

    @Override
    public Mono<Void> handleCommand(final SlashCommandData command, final SlashCommandInteractionEvent event) {
        if (command.getName().equals("config")) {
            // Call configCommand asynchronously and return its Mono
            log.info("test");
            return configCommand(command, event);
        }

        // Unknown command
        return Mono.empty();
    }

    // TODO - remove example
    private Mono<Void> configCommand(final SlashCommandData command, final SlashCommandInteractionEvent event) {
        // Start by sending a message to the user "starting config command"
        Mono<Void> startingMessage = Mono.fromRunnable(() -> sendMessage(event, true, "Starting config command"));

        // Get the server's prefix value
        Mono<String> getPrefix = firebaseService.getValueAtPathString("/servers/" + event.getGuild().getId() + "/prefix");

        // Send message about prefix before endingMessage
        Mono<Void> prefixMessage = getPrefix.doOnNext(prefix ->
                        sendMessage(event, false, "Your prefix is: " + prefix)).then();

        // Send ending message
        Mono<Void> endingMessage = Mono.fromRunnable(() -> sendMessage(event, false, "Ending config command"));

        // Combine the starting message, prefix message, and ending message
        return startingMessage
                .then(prefixMessage)
                .then(endingMessage);
    }

//    // TODO - remove example
//    private Mono<Void> configCommand(final SlashCommandData command, final SlashCommandInteractionEvent event) {
//        // Start by sending a message to the user "starting config command"
//        Mono<Void> startingMessage = Mono.fromRunnable(() -> event.reply("Starting config command").queue());
//
//        // Set the server's prefix value to "config"
//        Mono<Void> setPrefix = firebaseService.setValueAtPath("/servers/" + event.getGuild().getId() + "/prefix", "config");
//
//        // Get the server's channel_admin value
//        Mono<Map<String, Object>> getChannelAdmin = firebaseService.getValueAtPathMap("/servers/" + event.getGuild().getId() + "/channel_admin");
//
//        // Modify the channel value to include "_id"
//        Mono<Map<String, Object>> channelWithId = getChannelAdmin.map(channelAdmin -> {
//            String channelId = (String) channelAdmin.get("channel_admin");
//            channelAdmin.put("channel_admin_id", channelId + "_id");
//            return channelAdmin;
//        });
//
//        // Once all operations are completed, compose the final message and send it to the user
//        return startingMessage
//                .flatMap(start -> setPrefix)
//                .then(channelWithId)
//                .flatMap(channelAdmin -> Mono.fromRunnable(() ->
//                        event.reply("Prefix set to 'config'. Admin channel: " + channelAdmin.get("channel_admin_id")).queue()));
//    }
}
