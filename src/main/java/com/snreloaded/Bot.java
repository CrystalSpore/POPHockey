package com.snreloaded;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.json.simple.parser.ParseException;

import javax.security.auth.login.LoginException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Queue;
import java.util.Scanner;

public class Bot extends ListenerAdapter {

    JDA instance;
    HashMap<Long, cacheData> cacheMap;
    File nhl_cache;

    public static void main(String[] args) throws LoginException
    {
        if (args.length < 1) {
            System.out.println("You have to provide a token as first argument!");
            System.exit(1);
        }
        // args[0] should be the token
        // We only need 2 intents in this bot. We only respond to messages in guilds and private channels.
        // All other events will be disabled.
        JDA instance = JDABuilder.createLight(args[0], GatewayIntent.GUILD_MESSAGES, GatewayIntent.DIRECT_MESSAGES)
                .setActivity(Activity.playing("Type !popPing"))
                .build();
        Bot thisBot = new Bot(instance);
        instance.addEventListener(thisBot);
    }

    protected class cacheData {
        long channelID;
        int teamID;
        int numGames;

        public cacheData(long channelID, int teamID, int numGames) {
            this.channelID = channelID;
            this.teamID = teamID;
            this.numGames = numGames;
        }

        public long getChannelID() {
            return channelID;
        }

        public int getTeamID() {
            return teamID;
        }

        public int getNumGames() {
            return numGames;
        }

        public void setNumGames(int numGames) {
            this.numGames = numGames;
        }
    }

    public Bot(JDA instance) {
        this.instance = instance;

        nhl_cache = new File(System.getProperty("user.home")+"/"+".nhl_cache");

        if ( nhl_cache.exists() )
        {
            //Guild/Server, <channel, teamID, numGames>
            cacheMap = new HashMap<>();
            Scanner fin = null;
            try {
                fin = new Scanner(new FileInputStream(nhl_cache));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            if (fin != null) {
                while ( fin.hasNext() )
                {
                    String line = fin.nextLine();
                    String[] splitLine = line.split(":");
                    cacheMap.put(Long.parseLong(splitLine[0]),
                            new cacheData( Long.parseLong(splitLine[1]),
                                    Integer.parseInt(splitLine[2]),
                                    Integer.parseInt(splitLine[3])));
                }
            }
        }
        else
        {
            try {
                nhl_cache.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        Message msg = event.getMessage();
        if (msg.getContentRaw().equals("!popPing")) {
            MessageChannel channel = event.getChannel();
            long time = System.currentTimeMillis();
            channel.sendMessage("Pong!") /* => RestAction<Message> */
                    .queue(response /* => Message */ -> {
                        response.editMessageFormat("Pong: %d ms", System.currentTimeMillis() - time).queue();
                    });
        } else if (msg.getContentRaw().equals("!listTeams")) {
            MessageChannel channel = event.getChannel();
            try {
                MessageBuilder builder = new MessageBuilder();
                //System.out.println(NHLPolling.listTeams());
                builder.appendCodeBlock(NHLPolling.listTeams(),"");
                Queue<Message> messages = builder.buildAll(MessageBuilder.SplitPolicy.NEWLINE);
                for (Message m : messages) {
                    channel.sendMessage(m).queue();
                }
            } catch (IOException e) {
                channel.sendMessage("Error w/ NHL api").queue();
                e.printStackTrace();
            } catch (ParseException e) {
                channel.sendMessage("Error parsing team list").queue();
                e.printStackTrace();
            }
        } else if (msg.getContentRaw().startsWith("!setupTeam")) {
            MessageChannel channel = event.getChannel();
            String[] strings = msg.getContentRaw().split(" ");

            if ( strings.length == 1 || strings[1].toLowerCase().equals("h") || strings[1].toLowerCase().equals("help") ) {
                MessageBuilder builder = new MessageBuilder();
                builder.appendCodeBlock("Format as > !setupTeam <teamID>\n\tSetup NHL team listener in current channel.\n\tUse !listTeams to find the teamID.","");
                Queue<Message> messages = builder.buildAll(MessageBuilder.SplitPolicy.NEWLINE);
                for (Message m : messages) {
                    channel.sendMessage(m).queue();
                }
            } else {
                channel.sendMessage("start").queue();
                Long channelID = channel.getIdLong();
                Long guildID = event.getGuild().getIdLong();
                int nhlTeamID = Integer.parseInt(strings[1]);
                int numGames;
                try {
                    numGames = NHLPolling.checkNHL(nhlTeamID).getGamesPlayed();
                    channel.sendMessage("guild: " + guildID + " | channel: " + channelID + " | nhlTeamID: " + nhlTeamID + " | Number of Games Played: " + numGames).queue();
                    channel.sendMessage("test").queue();
                } catch (IOException e) {
                    channel.sendMessage("Error w/ NHL api").queue();
                    e.printStackTrace();
                } catch (ParseException e) {
                    channel.sendMessage("Error parsing team list").queue();
                    e.printStackTrace();
                }
                channel.sendMessage("end").queue();
            }
        }

    }
}
