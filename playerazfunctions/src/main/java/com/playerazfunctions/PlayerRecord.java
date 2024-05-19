package com.playerazfunctions;

import com.google.gson.JsonObject;

/**
 * This is a record type (struct / PODO (Plain Old Data Object)) representing
 * the core data of a player like name, id, position, etc.
 * <p>
 * A record is a pure data object without any behavior, suitable for
 * networking and persistence as it only contains data.
 *
 * @author Henrik Baerbak Christensen, Aarhus University.
 */

public class PlayerRecord {
    private String playerID;
    private String playerName;
    private String groupName;
    private String region;
    private String positionAsString;
    private String accessToken;

    public PlayerRecord(String playerID, String playerName, String groupName, String region, String positionString, String accessTokenIsInCave) {
        this.playerID = playerID;
        this.playerName = playerName;
        this.groupName = groupName;
        this.region = region;
        this.positionAsString = positionString;
        this.accessToken = accessTokenIsInCave;
    }

    public PlayerRecord(JsonObject playerJsonObject) {
        this.playerID = playerJsonObject.get("playerID").getAsString();
        this.playerName = playerJsonObject.get("playerName").getAsString();
        this.groupName = playerJsonObject.get("groupName").getAsString();
        this.region = playerJsonObject.get("region").getAsString();
        this.positionAsString = playerJsonObject.get("positionAsString").getAsString();
        this.accessToken = playerJsonObject.get("accessToken").getAsString();
    }

    public String getPlayerID() {
        return playerID;
    }
    public String getPlayerName() {
        return playerName;
    }
    public String getGroupName() {
        return groupName;
    }
    public String getPositionAsString() {
        return positionAsString;
    }
    public String getRegion() {
        return region;
    }
    /**
     * get the player's access token from the OAuth
     * authorization; if it is null
     * then the player is not presently in the cave
     * @return the access token or null in case
     * no session exists for the given player
     */
    public String getAccessToken() {
        return accessToken;
    }
    public boolean isInCave() {
        return accessToken != null;
    }

    public void setPositionAsString(String positionAsString) {
        this.positionAsString = positionAsString;
    }
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((playerID == null) ? 0 : playerID.hashCode());
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        PlayerRecord other = (PlayerRecord) obj;
        if (playerID == null) {
            if (other.playerID != null)
                return false;
        } else if (!playerID.equals(other.playerID))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "PlayerRecord [playerID=" + playerID + ", playerName=" + playerName
                + ", groupName=" + groupName + ", region=" + region
                + ", positionAsString=" + positionAsString + ", accessToken=" + accessToken
                + "]";
    }
}
