package com.playerazfunctions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class InMemoryStorage implements StorageInterface{

        private Map<String, PlayerRecord> fakePlayerData = new HashMap<>();

        private Map<String, String> playersInRoom = new HashMap<>();

    @Override
    public String initialize() {

        System.out.println("I'm being initialized");


        updatePlayerRecord(new PlayerRecord("231", "Psyck", "Ops", "ODENSE", "(0,0,0)", "yes"));
        updatePlayerRecord(new PlayerRecord("456", "MainTakingOver", "Ops", "ODENSE", "(0,0,0)", "yes"));
        updatePlayerRecord(new PlayerRecord("879", "ObliviousNarc", "Tia2", "ODENSE", "(0,0,0)", "yes"));
        updatePlayerRecord(new PlayerRecord("725", "Fam1lyaGround", "Tia2", "ODENSE", "(0,0,0)", "yes"));

        return "ok";
    }

    @Override
    public PlayerRecord getPlayerByID(String playerID) {

        System.out.println("You are inside the getPlayerId method");
        if (!fakePlayerData.containsKey(playerID)) {
            //logger.error("Cannot GET player " + playerID + " as it does not exist"); //Logger cannot easily be added - to be fixed
            System.out.println("Returning null playerRecord");
            return null;
        }
        return fakePlayerData.get(playerID);

    }

    @Override
    public void updatePlayerRecord(PlayerRecord record) {
        String playerId = record.getPlayerID();

        PlayerRecord playerBeforeUpdate = fakePlayerData.get(playerId);

        // Update or add the player record
        fakePlayerData.put(playerId, record);

        // Update PLAYERS_IN_ROOM last position and new position
        if (playerBeforeUpdate != null && playerBeforeUpdate.isInCave()) {
            playersInRoom.remove(playerBeforeUpdate.getPositionAsString(), playerId);
        }
        if (record.isInCave()) {
            playersInRoom.put(record.getPositionAsString(), playerId);
        }
    }

    @Override
    public List<PlayerRecord> computeListOfPlayersAt(String positionString) {
        List<PlayerRecord> playerList = new ArrayList<>();
        playersInRoom.forEach((position, playerId) -> {
            if (position.equals(positionString)) {
                PlayerRecord player = fakePlayerData.get(playerId);
                playerList.add(player);
            }
        });

        return playerList;
    }
    
}
