package eatAWESOME.pixelmonspawnalerts.utils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SpawnAlertTracker {

    private static final Map<UUID, String> spawnAlerts = new HashMap<>();

    public static void updateSpawnAlert(UUID playerId, String targetName) {
        spawnAlerts.put(playerId, targetName);
    }

    public static void removeSpawnAlert(UUID playerId) {
        spawnAlerts.remove(playerId);
    }

    public static String getSpawnAlert(UUID playerId) {
        return spawnAlerts.get(playerId);
    }

    public static Map<UUID, String> getAllSpawnAlerts() {
        return Collections.unmodifiableMap(spawnAlerts);
    }
}