package com.playerazfunctions;

import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.BindingName;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;
import java.util.List;
import java.util.Optional;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;


/**
 * These Azure Functions are a rebuild of a mincroservice named player microservice, which was carved out from the monolithic application SkyCaveOpen.
 * The purpose of the microservices was to refactor the SkyCaveOpen application into three microservices, the player microservice being one of them,
 * as part of a educational project to get comfortable building microservices.
 * 
 * This project has opted to rebuild the player microservices using Azure Functions. It exposes the same three endpoint as the orginal microservice 1)
 * get a player, 2) update or add a player and 3) get all players at a certain position.
 */
public class Function {

     private static StorageInterface fakeDB = new InMemoryStorage();
     String returnString = fakeDB.initialize();
     private Gson gson;
     
     public Function(){
        gson = new GsonBuilder().serializeNulls().create();
        System.out.println("Version 3");
     }

    // Get a player by player id
    @FunctionName("getPlayerById")
    public HttpResponseMessage getPlayerById(
            @HttpTrigger(name = "req", 
            methods = {HttpMethod.GET, HttpMethod.POST}, 
            route="v1/players/{id}", 
            authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request,
            @BindingName("id")String id,
            final ExecutionContext context) {
        
                // Get the player with the inputted ID
                PlayerRecord pr = fakeDB.getPlayerByID(id);

                //System.out.println("I'm in the getPlayerById method and got this playerRecord: " + pr);


                // If there is no player with the ID a not_found reponse is returned
                if(pr == null){
                    return request.createResponseBuilder(HttpStatus.NOT_FOUND).body("").build();
                }

                // Converts the JSON to a string
                String playerAsJson = gson.toJson(pr);

                //String playerAsJson;
                try {
                    playerAsJson = gson.toJson(pr);
                } catch (Exception e) {
                    return request.createResponseBuilder(HttpStatus.BAD_REQUEST).build();
                }

                // Set the response status and send the response including the player as JSON
                return request.createResponseBuilder(HttpStatus.OK).body(playerAsJson).build();
            }

    // Update or add player
    @FunctionName("addOrUpdatePlayer")
    public HttpResponseMessage addOrUpdatePlayer(
        @HttpTrigger(name = "req", 
        methods = {HttpMethod.GET, HttpMethod.POST, HttpMethod.PUT},
        route="v1/players",  
        authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request,
        final ExecutionContext context)  {

            // Extracts the request as a String
            String requestBodyString = request.getBody().orElse(null);
            
            // Convert the string in a player record
            JsonObject playerJsonObject;
            PlayerRecord player;
            try {
                playerJsonObject = JsonParser.parseString(requestBodyString).getAsJsonObject();
                player = gson.fromJson(playerJsonObject, PlayerRecord.class);
            } catch (Exception e) {
                return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body("Could not convert player to json").build();
            }

            // Ensure that the player data is valid
            if (!validatePlayerRecord(player)) {
                return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body("\n").build();
            }                

            // Adding hte player to the database
            fakeDB.updatePlayerRecord(player);
            
            return request.createResponseBuilder(HttpStatus.OK).build();    
    }



    // Get all players at a certain position
    @FunctionName("getPlayersAtPosition")
    public HttpResponseMessage getPlayersAtPosition(
        @HttpTrigger(name = "req", 
        methods = {HttpMethod.GET}, 
        route="v1/players/position/{pos}", 
        authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request,
        @BindingName("pos")String pos,
        final ExecutionContext context) {

            // Validate the position input string is correctly formatted
            if (!validatePositionFormat(pos)){
                return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body("\n").build();
            }

            // Requests the storage for all players at a certain position
            List<PlayerRecord> players = fakeDB.computeListOfPlayersAt(pos);

            String returnValue = formatToJson(players);
            System.out.println("Returned value is: "+ returnValue);
            
            // Check is there are any players in the room
            if(returnValue.equals("{\"players\":[]}")){
                //System.out.println("No player are in that room");
                return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body("\n").build();
            }

            // Set the response status and send a string with the players
            return request.createResponseBuilder(HttpStatus.OK).body(returnValue).build();
    }

    /** Helper methods below*/

    // Method to verify that the player record contains information
    private boolean validatePlayerRecord(PlayerRecord pr) {
        return
            pr.getPlayerID() != null &&
            pr.getPlayerName() != null &&
            pr.getGroupName() != null &&
            pr.getPositionAsString() != null &&
            pr.getRegion() != null;
    }

    // Method to validate the position in correctly formatted
    private boolean validatePositionFormat(String positionString) {
        // Check if the input is already in the desired format (int,int,int)
        if (positionString.startsWith("(") && positionString.endsWith(")")) {
            // Remove parentheses
            String content = positionString.substring(1, positionString.length() - 1);

            // Split by comma and check if each part is a number
            String[] parts = content.split(",");
            for (String part : parts) {
                try {
                    Integer.parseInt(part);
                } catch (NumberFormatException e) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    private String formatToJson(List<PlayerRecord> playerRecords) {
        // Create a JSONObject to represent the final JSON structure
        JsonObject jsonObject = new JsonObject();

        // Create a JSONArray to represent the "players" array
        JsonArray playersArray = new JsonArray();

        // Iterate through the list of PlayerRecords and create JSON objects for each player
        for (PlayerRecord playerRecord : playerRecords) {
            JsonObject playerObject = new JsonObject();
            playerObject.addProperty("playerID", playerRecord.getPlayerID());
            playerObject.addProperty("playerName", playerRecord.getPlayerName());
            playersArray.add(playerObject);
        }

        // Add the "players" array to the main JSONObject
        jsonObject.add("players", playersArray);

        // Convert the JSONObject to a JSON string
        return jsonObject.toString();
    }
    
}

