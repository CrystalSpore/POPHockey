package com.snreloaded;

import it.sauronsoftware.cron4j.Scheduler;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.Permission;
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
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;

public class Bot extends ListenerAdapter {

    private final JDA instance;
    private HashMap<cacheKey, cacheValue> cacheMap;
    private final File nhl_cache;
    private static final String CRON_QUARTER_HOUR = "0,15,30,45 * * * *";

    public static void main(String[] args) throws LoginException {
        if (args.length < 1) {
            System.out.println("You have to provide a token as first argument!");
            System.exit(1);
        }
        // args[0] should be the token
        // We only need 2 intents in this bot. We only respond to messages in guilds and private channels.
        // All other events will be disabled.
        JDA instance = JDABuilder.createLight(args[0], GatewayIntent.GUILD_MESSAGES, GatewayIntent.DIRECT_MESSAGES)
                .setActivity(Activity.playing("Type ~ping"))
                .build();
        //We don't immediately add the bot as an event listener, so that we can store the instance into this object
        Bot thisBot = new Bot(instance);
        instance.addEventListener(thisBot);

        //Create new scheduler
        Scheduler scheduler = new Scheduler();

        //Schedule tasks to run every quarter hour, starting on the hour (based on cron timing, eg https://crontab.guru/#0,15,30,45_*_*_*_* )
        scheduler.schedule(CRON_QUARTER_HOUR, thisBot::scheduledStats);

        //Start scheduler
        scheduler.start();
    }

    /** guild & channel for our cached data */
    private static class cacheKey {
        long guildID;
        long channelID;

        public cacheKey(long guildID, long channelID) {
            this.guildID = guildID;
            this.channelID = channelID;
        }


        public long getGuildID() {
            return guildID;
        }

        public long getChannelID() {
            return channelID;
        }
    }

    /**
     * data structure for our cached data
     */
    private static class cacheValue {
        int teamID;
        int numGames;

        public cacheValue(int teamID, int numGames) {
            this.teamID = teamID;
            this.numGames = numGames;
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

        //path to our cache file (starts with a . so hidden file)
        nhl_cache = new File(System.getProperty("user.home")+"/"+".nhl_cache");

        //if the cache exists, we want to read in from it
        if ( nhl_cache.exists() ) {
            //Guild/Server, <channel, teamID, numGames>
            cacheMap = new HashMap<>();
            Scanner fin = null;
            try {
                fin = new Scanner(new FileInputStream(nhl_cache));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            if (fin != null) {
                while ( fin.hasNext() ) {
                    String line = fin.nextLine();
                    String[] splitLine = line.split(":");
                    cacheMap.put(
                            new cacheKey(
                                    Long.parseLong(splitLine[0]),
                                    Long.parseLong(splitLine[1])
                            ),
                            new cacheValue(
                                    Integer.parseInt(splitLine[2]),
                                    Integer.parseInt(splitLine[3])
                            )
                    );
                }
            }
        } else { // otherwise create the file
            try {
                //ignoring output due to checking existence above
                //noinspection ResultOfMethodCallIgnored
                nhl_cache.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        Message msg = event.getMessage();
        //check entire text or start of text for command, then call method
        if (msg.getContentRaw().equals("~ping")) {
            ping(event);
        } else if (msg.getContentRaw().equals("~listTeams")) {
            listTeams(event);
        } else if ( (msg.getContentRaw().startsWith("~setupTeam")
                || msg.getContentRaw().startsWith("~setup ") ) && Objects.requireNonNull(msg.getMember()).hasPermission(Permission.ADMINISTRATOR)) {
            setupTeam(event);
        }
    }

    //ping command, for checking bot latency
    public void ping(MessageReceivedEvent event) {
        MessageChannel channel = event.getChannel();
        long time = System.currentTimeMillis();
        channel.sendMessage("Pong!") /* => RestAction<Message> */
                .queue(response /* => Message */ -> response.editMessageFormat("Pong: %d ms", System.currentTimeMillis() - time).queue());
    }

    //list all teams that NHL reports back from API
    public void listTeams(MessageReceivedEvent event) {
        MessageChannel channel = event.getChannel();
        try {
            //multi-line messages need a MessageBuilder
            MessageBuilder builder = new MessageBuilder();

            //NHLPolling is the API class
            builder.appendCodeBlock(NHLPolling.listTeams(),"");
            Queue<Message> messages = builder.buildAll(MessageBuilder.SplitPolicy.NEWLINE);

            //multi-line message needs a loop to send all lines
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
    }

    //setup a channel with auto reporting team stats
    public void setupTeam(MessageReceivedEvent event) {
        MessageChannel channel = event.getChannel();
        String[] strings = event.getMessage().getContentRaw().split(" ");

        //help block
        if ( strings.length == 1 || strings[1].equalsIgnoreCase("h") || strings[1].equalsIgnoreCase("help") ) {
            MessageBuilder builder = new MessageBuilder();
            builder.appendCodeBlock("Format as > !setupTeam <teamID>\n\tSetup NHL team listener in current channel.\n\tUse !listTeams to find the teamID.","");
            Queue<Message> messages = builder.buildAll(MessageBuilder.SplitPolicy.NEWLINE);
            for (Message m : messages) {
                channel.sendMessage(m).queue();
            }
        } else { //setup logic block
            //data for cache (not yet implemented)
            long guildID = event.getGuild().getIdLong();
            long channelID = event.getTextChannel().getIdLong();
            int nhlTeamID = Integer.parseInt(strings[1]);
            int numGames;

            boolean keyExists = false;
            for ( cacheKey key : cacheMap.keySet() ) {
                if ( key.getGuildID() == guildID && key.getChannelID() == channelID ) {
                    channel.sendMessage("There is already a team setup for this channel. Please remove team from channel before attempting to setup a new team.").queue();
                    keyExists = true;
                    break;
                }
            }

            if (!keyExists) {
                try {
                    //testing message for checkNHL method. currently broken on JSON parse
                    numGames = NHLPolling.teamStats(nhlTeamID).getGamesPlayed();
                    channel.sendMessage("guild: " + guildID + " | channel: " + channelID + " | nhlTeamID: " + nhlTeamID + " | Number of Games Played: " + numGames).queue();
                    cacheMap.put(new cacheKey(guildID, channelID), new cacheValue(nhlTeamID, numGames));
                    Files.write(Paths.get(nhl_cache.getAbsolutePath()), (guildID + ":" + channelID + ":" + nhlTeamID + ":" + numGames + "\n").getBytes(), StandardOpenOption.APPEND);
                } catch (IOException e) {
                    channel.sendMessage("Error w/ NHL api").queue();
                    e.printStackTrace();
                } catch (ParseException e) {
                    channel.sendMessage("Error parsing team list").queue();
                    e.printStackTrace();
                }
            }
        }
    }

    //scheduled task
    public void scheduledStats() {
        for ( Map.Entry<cacheKey, cacheValue> e : cacheMap.entrySet() ) {
            long textChannelID = e.getKey().getChannelID();
            int teamNumber = e.getValue().getTeamID();
            TextChannel textChannel = instance.getTextChannelById(textChannelID);
            assert textChannel != null;
            try {
                NHLStats stats = NHLPolling.teamStats(teamNumber);
                MessageBuilder builder = new MessageBuilder();
                builder.appendCodeBlock(stats.toString(), "");
                Queue<Message> messages = builder.buildAll(MessageBuilder.SplitPolicy.NEWLINE);
                for (Message m : messages) {
                    textChannel.sendMessage(m).queue();
                }
            } catch (IOException | ParseException ex) {
                ex.printStackTrace();
            }
        }
    }
}
