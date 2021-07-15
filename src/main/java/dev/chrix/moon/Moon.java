package dev.chrix.moon;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;

import javax.security.auth.login.LoginException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Objects;

/**
 *
 */
public class Moon extends ListenerAdapter {

    public static String Prefix = "+";
    private static final String[] COMMANDS = new String[]{"help", "ping", "prefix", "nick", "automod"};
    private static AutoMod autoMod;
    private static Moon moon;

    public Moon() throws IOException {
        autoMod = new AutoMod();
    }

    public static void main(String[] args) throws Exception {
        moon = new Moon();
        JDA api = JDABuilder.createLight(Secret.BOT_TOKEN, GatewayIntent.GUILD_MESSAGES, GatewayIntent.DIRECT_MESSAGES)
                .addEventListeners(moon)
                .setActivity(Activity.playing("with Chrix!"))
                .build();
        api.upsertCommand("ping", "Calculates the ping of Xova.").queue();
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        Message msg = event.getMessage();
        MessageChannel channel = event.getChannel();
        // Checks for message sender and bot prefix
        if (event.getAuthor().isBot()) return;
        if (!msg.getContentRaw().startsWith(Prefix)) {
            autoMod.handleMessage(event);
            return;
        }
        switch (msg.getContentRaw().substring(1).toLowerCase().split(" ")[0]) {
            case "ping":
                Ping.handleEvent(event);
                break;
            case "prefix":
                changePrefix(event);
                break;
            case "nick":
                Nickname.handleEvent(event);
                break;
            case "automod":
                autoMod.handleEvent(event);
                break;
            default:
                autoMod.handleMessage(event);
                channel.sendMessage("That command does not exist. Please see `+help` for a list of commands.").queue();
        }
    }

    @Override
    public void onSlashCommand(SlashCommandEvent event) {
        MessageChannel channel = event.getChannel();
        switch (event.getName().toLowerCase().split(" ")[0]) {
            case "ping":
                Ping.handleSlash(event);
                break;
            case "nick":
                //Nickname.handleSlash(event);
                break;
        }
    }

    private void changePrefix(MessageReceivedEvent event) {
        Message msg = event.getMessage();
        MessageChannel channel = event.getChannel();
        if (Objects.requireNonNull(event.getMember()).hasPermission(Permission.ADMINISTRATOR)) {
            try {
                String newPrefix = msg.getContentRaw().substring(8);
                if (newPrefix.length() != 1) {
                    channel.sendMessage("Prefix must be exactly 1 character.\nFor example: `+prefix !`").queue();
                } else {
                    Prefix = newPrefix;
                    channel.sendMessage("Prefix has been successfully updated!").queue();
                }
            } catch (StringIndexOutOfBoundsException e) {
                channel.sendMessage("Prefix is used to change the command prefix of Xova.\nCommand usage: `+prefix !`").queue();
                //e.printStackTrace();
            }
        }
    }
}

