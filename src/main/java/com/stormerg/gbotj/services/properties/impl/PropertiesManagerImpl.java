package com.stormerg.gbotj.services.properties.impl;

import com.stormerg.gbotj.services.logging.LoggingService;
import com.stormerg.gbotj.services.properties.PropertiesManager;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;

@Service
@ConfigurationProperties(prefix = "gbotj")
public class PropertiesManagerImpl implements PropertiesManager {

    private static final String LOG_LEVEL_INFO = "INFO";
    private static final String DELIMITER = ",";

    private final LoggingService loggingService;
    private final Logger LOGGER;

    // GBOT PROPERTIES
    private String version;
    private String timezone;
    private String logLevel;

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

    @Autowired
    public PropertiesManagerImpl(final LoggingService loggingService) {
        this.loggingService = loggingService;
        LOGGER = loggingService.getLogger(PropertiesManagerImpl.class);
    }

    @PostConstruct
    public void init() {
        LOGGER.info("PropertiesManagerImpl initialized successfully with the following properties on startup: {}", this);

        // Perform any initialization tasks using the properties here
        String configuredLogLevel = getLogLevel();
        if (configuredLogLevel != null && !LOG_LEVEL_INFO.equalsIgnoreCase(configuredLogLevel)) {
            loggingService.setGlobalLogLevel(configuredLogLevel);
        }
    }

    // Getters and Setters for GBOT PROPERTIES
    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public String getLogLevel() {
        return logLevel;
    }

    public void setLogLevel(String logLevel) {
        this.logLevel = logLevel;
    }

    // Getters and Setters for URL PROPERTIES
    public String getFirebaseUrl() {
        return firebaseUrl;
    }

    public void setFirebaseUrl(String firebaseUrl) {
        this.firebaseUrl = firebaseUrl;
    }

    public String getGitUpdaterUrl() {
        return gitUpdaterUrl;
    }

    public void setGitUpdaterUrl(String gitUpdaterUrl) {
        this.gitUpdaterUrl = gitUpdaterUrl;
    }

    public String getPatreonUrl() {
        return patreonUrl;
    }

    public void setPatreonUrl(String patreonUrl) {
        this.patreonUrl = patreonUrl;
    }

    // Getters and Setters for DISCORD PROPERTIES
    public String getDiscordToken() {
        return discordToken;
    }

    public void setDiscordToken(String discordToken) {
        this.discordToken = discordToken;
    }

    public String getDiscordPatreonGuildId() {
        return discordPatreonGuildId;
    }

    public void setDiscordPatreonGuildId(String discordPatreonGuildId) {
        this.discordPatreonGuildId = discordPatreonGuildId;
    }

    public String getDiscordPatreonRoleId() {
        return discordPatreonRoleId;
    }

    public void setDiscordPatreonRoleId(String discordPatreonRoleId) {
        this.discordPatreonRoleId = discordPatreonRoleId;
    }

    public String[] getDiscordWhitelistGuildIdsArray() {
        return discordWhitelistGuildIds.split(DELIMITER);
    }

    public String getDiscordWhitelistGuildIds() {
        return discordWhitelistGuildIds;
    }

    public void setDiscordWhitelistGuildIds(String discordWhitelistGuildIds) {
        this.discordWhitelistGuildIds = discordWhitelistGuildIds;
    }

    public String[] getDiscordDevSlashCmdGuildIdsArray() {
        return discordDevSlashCmdGuildIds.split(DELIMITER);
    }

    public String getDiscordDevSlashCmdGuildIds() {
        return discordDevSlashCmdGuildIds;
    }

    public void setDiscordDevSlashCmdGuildIds(String discordDevSlashCmdGuildIds) {
        this.discordDevSlashCmdGuildIds = discordDevSlashCmdGuildIds;
    }

    // Getters and Setters for TIME PROPERTIES
    public int getUserResponseTimeoutSeconds() {
        return userResponseTimeoutSeconds;
    }

    public void setUserResponseTimeoutSeconds(int userResponseTimeoutSeconds) {
        this.userResponseTimeoutSeconds = userResponseTimeoutSeconds;
    }

    public int getMusicBotTimeoutSeconds() {
        return musicBotTimeoutSeconds;
    }

    public void setMusicBotTimeoutSeconds(int musicBotTimeoutSeconds) {
        this.musicBotTimeoutSeconds = musicBotTimeoutSeconds;
    }

    public int getStormsMinFrequencySeconds() {
        return stormsMinFrequencySeconds;
    }

    public void setStormsMinFrequencySeconds(int stormsMinFrequencySeconds) {
        this.stormsMinFrequencySeconds = stormsMinFrequencySeconds;
    }

    public int getStormsMaxFrequencySeconds() {
        return stormsMaxFrequencySeconds;
    }

    public void setStormsMaxFrequencySeconds(int stormsMaxFrequencySeconds) {
        this.stormsMaxFrequencySeconds = stormsMaxFrequencySeconds;
    }

    public int getStormsDeleteMessagesSeconds() {
        return stormsDeleteMessagesSeconds;
    }

    public void setStormsDeleteMessagesSeconds(int stormsDeleteMessagesSeconds) {
        this.stormsDeleteMessagesSeconds = stormsDeleteMessagesSeconds;
    }

    public int getMusicBotCacheDeletionMinutes() {
        return musicBotCacheDeletionMinutes;
    }

    public void setMusicBotCacheDeletionMinutes(int musicBotCacheDeletionMinutes) {
        this.musicBotCacheDeletionMinutes = musicBotCacheDeletionMinutes;
    }

    public int getGtradeTransactionTimeoutMinutes() {
        return gtradeTransactionTimeoutMinutes;
    }

    public void setGtradeTransactionTimeoutMinutes(int gtradeTransactionTimeoutMinutes) {
        this.gtradeTransactionTimeoutMinutes = gtradeTransactionTimeoutMinutes;
    }

    public int getWhodisTimeoutMinutes() {
        return whodisTimeoutMinutes;
    }

    public void setWhodisTimeoutMinutes(int whodisTimeoutMinutes) {
        this.whodisTimeoutMinutes = whodisTimeoutMinutes;
    }

    public int getWhodisCooldownMinutes() {
        return whodisCooldownMinutes;
    }

    public void setWhodisCooldownMinutes(int whodisCooldownMinutes) {
        this.whodisCooldownMinutes = whodisCooldownMinutes;
    }

    public int getGtradeMarketSaleTimeoutHours() {
        return gtradeMarketSaleTimeoutHours;
    }

    public void setGtradeMarketSaleTimeoutHours(int gtradeMarketSaleTimeoutHours) {
        this.gtradeMarketSaleTimeoutHours = gtradeMarketSaleTimeoutHours;
    }

    public String toString() {
        return "PropertiesManagerImpl [version=" + version + ", timezone=" + timezone + ", logLevel=" + logLevel + ", firebaseUrl=" + firebaseUrl
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
