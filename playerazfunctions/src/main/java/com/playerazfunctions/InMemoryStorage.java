package com.playerazfunctions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class InMemoryStorage implements StorageInterface{

        private static Map<String, PlayerRecord> fakePlayerData = new HashMap<>();

        private static Map<String, String> playersInRoom = new HashMap<>();

    @Override
    public String initialize() {    


        updatePlayerRecord(new PlayerRecord("231", "Psyck", "Ops", "ODENSE", "(0,0,0)", "yes"));
        updatePlayerRecord(new PlayerRecord("456", "MainTakingOver", "Ops", "ODENSE", "(0,0,0)", "yes"));
        updatePlayerRecord(new PlayerRecord("879", "ObliviousNarc", "Tia2", "ODENSE", "(0,1,0)", "yes"));
        updatePlayerRecord(new PlayerRecord("725", "Fam1lyaGround", "Tia2", "ODENSE", "(0,0,0)", "yes"));

        System.out.println("I'm being initialized. The hashmap is this long: " + fakePlayerData.size());

        return "ok";
    }

    @Override
    public PlayerRecord getPlayerByID(String playerID) {

        System.out.println("You are inside the getPlayerId method");
        //Return a null if the player does not exist
        if (!fakePlayerData.containsKey(playerID)) {
            System.out.println("Returning null playerRecord");
            return null;
        }
        return fakePlayerData.get(playerID);
    }

    @Override
    public void updatePlayerRecord(PlayerRecord record) {
        //String playerId = record.getPlayerID();

        //PlayerRecord playerBeforeUpdate = fakePlayerData.get(playerId);

        // Update or add the player record
        fakePlayerData.put(record.getPlayerID(), record);
        PlayerRecord tempPR = fakePlayerData.get(record.getPlayerID());
        System.out.println("I've inserted this record in the hashmap: " + tempPR);

        // Update PLAYERS_IN_ROOM last position and new position
        /*if (playerBeforeUpdate != null && playerBeforeUpdate.isInCave()) {
            playersInRoom.remove(playerBeforeUpdate.getPositionAsString(), playerId);
        }
        if (record.isInCave()) {
            System.out.println("adding to PlayersInRoom, player: " + playerId + " at potision: " + record.getPositionAsString());
            playersInRoom.put(record.getPositionAsString(), playerId);
        }*/
    }

    @Override
    public List<PlayerRecord> computeListOfPlayersAt(String positionString) {
        List<PlayerRecord> playerList = new ArrayList<>();    

        for (String id : fakePlayerData.keySet()) {
            PlayerRecord ps = fakePlayerData.get(id);
            if (ps.isInCave() && ps.getPositionAsString().equals(positionString)) {
                playerList.add(ps);
            }
        }

        return playerList;
    }
    
}
