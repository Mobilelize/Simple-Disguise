package net.mobilelize.disguise.manager;

import net.mobilelize.disguise.Disguise;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;

public class ConfigManager {

    private final Disguise plugin;

    // Cached configuration values
    private boolean allowSkinCommand;
    private boolean allowNameCommand;
    private int commandCooldown;
    private boolean allowDuplicateLogin;
    private boolean checkRealUsername;
    private String duplicateLoginAction;
    private boolean randomNameEnabled;
    private List<String> randomNameList;
    private boolean forceUseRandomNameList;
    private int maxModifiableLength;
    private int getDelayAmount;
    private boolean autoCommandEnabled;
    private boolean realNameBehaviorEnabled;
    private boolean realNameBehaviorForce;
    private boolean realNameApplyToAll;
    private boolean nameBlacklistEnabled;
    private List<String> nameBlacklist;
    private boolean mobAutoDetect;
    private boolean mobOverrideRealName;

    // Constructor
    public ConfigManager(Disguise plugin) {
        this.plugin = plugin;
        loadSettings(); // Load settings on initialization
    }

    // Reload the configuration and update cached values
    public String reloadConfig() {
        String output = ChatColor.BLUE + "Simple Disguise configuration reloaded successfully!";
        plugin.reloadConfig(); // Reload the config file
        loadSettings();
        plugin.getLogger().info("Configuration reloaded successfully!");
        // Check the configuration and log a recommendation if needed
        if (!isCheckRealUsername() && isAllowDuplicateLogin()) {
            Disguise.getInstance().getLogger().warning("It is recommended to enable 'check-real-username' to prevent conflicts and abuse.");
            output = output + ChatColor.RED + "\nIt is recommended to enable 'check-real-username' to prevent conflicts and abuse.";
        }
        return output;
    }

    // Load all settings into memory
    private void loadSettings() {
        FileConfiguration config = plugin.getConfig();

        // Allow commands
        allowSkinCommand = config.getBoolean("allow-commands.skin", true);
        allowNameCommand = config.getBoolean("allow-commands.name", true);
        commandCooldown = config.getInt("command-cooldown.cooldown", 10);

        // Duplicate login settings
        allowDuplicateLogin = config.getBoolean("allow-duplicate-login.enabled", true);
        checkRealUsername = config.getBoolean("allow-duplicate-login.check-real-username", true);
        duplicateLoginAction = config.getString("allow-duplicate-login.action", "disguise-joining-player");

        // Random name generation settings
        randomNameEnabled = config.getBoolean("allow-duplicate-login.random-name.enabled", true);
        forceUseRandomNameList = config.getBoolean("allow-duplicate-login.random-name.force-use-list", false);
        randomNameList = config.getStringList("allow-duplicate-login.random-name.list");
        maxModifiableLength = config.getInt("allow-duplicate-login.random-name.max-modifiable-length", 13);
        getDelayAmount = config.getInt("random-name.delay", 1000);

        // Auto-command settings
        autoCommandEnabled = config.getBoolean("auto-command.enabled", true);

        // Real name behavior settings
        realNameBehaviorEnabled = config.getBoolean("real-name-behavior.enabled", true);
        realNameBehaviorForce = config.getBoolean("real-name-behavior.force", false);
        realNameApplyToAll = config.getBoolean("real-name-behavior.apply-to-all", false);

        // Name blacklist settings
        nameBlacklistEnabled = config.getBoolean("name-blacklist.enabled", false);
        nameBlacklist = config.getStringList("name-blacklist.names");

        // Mob disguise settings
        mobAutoDetect = config.getBoolean("mob-disguise.auto-detect", true);
        mobOverrideRealName = config.getBoolean("mob-disguise.override-real-name", true);
    }

    // Getter methods for cached values

    // Command settings
    public boolean isAllowSkinCommand() {
        return allowSkinCommand;
    }

    public boolean isAllowNameCommand() {
        return allowNameCommand;
    }

    public int getCommandCooldown() {return commandCooldown;}

    // Duplicate login settings
    public boolean isAllowDuplicateLogin() {
        return allowDuplicateLogin;
    }

    public boolean isCheckRealUsername() {
        return checkRealUsername;
    }

    public String getDuplicateLoginAction() {
        return duplicateLoginAction;
    }

    // Random name generation settings
    public boolean isRandomNameEnabled() {
        return randomNameEnabled;
    }

    public boolean isForceUseRandomNameList() {
        return forceUseRandomNameList;
    }

    public List<String> getRandomNameList() {
        return randomNameList;
    }

    public int getMaxModifiableLength() {
        return maxModifiableLength;
    }

    public int getDelayAmount() {return getDelayAmount;}

    // Auto-command settings
    public boolean isAutoCommandEnabled() {
        return autoCommandEnabled;
    }

    // Real name behavior settings
    public boolean isRealNameBehaviorEnabled() {
        return realNameBehaviorEnabled;
    }

    public boolean isRealNameBehaviorForce() {
        return realNameBehaviorForce;
    }

    public boolean isRealNameApplyToAll() {
        return realNameApplyToAll;
    }

    // Name blacklist settings
    public boolean isNameBlacklistEnabled() {
        return nameBlacklistEnabled;
    }

    public List<String> getNameBlacklist() {
        return nameBlacklist;
    }

    // Mob disguise settings
    public boolean isMobAutoDetect() {
        return mobAutoDetect;
    }

    public boolean isMobOverrideRealName() {
        return mobOverrideRealName;
    }
}
