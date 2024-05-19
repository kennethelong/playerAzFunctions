package com.playerazfunctions;

import java.util.List;

public interface StorageInterface {

    String initialize();

    PlayerRecord getPlayerByID(String playerID);

    void updatePlayerRecord(PlayerRecord record);

    List<PlayerRecord> computeListOfPlayersAt(String positionString);
    
}
