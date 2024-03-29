package com.shnok.javaserver;

import com.shnok.javaserver.model.Point3D;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.util.Properties;

public class Config {
    public static final String CONFIG_FILE = "server.properties";
    public static int GAMESERVER_PORT;
    public static int CONNECTION_TIMEOUT_SEC;
    public static int TIME_TICKS_PER_SECOND;
    public static int TIME_DAY_DURATION_MINUTES;
    public static boolean PRINT_SERVER_PACKETS_LOGS;
    public static boolean PRINT_PATHFINDER_LOGS;
    public static boolean PRINT_CLIENT_PACKETS_LOGS;
    public static boolean PRINT_WORLDREGION_LOGS;
    public static boolean PRINT_KNOWN_LIST_LOGS;
    public static boolean PRINT_AI_LOGS;
    public static boolean SPAWN_NPCS;
    public static boolean SPAWN_MONSTERS;
    public static boolean SPAWN_DEBUG;
    public static boolean AI_PATROL;
    public static int AI_LOOP_RATE_MS;
    public static float AI_PATROL_CHANCE;
    public static Point3D PLAYER_SPAWN_POINT;
    public static String[] ZONES_TO_LOAD;
    public static boolean PATHFINDER_ENABLED;
    public static boolean PATHFINDER_SIMPLIFY_PATH;
    public static boolean KEEP_AI_ALIVE;
    public static boolean GEODATA_ENABLED;
    public static int GEODATA_MAXIMUM_Y_ERROR;
    public static float GEODATA_NODE_SIZE;
    public static int GEODATA_MAP_SIZE;
    public static int GEODATA_MAXIMUM_LAYERS;
    public static boolean SPECIFIC_CHARACTER;
    public static int SPECIFIC_CHARACTER_ID;

    public static final int MONEY_ID = 57;

    public static void LoadSettings() throws Exception {
        ClassLoader classLoader = Main.class.getClassLoader();
        URL resourceUrl = classLoader.getResource(CONFIG_FILE);

        Properties optionsSettings = new Properties();
        InputStream is = Files.newInputStream(new File(resourceUrl.getPath()).toPath());
        optionsSettings.load(is);
        is.close();

        GAMESERVER_PORT = Integer.parseInt(optionsSettings.getProperty("gameserver.port", "8000"));
        CONNECTION_TIMEOUT_SEC = Integer.parseInt(optionsSettings.getProperty("server.connection.timeout.ms", "10"));

        TIME_TICKS_PER_SECOND = Integer.parseInt(optionsSettings.getProperty("server.time.ticks-per-second", "10"));
        TIME_DAY_DURATION_MINUTES = Integer.parseInt(optionsSettings.getProperty("server.time.day.duration.minutes", "10"));

        float spawnX = Float.parseFloat(optionsSettings.getProperty("server.spawn.location.x", "0"));
        float spawnY = Float.parseFloat(optionsSettings.getProperty("server.spawn.location.y", "0"));
        float spawnZ = Float.parseFloat(optionsSettings.getProperty("server.spawn.location.z", "0"));
        SPAWN_NPCS = Boolean.parseBoolean(optionsSettings.getProperty("server.world.npc.spawn-npcs", "true"));
        SPAWN_MONSTERS = Boolean.parseBoolean(optionsSettings.getProperty("server.world.npc.spawn-monsters", "true"));
        SPAWN_DEBUG = Boolean.parseBoolean(optionsSettings.getProperty("server.world.npc.spawn-debug", "false"));

        AI_LOOP_RATE_MS = Integer.parseInt(optionsSettings.getProperty("server.ai.loop-rate-ms", "1000"));
        AI_PATROL = Boolean.parseBoolean(optionsSettings.getProperty("server.world.ai.monsters.patrol", "true"));
        AI_PATROL_CHANCE = Float.parseFloat(optionsSettings.getProperty("server.ai.monsters.patrol-chance", "10"));
        KEEP_AI_ALIVE = Boolean.parseBoolean(optionsSettings.getProperty("server.world.ai.keep-alive", "true"));

        PLAYER_SPAWN_POINT = new Point3D(spawnX, spawnY, spawnZ);

        PRINT_SERVER_PACKETS_LOGS = Boolean.parseBoolean(optionsSettings.getProperty("logger.print.server-packets", "false"));
        PRINT_CLIENT_PACKETS_LOGS = Boolean.parseBoolean(optionsSettings.getProperty("logger.print.client-packets", "false"));
        PRINT_PATHFINDER_LOGS = Boolean.parseBoolean(optionsSettings.getProperty("logger.print.pathfinder", "false"));
        PRINT_WORLDREGION_LOGS = Boolean.parseBoolean(optionsSettings.getProperty("logger.print.world-region", "false"));
        PRINT_KNOWN_LIST_LOGS = Boolean.parseBoolean(optionsSettings.getProperty("logger.print.known-list", "false"));
        PRINT_AI_LOGS = Boolean.parseBoolean(optionsSettings.getProperty("logger.print.ai", "false"));

        String zoneList = optionsSettings.getProperty("server.world.geodata.zones.load", "");
        ZONES_TO_LOAD = zoneList.toUpperCase().split(",");

        GEODATA_ENABLED = Boolean.parseBoolean(optionsSettings.getProperty("server.world.geodata.enabled", "true"));
        GEODATA_MAXIMUM_Y_ERROR = Integer.parseInt(optionsSettings.getProperty("server.world.geodata.maximum-y-error", "5"));
        GEODATA_NODE_SIZE = Float.parseFloat(optionsSettings.getProperty("server.world.geodata.node-size", "1"));
        GEODATA_MAP_SIZE = Integer.parseInt(optionsSettings.getProperty("server.world.geodata.map-size", "625"));
        GEODATA_MAXIMUM_LAYERS = Integer.parseInt(optionsSettings.getProperty("server.world.geodata.maximum-layers", "5"));
        PATHFINDER_ENABLED = Boolean.parseBoolean(optionsSettings.getProperty("server.world.geodata.pathfinder.enabled", "false"));
        PATHFINDER_SIMPLIFY_PATH = Boolean.parseBoolean(optionsSettings.getProperty("server.world.geodata.pathfinder.simplify-path", "false"));

        SPECIFIC_CHARACTER = Boolean.parseBoolean(optionsSettings.getProperty("server.world.player.specific-character", "false"));
        SPECIFIC_CHARACTER_ID = Integer.parseInt(optionsSettings.getProperty("server.world.player.specific-character-id", "5"));
    }
}
