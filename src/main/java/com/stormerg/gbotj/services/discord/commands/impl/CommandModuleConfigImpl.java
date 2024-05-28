package com.stormerg.gbotj.services.discord.commands.impl;

import com.stormerg.gbotj.services.discord.commands.AbstractCommandModule;
import com.stormerg.gbotj.services.discord.commands.PaginationService;
import com.stormerg.gbotj.services.discord.commands.helpers.InteractionHelper;
import com.stormerg.gbotj.services.discord.commands.models.CustomSlashCommandData;
import com.stormerg.gbotj.services.firebase.FirebaseService;
import com.stormerg.gbotj.services.properties.PropertiesManager;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class CommandModuleConfigImpl extends AbstractCommandModule {

    @Autowired
    public CommandModuleConfigImpl(final PropertiesManager propertiesManager,
                                   final FirebaseService firebaseService,
                                   final PaginationService paginationService) {
        super(propertiesManager, firebaseService, paginationService);
    }

    @Override
    protected void setSupportedCommands() {
        CustomSlashCommandData configCommand = new CustomSlashCommandData("config", "Config command");
        configCommand.setGuildOnly(true);

        supportedCommands = new CustomSlashCommandData[] {
                configCommand
        };
    }

    @Override
    protected void setFeatureToggleName() {
        featureToggleName = null;
    }

    @Override
    public Mono<Void> handleCommand(final CustomSlashCommandData command, final SlashCommandInteractionEvent event) {
        if (command.getName().equals("config")) {
            // Call configCommand asynchronously and return its Mono
            return configCommand(command, event);
        }

        // Unknown command
        return Mono.empty();
    }

    // TODO - remove example
    private Mono<Void> configCommand(final CustomSlashCommandData command, final SlashCommandInteractionEvent event) {
        // Start by sending a message to the user "starting config command"
        Mono<Void> startingMessage = Mono.fromRunnable(() ->
                InteractionHelper.sendMessage(event, true, "Starting config command"));

        // Get the server's prefix value
        Mono<String> getPrefix = firebaseService.getValueAtPathString("/servers/" + event.getGuild().getId() + "/prefix");

        // Send message about prefix before endingMessage
        Mono<Void> prefixMessage = getPrefix.doOnNext(prefix ->
                InteractionHelper.sendMessage(event, false, "Your prefix is: " + prefix)).then();

        // Send ending message
        Mono<Void> endingMessage = Mono.fromRunnable(() -> {
            InteractionHelper.sendMessage(event, false, "Ending config command");

            List<MessageEmbed> pages = new ArrayList<>();
            pages.add(createDummyEmbed(1));
            pages.add(createDummyEmbed(2));
            pages.add(createDummyEmbed(3));
            paginationService.paginate(event, false,
                    propertiesManager.getUserResponseTimeoutSeconds(), TimeUnit.SECONDS, pages);
        });

        // Combine the starting message, prefix message, and ending message
        return startingMessage
                .then(prefixMessage)
                .then(endingMessage);
    }

    // TODO - remove example
    private MessageEmbed createDummyEmbed(int num) {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle("Example Embed Title" + num)
                .setDescription("This is an example of a message embed.")
                .setColor(Color.BLUE)
                .addField("Field 1", "This is the value of field 1", true)
                .addField("Field 2", "This is the value of field 2", true)
                .addField("Field 3", "This is the value of field 3", false)
                .setFooter("This is the footer text", "https://example.com/footer_icon.png")
                .setThumbnail("https://example.com/thumbnail.png")
                .setImage("https://example.com/image.png");
        return builder.build();
    }

//    // TODO - remove example
//    private Mono<Void> configCommand(final CustomSlashCommandData command, final SlashCommandInteractionEvent event) {
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
