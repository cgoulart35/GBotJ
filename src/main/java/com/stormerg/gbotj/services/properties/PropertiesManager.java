package com.stormerg.gbotj.services.properties;

public interface PropertiesManager {

    // Getters and Setters for GBOT PROPERTIES
    String getVersion();

    void setVersion(String version);

    String getTimezone();

    void setTimezone(String timezone);

    String getLogLevel();

    void setLogLevel(String logLevel);

    // Getters and Setters for URL PROPERTIES
    String getFirebaseUrl();

    void setFirebaseUrl(String firebaseUrl);

    String getGitUpdaterUrl();

    void setGitUpdaterUrl(String gitUpdaterUrl);

    String getPatreonUrl();

    void setPatreonUrl(String patreonUrl);

    // Getters and Setters for DISCORD PROPERTIES
    String getDiscordToken();

    void setDiscordToken(String discordToken);

    String getDiscordPatreonGuildId();

    void setDiscordPatreonGuildId(String discordPatreonGuildId);

    String getDiscordPatreonRoleId();

    void setDiscordPatreonRoleId(String discordPatreonRoleId);

    String[] getDiscordWhitelistGuildIdsArray();

    String getDiscordWhitelistGuildIds();

    void setDiscordWhitelistGuildIds(String discordWhitelistGuildIds);

    String[] getDiscordDevSlashCmdGuildIdsArray();

    String getDiscordDevSlashCmdGuildIds();

    void setDiscordDevSlashCmdGuildIds(String discordDevSlashCmdGuildIds);

    // Getters and Setters for TIME PROPERTIES
    int getUserResponseTimeoutSeconds();

    void setUserResponseTimeoutSeconds(int userResponseTimeoutSeconds);

    int getMusicBotTimeoutSeconds();

    void setMusicBotTimeoutSeconds(int musicBotTimeoutSeconds);

    int getStormsMinFrequencySeconds();

    void setStormsMinFrequencySeconds(int stormsMinFrequencySeconds);

    int getStormsMaxFrequencySeconds();

    void setStormsMaxFrequencySeconds(int stormsMaxFrequencySeconds);

    int getStormsDeleteMessagesSeconds();

    void setStormsDeleteMessagesSeconds(int stormsDeleteMessagesSeconds);

    int getMusicBotCacheDeletionMinutes();

    void setMusicBotCacheDeletionMinutes(int musicBotCacheDeletionMinutes);

    int getGtradeTransactionTimeoutMinutes();

    void setGtradeTransactionTimeoutMinutes(int gtradeTransactionTimeoutMinutes);

    int getWhodisTimeoutMinutes();

    void setWhodisTimeoutMinutes(int whodisTimeoutMinutes);

    int getWhodisCooldownMinutes();

    void setWhodisCooldownMinutes(int whodisCooldownMinutes);

    int getGtradeMarketSaleTimeoutHours();

    void setGtradeMarketSaleTimeoutHours(int gtradeMarketSaleTimeoutHours);

}
