package com.github.rmsy.channels;

import com.github.rmsy.channels.command.GlobalChannelCommands;
import com.github.rmsy.channels.impl.SimpleChannel;
import com.github.rmsy.channels.impl.SimplePlayerManager;
import com.github.rmsy.channels.listener.ChatListener;
import com.github.rmsy.channels.listener.PlayerListener;
import com.google.common.base.Preconditions;
import com.sk89q.bukkit.util.BukkitCommandsManager;
import com.sk89q.bukkit.util.CommandsManagerRegistration;
import com.sk89q.minecraft.util.commands.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

public class ChannelsPlugin extends JavaPlugin {
    public static final String GLOBAL_CHANNEL_PARENT_NODE = "channels.global";
    public static final String GLOBAL_CHANNEL_SEND_NODE = ChannelsPlugin.GLOBAL_CHANNEL_PARENT_NODE + ".send";
    public static final String GLOBAL_CHANNEL_RECEIVE_NODE = ChannelsPlugin.GLOBAL_CHANNEL_PARENT_NODE + ".receive";
    /** The plugin instance. */
    private static ChannelsPlugin plugin;
    /** The global channel. */
    private Channel globalChannel;
    /** The default channel. */
    private Channel defaultChannel;
    /** The commands manager. */
    private CommandsManager commands;
    /** The commands' registration. */
    private CommandsManagerRegistration commandsRegistration;
    /** The player manager. */
    private PlayerManager playerManager;

    /**
     * Gets the plugin instance
     *
     * @return The plugin instance.
     */
    public static ChannelsPlugin get() {
        return ChannelsPlugin.plugin;
    }

    /**
     * Gets the universal player manager.
     *
     * @return The universal player manager.
     */
    public PlayerManager getPlayerManager() {
        return this.playerManager;
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command cmd, String commandLabel, String[] args) {
        try {
            this.commands.execute(cmd.getName(), args, sender, sender);
        } catch (CommandPermissionsException e) {
            sender.sendMessage(ChatColor.RED + "You don't have permission.");
        } catch (MissingNestedCommandException e) {
            sender.sendMessage(ChatColor.RED + e.getUsage());
        } catch (CommandUsageException e) {
            sender.sendMessage(ChatColor.RED + e.getMessage());
            sender.sendMessage(ChatColor.RED + "Usage: " + e.getUsage());
        } catch (WrappedCommandException e) {
            sender.sendMessage(ChatColor.RED + "An unknown error has occurred. Please notify an administrator.");
            e.printStackTrace();
        } catch (CommandException e) {
            sender.sendMessage(ChatColor.RED + e.getMessage());
        }

        return true;
    }

    @Override
    public void onDisable() {
        HandlerList.unregisterAll(this);
        this.commandsRegistration = null;
        this.commands = null;
        this.playerManager = null;
        this.defaultChannel = null;
        this.globalChannel = null;
    }

    @Override
    public void onEnable() {
        ChannelsPlugin.plugin = this;

        this.globalChannel = new SimpleChannel(ChatColor.WHITE + "<%s" + ChatColor.RESET + ChatColor.WHITE + ">: ", true, ChannelsPlugin.GLOBAL_CHANNEL_RECEIVE_NODE);
        this.playerManager = new SimplePlayerManager();
        Bukkit.getPluginManager().registerEvents(new ChatListener(this), this);
        Bukkit.getPluginManager().registerEvents(new PlayerListener(this), this);

        this.commands = new BukkitCommandsManager();
        this.commandsRegistration = new CommandsManagerRegistration(this, this.commands);
        this.commandsRegistration.register(GlobalChannelCommands.class);
    }

    /**
     * Gets the global channel.
     *
     * @return The global channel.
     */
    public Channel getGlobalChannel() {
        return this.globalChannel;
    }

    /**
     * Gets the channel that newly-connected players will be added to.
     *
     * @return The channel.
     */
    public Channel getDefaultChannel() {
        return this.defaultChannel;
    }

    /**
     * Sets the channel that newly-connected players will be added to.
     *
     * @param channel The channel.
     */
    public void setDefaultChannel(Channel channel) {
        this.defaultChannel = Preconditions.checkNotNull(channel, "Channel");
    }
}
