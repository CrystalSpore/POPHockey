package com.snreloaded;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class CacheValue {

    public static ArrayList<String> priorTeams = new ArrayList<>();

    private static final String cacheVersion = "v2.0";
    private int teamID;
    private int numGames;
    private int numWins;
    private int numLosses;
    private int numOTLosses;
    private boolean statsPublisher;

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
        return "CacheValue { " +
                "teamID = " + teamID +
                ", numGames = " + numGames +
                ", numWins = " + numWins +
                ", numLosses = " + numLosses +
                ", numOTLosses = " + numOTLosses +
                ", statsPublisher = " + statsPublisher +
                "}";
    }


    public static ConcurrentHashMap<Long, ConcurrentHashMap<Long, CopyOnWriteArrayList<CacheValue>>> parseCacheFile(File nhl_cache) {
        //https://github.com/CrystalSpore/POPHockey/blob/main/CACHE_FORMAT.md
        ConcurrentHashMap<Long, ConcurrentHashMap<Long, CopyOnWriteArrayList<CacheValue>>> cacheMap = new ConcurrentHashMap<>();
        Scanner fin = null;
        try {
            fin = new Scanner(new FileInputStream(nhl_cache));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        String cacheFileCurrentVersion = "";
        if (fin != null) {
            if ( fin.hasNext() ) {
                cacheFileCurrentVersion = fin.nextLine();
                if ( !cacheFileCurrentVersion.equals(cacheVersion) && !cacheFileCurrentVersion.equals("v1.0") ) {
                    System.err.println("Cache needs to be updated. " +
                            "Please run update script or manually update based on spec. (Current version = " + cacheVersion + ")");
                    System.exit(0);
                    return null;
                }
            }
            while ( fin.hasNext() ) {
                String line = fin.nextLine();
                if (line.charAt(0) == '>')
                {
                    priorTeams.add(line.substring(1));
                } else {
                    String[] splitLine = line.split(":");

                    //if there isn't enough for key & value pair, then exit
                    if (splitLine.length != 8) {
                        System.err.println("Error reading cache, please verify the cache matches spec...");
                        System.err.println("\tManual modification of cache may be necessary. " +
                                "You most likely don't want to modify the first 2 values (Guild & Channel IDs).");
                        System.exit(0);
                        return null;
                    }
                    long readGuildID = Long.parseLong(splitLine[0]);
                    long readChannelID = Long.parseLong(splitLine[1]);

                    if (!cacheMap.containsKey(readGuildID)) {
                        cacheMap.put(readGuildID, new ConcurrentHashMap<>());
                    }

                    if (!cacheMap.get(readGuildID).containsKey(readChannelID)) {
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
            if ( cacheFileCurrentVersion.equals("v1.0") /*&& cacheVersion == "v2.0"*/ ) {
                rewriteCacheFile(cacheMap, nhl_cache);
            }
        }
        return cacheMap;
    }

    public static void rewriteCacheFile(ConcurrentHashMap<Long, ConcurrentHashMap<Long, CopyOnWriteArrayList<CacheValue>>> cacheMap, File nhl_cache) {
        try {
            // delete & remake before writing
            nhl_cache.delete();
            nhl_cache.createNewFile();
            // write cache version
            Files.write(Paths.get(nhl_cache.getAbsolutePath()), (cacheVersion + "\n").getBytes(), StandardOpenOption.APPEND);

            // write out the prior teams to be excluded
            for ( String priorTeam : priorTeams ) {
                Files.write(Paths.get(nhl_cache.getAbsolutePath()), (">" + priorTeam + "\n").getBytes(), StandardOpenOption.APPEND);
            }

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

    public static File openNHL_Cache(ConcurrentHashMap<Long, ConcurrentHashMap<Long, CopyOnWriteArrayList<CacheValue>>> cacheMap) {
        String cache_pathname = "";
        //if OS is Windows
        if (System.getProperty("os.name").toLowerCase().contains("win")) {
            cache_pathname = System.getProperty("user.home") + "/POPHockey/nhl_cache";
            //if OS is not Windows
        } else {
            cache_pathname = System.getProperty("user.home") + "/.config/POPHockey/nhl_cache";
        }

        //path to our cache file (starts with a . so hidden file)
        File nhl_cache = new File(cache_pathname);

        //if the cache file exists, we want to read in from it
        if (nhl_cache.exists()) {
            cacheMap = CacheValue.parseCacheFile(nhl_cache);
            //if the old cache exists, try to move, then read the cache in
        } else if ( new File(System.getProperty("user.home") + "/.nhl_cache").exists() ) {
            //create directories for where new cache file lives
            //if OS is Windows
            if (System.getProperty("os.name").toLowerCase().contains("win")) {
                new File( System.getProperty("user.home") + "/POPHockey/").mkdirs();
                //if OS is not Windows
            } else {
                new File(System.getProperty("user.home") + "/.config/POPHockey/").mkdirs();
            }

            //move cache file
            boolean moveSuccess = new File(System.getProperty("user.home") + "/.nhl_cache").renameTo(nhl_cache);
            if ( !moveSuccess ) {
                System.out.println("Error when moving cache file. Try again or manually move file.");
                System.exit(-1);
            }
            cacheMap = CacheValue.parseCacheFile(nhl_cache);
        } else { // otherwise create the file
            try {
                //ignoring output due to checking existence above
                nhl_cache.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return nhl_cache;
    }

}
