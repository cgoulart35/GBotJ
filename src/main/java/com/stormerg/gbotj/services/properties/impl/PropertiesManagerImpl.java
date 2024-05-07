package com.stormerg.gbotj.services.properties.impl;

import com.stormerg.gbotj.services.properties.PropertiesManager;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Slf4j
@Setter
@Getter
@Service
@ConfigurationProperties(prefix = "gbotj")
public class PropertiesManagerImpl implements PropertiesManager {

    private static final String DELIMITER = ",";

    // GBOT PROPERTIES
    private String version;
    private String timezone;

    // URL PROPERTIES
    private String firebaseUrl;
    private String gitUpdaterUrl;
    private String patreonUrl;

    // DISCORD PROPERTIES
    private String discordToken;
    private String discordPatreonGuildId;
    private String discordPatreonRoleId;
    private String discordWhitelistGuildIds;
    private String discordDevSlashCmdGuildIds;

    // TIME PROPERTIES
    private int userResponseTimeoutSeconds;
    private int musicBotTimeoutSeconds;
    private int stormsMinFrequencySeconds;
    private int stormsMaxFrequencySeconds;
    private int stormsDeleteMessagesSeconds;
    private int musicBotCacheDeletionMinutes;
    private int gtradeTransactionTimeoutMinutes;
    private int whodisTimeoutMinutes;
    private int whodisCooldownMinutes;
    private int gtradeMarketSaleTimeoutHours;

    @PostConstruct
    public void init() {
        log.info("PropertiesManagerImpl initialized successfully with the following properties on startup: {}", this);
    }

    public String[] getDiscordWhitelistGuildIdsArray() {
        return discordWhitelistGuildIds.split(DELIMITER);
    }

    public String[] getDiscordDevSlashCmdGuildIdsArray() {
        return Arrays.stream(discordDevSlashCmdGuildIds.split(DELIMITER))
                .filter(id -> !id.isEmpty()).toArray(String[]::new);
    }

    public String toString() {
        return "PropertiesManagerImpl [version=" + version + ", timezone=" + timezone + ", firebaseUrl=" + firebaseUrl
                + ", gitUpdaterUrl=" + gitUpdaterUrl + ", patreonUrl=" + patreonUrl + ", discordToken=" + discordToken
                + ", discordPatreonGuildId=" + discordPatreonGuildId + ", discordPatreonRoleId=" + discordPatreonRoleId
                + ", discordWhitelistGuildIds=" + discordWhitelistGuildIds + ", discordDevSlashCmdGuildIds=" + discordDevSlashCmdGuildIds
                + ", userResponseTimeoutSeconds=" + userResponseTimeoutSeconds + ", musicBotTimeoutSeconds=" + musicBotTimeoutSeconds
                + ", stormsMinFrequencySeconds=" + stormsMinFrequencySeconds + ", stormsMaxFrequencySeconds=" + stormsMaxFrequencySeconds
                + ", stormsDeleteMessagesSeconds=" + stormsDeleteMessagesSeconds + ", musicBotCacheDeletionMinutes=" + musicBotCacheDeletionMinutes
                + ", gtradeTransactionTimeoutMinutes=" + gtradeTransactionTimeoutMinutes + ", whodisTimeoutMinutes=" + whodisTimeoutMinutes
                + ", whodisCooldownMinutes=" + whodisCooldownMinutes + ", gtradeMarketSaleTimeoutHours=" + gtradeMarketSaleTimeoutHours + "]";
    }
}
