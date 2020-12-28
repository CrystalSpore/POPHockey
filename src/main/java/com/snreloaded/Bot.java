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
    private static final String CRON_QUARTER_HOUR = "0,15,30,45 * * * *"; //https://crontab.guru/#0,15,30,45_*_*_*_*
    private static final String CRON_ONE_MINUTE = "* * * * *";
    private static final String cacheVersion = "v1.0";

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

        //Schedule tasks to based on cron timing
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
        int numWins;
        int numLosses;
        int numOTLosses;
        boolean statsPublisher;

        public cacheValue(int teamID, int numGames, int numWins, int numLosses, int numOTLosses, boolean statsPublisher) {
            this.teamID = teamID;
            this.numGames = numGames;
            this.numWins = numWins;
            this.numLosses = numLosses;
            this.numOTLosses = numOTLosses;
            this.statsPublisher = statsPublisher;
        }

        public int getTeamID() {
            return teamID;
        }

        public int getNumGames() {
            return numGames;
        }

        public int getNumWins() {
            return numWins;
        }

        public int getNumLosses() {
            return numLosses;
        }

        public int getNumOTLosses() {
            return numOTLosses;
        }

        public boolean isStatsPublisher() {
            return statsPublisher;
        }
    }

    public Bot(JDA instance) {
        this.instance = instance;

        //path to our cache file (starts with a . so hidden file)
        nhl_cache = new File(System.getProperty("user.home")+"/"+".nhl_cache");

        //if the cache exists, we want to read in from it
        if ( nhl_cache.exists() ) {
            //https://github.com/CrystalSpore/POPHockey/blob/main/CACHE_FORMAT.md
            cacheMap = new HashMap<>();
            Scanner fin = null;
            try {
                fin = new Scanner(new FileInputStream(nhl_cache));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            if (fin != null) {
                if ( fin.hasNext() ) {
                    String line = fin.nextLine();
                    if ( !line.equals(cacheVersion) ) {
                        System.err.println("Cache needs to be updated. " +
                                "Please run update script or manually update based on spec. (Current version = " + cacheVersion + ")");
                        System.exit(0);
                        return;
                    }
                }
                while ( fin.hasNext() ) {
                    String line = fin.nextLine();
                    String[] splitLine = line.split(":");

                    //if there isn't enough for key & value pair, then exit
                    if ( splitLine.length != 8 )
                    {
                        System.err.println("Error reading cache, please verify the cache matches spec...");
                        System.err.println("\tManual modification of cache may be necessary. " +
                                "You most likely don't want to modify the first 2 values (Guild & Channel IDs).");
                        System.exit(0);
                        return;
                    }
                    cacheMap.put(
                            new cacheKey(
                                    Long.parseLong(splitLine[0]),
                                    Long.parseLong(splitLine[1])
                            ),
                            new cacheValue(
                                    Integer.parseInt(splitLine[2]),
                                    Integer.parseInt(splitLine[3]),
                                    Integer.parseInt(splitLine[4]),
                                    Integer.parseInt(splitLine[5]),
                                    Integer.parseInt(splitLine[6]),
                                    Boolean.parseBoolean(splitLine[7])
                            )
                    );
                }
            }
        } else { // otherwise create the file
            try {
                //ignoring output due to checking existence above
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
            int numWins;
            int numLosses;
            int numOTLosses;

            boolean statsPublisher = false;
            if ( strings.length >= 3 &&
                    (strings[2].equals("1") || strings[2].equalsIgnoreCase("true")
                            || strings[2].equalsIgnoreCase("t") || strings[2].equalsIgnoreCase("y")
                            || strings[2].equalsIgnoreCase("yes")
                    )
            ) {
                statsPublisher = true;
            }

            try {
                NHLStats teamStats = NHLPolling.teamStats(nhlTeamID);
                numGames = teamStats.getGamesPlayed();
                numWins = teamStats.getWins();
                numLosses = teamStats.getLosses();
                numOTLosses = teamStats.getOt();
                channel.sendMessage("Setup \"" + teamStats.getTeamName() + "\" for this channel.").queue();
                cacheMap.put(new cacheKey(guildID, channelID), new cacheValue(nhlTeamID, numGames, numWins, numLosses, numOTLosses, statsPublisher));
                Files.write(Paths.get(nhl_cache.getAbsolutePath()), (guildID + ":" +
                        channelID + ":" +
                        nhlTeamID + ":" +
                        numGames + ":" +
                        numWins + ":" +
                        numLosses + ":" +
                        numOTLosses + ":" +
                        statsPublisher + "\n").getBytes(), StandardOpenOption.APPEND);
            } catch (IOException e) {
                channel.sendMessage("Error w/ NHL api").queue();
                e.printStackTrace();
            } catch (ParseException e) {
                channel.sendMessage("Error parsing team list").queue();
                e.printStackTrace();
            }
        }
    }

    //scheduled task
    public void scheduledStats() {
        boolean cacheChanged = false;
        //loop through all entries of cache to check for changes
        for ( Map.Entry<cacheKey, cacheValue> e : cacheMap.entrySet() ) {
            //setup values for ease of use later
            long textChannelID = e.getKey().getChannelID();
            int teamNumber = e.getValue().getTeamID();
            TextChannel textChannel = instance.getTextChannelById(textChannelID);
            assert textChannel != null;
            try {
                NHLStats teamStats = NHLPolling.teamStats(teamNumber);

                //Currently unsure if all teams reset to 0, or only update when game 1 happens
                if ( teamStats.getGamesPlayed() == 0 || teamStats.getGamesPlayed() == 1 )
                {
                    textChannel.sendMessage("Start of new season!").queue();
                }
                //stats publisher block, being the full stats listing
                if ( teamStats.getGamesPlayed() != e.getValue().getNumGames() && e.getValue().isStatsPublisher()) {
                    printStats(teamStats, textChannel);
                    updateCacheEntry(e, teamStats);
                    cacheChanged = true;

                //simple win/lose block
                } else if(teamStats.getGamesPlayed() != e.getValue().getNumGames() && !e.getValue().isStatsPublisher()) {
                    winOrLoss(e, teamStats, textChannel);
                    updateCacheEntry(e, teamStats);
                    cacheChanged = true;
                }
            // print failure, but don't message discord, since this is a scheduled task
            } catch (IOException | ParseException ex) {
                ex.printStackTrace();
            }
        }

        if (cacheChanged) {
            rewriteCacheFile();
        }
    }

    public void printStats(NHLStats teamStats, TextChannel textChannel) {
        MessageBuilder builder = new MessageBuilder();
        builder.appendCodeBlock(teamStats.toString(), "");
        Queue<Message> messages = builder.buildAll(MessageBuilder.SplitPolicy.NEWLINE);
        for (Message m : messages) {
            textChannel.sendMessage(m).queue();
        }
    }

    //Wins + Losses + OT Losses = num Games played
    //  therefore, we need to report all 3 conditions here
    public void winOrLoss(Map.Entry<cacheKey, cacheValue> e, NHLStats teamStats, TextChannel textChannel) {
        //recorded less than actual, indicates a win or loss
        if ( e.getValue().getNumWins() < teamStats.getWins() ) {
            textChannel.sendMessage(teamStats.getTeamName() + " won!").queue();
        }
        else if ( e.getValue().getNumLosses() < teamStats.getLosses() ) {
            textChannel.sendMessage(teamStats.getTeamName() + " lost.").queue();
        }
        else if ( e.getValue().getNumOTLosses() < teamStats.getOt() ) {
            textChannel.sendMessage(teamStats.getTeamName() + " lost in overtime.").queue();
        }
    }

    //update value in cache
    public void updateCacheEntry(Map.Entry<cacheKey, cacheValue> e, NHLStats teamStats) {
        cacheMap.put(e.getKey(), new cacheValue(
                e.getValue().getTeamID(), teamStats.getGamesPlayed(),
                teamStats.getWins(), teamStats.getLosses(), teamStats.getOt(), e.getValue().isStatsPublisher()
        ));
    }

    public void rewriteCacheFile() {
        try {
            // delete & remake before writing
            nhl_cache.delete();
            nhl_cache.createNewFile();
            // write cache version
            Files.write(Paths.get(nhl_cache.getAbsolutePath()), (cacheVersion + "\n").getBytes(), StandardOpenOption.APPEND);
            // write all of cache back out
            for ( Map.Entry<cacheKey, cacheValue> e : cacheMap.entrySet() ) {
                Files.write(Paths.get(nhl_cache.getAbsolutePath()), (e.getKey().getGuildID() + ":" +
                        e.getKey().getChannelID() + ":" +
                        e.getValue().getTeamID() + ":" +
                        e.getValue().getNumGames() + ":" +
                        e.getValue().getNumWins() + ":" +
                        e.getValue().getNumLosses() + ":" +
                        e.getValue().isStatsPublisher() + "\n").getBytes(), StandardOpenOption.APPEND);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
