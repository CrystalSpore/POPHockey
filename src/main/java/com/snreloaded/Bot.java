package com.snreloaded;

import com.vdurmont.emoji.EmojiParser;
import it.sauronsoftware.cron4j.Scheduler;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.internal.utils.tuple.ImmutablePair;
import org.json.simple.parser.ParseException;

import javax.security.auth.login.LoginException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class Bot extends ListenerAdapter {

    private final JDA instance;
    private ConcurrentHashMap<Long, ConcurrentHashMap<Long, CopyOnWriteArrayList<CacheValue>>> cacheMap;
    private HashMap<Integer, String> teamList;
    private final File nhl_cache;
    private static final String CRON_ONE_DAY = "0 0 * * *";
    private static final String CRON_QUARTER_HOUR = "0,15,30,45 * * * *"; //https://crontab.guru/#0,15,30,45_*_*_*_*
    private static final String CRON_ONE_MINUTE = "* * * * *";
    private static final String cacheVersion = "v1.0";
    private static final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss.SSS z");

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

        //get the list of teams, which will refresh once a day at midnight
        thisBot.refreshTeams();

        //Create new scheduler
        Scheduler scheduler = new Scheduler();

        //Schedule tasks to based on cron timing
        scheduler.schedule(CRON_QUARTER_HOUR, thisBot::scheduledStats);
        scheduler.schedule(CRON_ONE_DAY, thisBot::refreshTeams);

        //Start scheduler
        scheduler.start();
    }

    /**
     * data structure for our cached data
     */
    private static class CacheValue {
        int teamID;
        int numGames;
        int numWins;
        int numLosses;
        int numOTLosses;
        boolean statsPublisher;

        public CacheValue(int teamID, int numGames, int numWins, int numLosses, int numOTLosses, boolean statsPublisher) {
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

        @Override
        public boolean equals(Object o) {
            if ( !(o instanceof CacheValue) ) {
                return false;
            } else {
                CacheValue other = (CacheValue) o;
                boolean result = other.getTeamID() == this.getTeamID();
                result &= other.isStatsPublisher() == this.isStatsPublisher();
                return result;
            }
        }

        @Override
        public String toString() {
            return "CacheValue{" +
                    "teamID=" + teamID +
                    ", numGames=" + numGames +
                    ", numWins=" + numWins +
                    ", numLosses=" + numLosses +
                    ", numOTLosses=" + numOTLosses +
                    ", statsPublisher=" + statsPublisher +
                    '}';
        }
    }

    public Bot(JDA instance) {
        this.instance = instance;

        //path to our cache file (starts with a . so hidden file)
        nhl_cache = new File(System.getProperty("user.home")+"/"+".nhl_cache");

        //if the cache exists, we want to read in from it
        if ( nhl_cache.exists() ) {
            //https://github.com/CrystalSpore/POPHockey/blob/main/CACHE_FORMAT.md
            cacheMap = new ConcurrentHashMap<>();
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
                    long readGuildID = Long.parseLong(splitLine[0]);
                    long readChannelID = Long.parseLong(splitLine[1]);

                    if ( !cacheMap.containsKey(readGuildID) ) {
                        cacheMap.put(readGuildID, new ConcurrentHashMap<>());
                    }

                    if ( !cacheMap.get(readGuildID).containsKey(readChannelID) ) {
                        cacheMap.get(readGuildID).put(readChannelID, new CopyOnWriteArrayList<>());
                    }

                    //Don't need add if absent, since reading from cache file
                    cacheMap.get(readGuildID).get(readChannelID).add(
                            new CacheValue(
                                    Integer.parseInt(splitLine[2]),
                                    Integer.parseInt(splitLine[3]),
                                    Integer.parseInt(splitLine[4]),
                                    Integer.parseInt(splitLine[5]),
                                    Integer.parseInt(splitLine[6]),
                                    Boolean.parseBoolean(splitLine[7])
                            ));
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
        } else if (msg.getContentRaw().startsWith("~deleteTeam") && Objects.requireNonNull(msg.getMember()).hasPermission(Permission.ADMINISTRATOR)) {
            deleteTeam(event);
        }
    }

    //ping command, for checking bot latency
    public void ping(MessageReceivedEvent event) {
        TextChannel channel = event.getTextChannel();
        long time = System.currentTimeMillis();
        channel.sendMessage("Pong!") /* => RestAction<Message> */
                .queue(response /* => Message */ -> response.editMessageFormat("Pong: %d ms", System.currentTimeMillis() - time).queue());
    }

    //list all teams that NHL reports back from API
    public void listTeams(MessageReceivedEvent event) {
        TextChannel channel = event.getTextChannel();
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
        TextChannel channel = event.getTextChannel();
        String[] strings = event.getMessage().getContentRaw().split(" ");

        //help block
        if ( strings.length == 1 || strings[1].equalsIgnoreCase("h") || strings[1].equalsIgnoreCase("help") ) {
            MessageBuilder builder = new MessageBuilder();
            builder.appendCodeBlock("Format as > ~setupTeam <teamID>\n\tSetup NHL team listener in current channel.\n\tUse ~listTeams to find the teamID.","");
            Queue<Message> messages = builder.buildAll(MessageBuilder.SplitPolicy.NEWLINE);
            for (Message m : messages) {
                channel.sendMessage(m).queue();
            }
        } else { //setup logic block
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

                if ( !cacheMap.containsKey(guildID) ) {
                    cacheMap.put(guildID, new ConcurrentHashMap<>());
                }

                if ( !cacheMap.get(guildID).containsKey(channelID) ) {
                    cacheMap.get(guildID).put(channelID, new CopyOnWriteArrayList<>());
                }

                CacheValue addValue = new CacheValue(nhlTeamID, numGames, numWins, numLosses, numOTLosses, statsPublisher);
                boolean contains = false;
                CopyOnWriteArrayList<CacheValue> valueList = cacheMap.get(guildID).get(channelID);
                for (CacheValue value : valueList) {
                    if (value.equals(addValue)) {
                        contains = true;
                        break;
                    }
                }

                if (!contains) {
                    cacheMap.get(guildID).get(channelID).add(addValue);
                    channel.sendMessage("Setup \"" + teamStats.getTeamName() + "\" for this channel.").queue();

                    Files.write(Paths.get(nhl_cache.getAbsolutePath()), (guildID + ":" +
                            channelID + ":" +
                            nhlTeamID + ":" +
                            numGames + ":" +
                            numWins + ":" +
                            numLosses + ":" +
                            numOTLosses + ":" +
                            statsPublisher + "\n").getBytes(), StandardOpenOption.APPEND);
                } else {
                    channel.sendMessage("\"" + teamStats.getTeamName() + "\" is already setup for this channel.").queue();
                }
            } catch (IOException e) {
                channel.sendMessage("Error w/ NHL api").queue();
                e.printStackTrace();
            } catch (ParseException e) {
                channel.sendMessage("Error parsing team list").queue();
                e.printStackTrace();
            }
        }
    }

    //remove one of the previously setup teams
    public void deleteTeam(MessageReceivedEvent event) {
        //prepare values for use in method
        TextChannel channel = event.getTextChannel();
        long guildID = event.getGuild().getIdLong();
        long channelID = event.getTextChannel().getIdLong();
        ArrayList<Triple<Integer, String, Boolean>> channelTeams = new ArrayList<>();
        ArrayList<ImmutablePair<Integer, CacheValue>> values = new ArrayList<>();
        CopyOnWriteArrayList<CacheValue> currentChannelList = cacheMap.get(guildID).get(channelID);
        for ( CacheValue value : currentChannelList ) {
            int teamNumber = value.getTeamID();
            channelTeams.add(new Triple<>(teamNumber, teamList.get(teamNumber), value.isStatsPublisher()));
            values.add(new ImmutablePair<>(value.getTeamID(), value));
        }

        //Check if any teams are setup for this channel, exit method if not
        if (channelTeams.size() == 0) {
            channel.sendMessage("There are no teams setup for this channel.").queue();
            return;
        }

        boolean cacheChanged = false;

        String[] msgParts = event.getMessage().getContentRaw().split(" ");
        //help for method
        if ( msgParts.length == 1 || msgParts[1].equalsIgnoreCase("h") || msgParts[1].equalsIgnoreCase("help")) {
            channel.sendMessage("Teams setup for this channel\n{teamNumber : teamName : isStatsPublisher}:\n" + channelTeams.toString().replace(", ", "\n")).queue();
            channel.sendMessage("Please enter command with teamNumber you wish to remove > `~deleteTeam <teamNumber>`").queue();
        //Logic block
        } else {

            //reduce team list for this channel to just team included in argument
            int teamNumber = Integer.parseInt(msgParts[1]);
            channelTeams.removeIf(triple -> triple.getA() != teamNumber);
            values.removeIf(pair -> pair.left != teamNumber);

            //if reduced list is empty, do nothing & exit
            if (channelTeams.size() == 0) {
                channel.sendMessage("Team entered isn't setup for this channel.").queue();
            //if only one type of publisher exists, just remove it
            } else if (channelTeams.size() == 1 && channelTeams.get(0).getA() == teamNumber) {
                currentChannelList.remove(values.get(0).right);
                channel.sendMessage("Removed " + teamList.get(teamNumber) ).queue();
                cacheChanged = true;
            //Prepare reaction response to remove a specific publisher
            } else if (channelTeams.size() > 1) {
                //Send message & add reactions to it
                Message message = channel.sendMessage("React with " +
                        EmojiParser.parseToUnicode(":information_source:") + " for removing stats publisher, or " +
                        EmojiParser.parseToUnicode(":waving_white_flag:") + " for removing win/loss publisher.").complete();
                message.addReaction(EmojiParser.parseToUnicode(":information_source:")).queue();
                message.addReaction(EmojiParser.parseToUnicode(":waving_white_flag:")).queue();

                //Wait 3 seconds
                try {
                    Thread.sleep(3000L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                //get list of users for both reactions (minus bot) & get author of original event
                List<User> usersInfo = message.retrieveReactionUsers(EmojiParser.parseToUnicode(":information_source:")).complete();
                List<User> usersFlag = message.retrieveReactionUsers(EmojiParser.parseToUnicode(":waving_white_flag:")).complete();
                usersInfo.remove(instance.getSelfUser());
                usersFlag.remove(instance.getSelfUser());
                User author = event.getAuthor();

                //Only 1 reaction allowed
                if (usersInfo.contains(author) && usersFlag.contains(author)) {
                    channel.sendMessage("Re-run command & only respond with 1 emote.").queue();
                //Must have a response
                } else if (!usersInfo.contains(author) && !usersFlag.contains(author)) {
                    channel.sendMessage("Response time-out. Please respond within 3 seconds.").queue();
                //Remove stat publisher logic
                } else if ( usersInfo.contains(author) ) {
                    CacheValue removeValue = null;
                    for ( CacheValue value : currentChannelList ) {
                        if (value.isStatsPublisher()) {
                            removeValue = value;
                        }
                    }
                    if (removeValue == null) {
                        channel.sendMessage("Error removing team.").queue();
                    } else {
                        currentChannelList.remove(removeValue);
                        channel.sendMessage("Removed stats publisher for " + teamList.get(teamNumber)).queue();
                        cacheChanged = true;
                    }
                //Remove win/loss publisher logic
                } else if ( usersFlag.contains(author) ) {
                    CacheValue removeValue = null;
                    for ( CacheValue value : currentChannelList ) {
                        if (!value.isStatsPublisher()) {
                            removeValue = value;
                        }
                    }
                    if (removeValue == null) {
                        channel.sendMessage("Error removing team.").queue();
                    } else {
                        currentChannelList.remove(removeValue);
                        channel.sendMessage("Removed Win/Loss publisher for " + teamList.get(teamNumber)).queue();
                        cacheChanged = true;
                    }
                }
            }

            //if cache was updated, we need to rewrite the cache file
            if (cacheChanged) {
                rewriteCacheFile();
            }
        }
    }

    //scheduled task, & sets the list of teams in the HashMap teamList
    public void refreshTeams() {
        try {
            String result = NHLPolling.listTeams();
            teamList = new HashMap<>();
            String[] lines = result.split("\n");
            for (String l : lines) {
                String[] parts = l.split("\t");
                teamList.put(Integer.parseInt(parts[0]), parts[2]); //parts[1] is ":"
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //scheduled task
    public synchronized void scheduledStats() {
        System.out.println("Starting scheduled tasks > " + timeFormat.format(new Date()));
        boolean cacheChanged = false;
        //loop through all entries of cache to check for changes
        for ( Map.Entry<Long,ConcurrentHashMap<Long,CopyOnWriteArrayList<CacheValue>>> cacheSet : cacheMap.entrySet()) {
            ConcurrentHashMap<Long, CopyOnWriteArrayList<CacheValue>> currentGuildMap = cacheSet.getValue();
            for ( Map.Entry<Long,CopyOnWriteArrayList<CacheValue>> e : currentGuildMap.entrySet() ) {
                for ( CacheValue value : e.getValue() ) {
                    //setup values for ease of use later
                    long textChannelID = e.getKey();
                    int teamNumber = value.getTeamID();
                    TextChannel textChannel = instance.getTextChannelById(textChannelID);
                    assert textChannel != null;
                    try {
                        NHLStats teamStats = NHLPolling.teamStats(teamNumber);

                        if (teamStats.getGamesPlayed() == 0 && value.getNumGames() != 0) {
                            textChannel.sendMessage("Start of new season!").queue();
                        }
                        //stats publisher block, being the full stats listing
                        if (teamStats.getGamesPlayed() != value.getNumGames() && value.isStatsPublisher()) {
                            printStats(teamStats, textChannel);
                            updateCacheEntry(value, e.getValue(), teamStats);
                            cacheChanged = true;

                            //simple win/lose block
                        } else if (teamStats.getGamesPlayed() != value.getNumGames() && !value.isStatsPublisher()) {
                            winOrLoss(value, teamStats, textChannel);
                            updateCacheEntry(value, e.getValue(), teamStats);
                            cacheChanged = true;
                        }
                        // print failure, but don't message discord, since this is a scheduled task
                    } catch (IOException | ParseException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }

        if (cacheChanged) {
            rewriteCacheFile();
        }
        System.out.println("Finished with scheduled tasks > " + timeFormat.format(new Date()));
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
    public void winOrLoss(CacheValue value, NHLStats teamStats, TextChannel textChannel) {
        //recorded less than actual, indicates a win or loss
        if ( value.getNumWins() < teamStats.getWins() ) {
            textChannel.sendMessage(teamStats.getTeamName() + " won!").queue();
        }
        else if ( value.getNumLosses() < teamStats.getLosses() ) {
            textChannel.sendMessage(teamStats.getTeamName() + " lost.").queue();
        }
        else if ( value.getNumOTLosses() < teamStats.getOt() ) {
            textChannel.sendMessage(teamStats.getTeamName() + " lost in overtime.").queue();
        }
    }

    //update value in cache
    public void updateCacheEntry(CacheValue value, CopyOnWriteArrayList<CacheValue> list, NHLStats teamStats) {
        list.remove(value);
        list.add( new CacheValue(
                value.getTeamID(), teamStats.getGamesPlayed(),
                teamStats.getWins(), teamStats.getLosses(), teamStats.getOt(), value.isStatsPublisher()
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

            for ( Map.Entry<Long,ConcurrentHashMap<Long,CopyOnWriteArrayList<CacheValue>>> cacheSet : cacheMap.entrySet()) {
                Long guildID = cacheSet.getKey();
                ConcurrentHashMap<Long, CopyOnWriteArrayList<CacheValue>> currentGuildMap = cacheSet.getValue();
                for (Map.Entry<Long, CopyOnWriteArrayList<CacheValue>> e : currentGuildMap.entrySet()) {
                    Long channelID = e.getKey();
                    for ( CacheValue value : e.getValue() ) {
                        Files.write(Paths.get(nhl_cache.getAbsolutePath()), (guildID + ":" +
                                channelID + ":" +
                                value.getTeamID() + ":" +
                                value.getNumGames() + ":" +
                                value.getNumWins() + ":" +
                                value.getNumLosses() + ":" +
                                value.getNumOTLosses() + ":" +
                                value.isStatsPublisher() + "\n").getBytes(), StandardOpenOption.APPEND);
                    }
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
