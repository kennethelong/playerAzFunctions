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

//import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;


/**
 * Azure Functions with HTTP Trigger.
 */
public class Function {
    /**
     * This function listens at endpoint "/api/HttpExample". Two ways to invoke it using "curl" command in bash:
     * 1. curl -d "HTTP Body" {your host}/api/HttpExample
     * 2. curl "{your host}/api/HttpExample?name=HTTP%20Query"
     */

     private static StorageInterface fakeDB = new InMemoryStorage();
     String returnedString = fakeDB.initialize();

    //get a player by player id
    @FunctionName("getPlayerById")
    public HttpResponseMessage getPlayerById(
            @HttpTrigger(name = "req", 
            methods = {HttpMethod.GET}, 
            route="v1/players/{id}", 
            authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request,
            @BindingName("id")String id,
            final ExecutionContext context) {
        
        //Get the player with the inputted ID
        PlayerRecord pr = fakeDB.getPlayerByID(id);

        //If there is no player with the ID
        if(pr == null){
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body("Player with: " + id + " does not exist").build();
        }
        
        JsonObject playerAsJson = new JsonObject();
        playerAsJson.addProperty("playerID", pr.getPlayerID());
        playerAsJson.addProperty("playerName", pr.getPlayerName());
        playerAsJson.addProperty("groupName", pr.getGroupName());
        playerAsJson.addProperty("region", pr.getRegion());
        playerAsJson.addProperty("positionAsString", pr.getPositionAsString());
        playerAsJson.addProperty("accessToken", pr.getAccessToken());

        // Set the response status and send the response
        return request.createResponseBuilder(HttpStatus.OK).body(playerAsJson.toString()).build();
    }

    //Update or add player
    @FunctionName("updatePlayer")
    public HttpResponseMessage updatePlayer(
        @HttpTrigger(name = "req", 
        methods = {HttpMethod.POST},
        route="v1/players",  
        authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request,
        final ExecutionContext context)  {

        String tempString = request.getBody().orElse(null);

        context.getLogger().info("\nRequest body is: " + tempString);
     
        JsonObject playerJsonObject = JsonParser.parseString(tempString).getAsJsonObject();
        PlayerRecord player = new PlayerRecord(playerJsonObject);

        // Ensure that the player data is valid
        if (player.getPlayerID() == null || player.getPlayerName() == null ||
                player.getGroupName() == null || player.getRegion() == null ||
                player.getPositionAsString() == null) {
                return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body("\n").build();
            }

        fakeDB.updatePlayerRecord(player);
        System.out.println("Player was added");

        return request.createResponseBuilder(HttpStatus.OK).body("\n").build();    
    }

    //xx
    @FunctionName("getPlayersAtPosition")
    public HttpResponseMessage getPlayersAtPosition(
        @HttpTrigger(name = "req", 
        methods = {HttpMethod.GET}, 
        route="v1/players/position/{pos}", 
        authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request,
        @BindingName("pos")String pos,
        final ExecutionContext context) {

            System.out.println("Inside the getPlayersAtPosition method");

            if (!validatePositionFormat(pos)){
                return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body("\n").build();
            }

            List<PlayerRecord> players = fakeDB.computeListOfPlayersAt(pos);

            String returnValue = formatToJson(players);
            System.out.println("Returned value is: "+ returnValue);
            if(returnValue.equals("{\"players\":[]}")){
                System.out.println("No player are in that room");
                return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body("\n").build();
            }

            // Set the response status and send the response     
            return request.createResponseBuilder(HttpStatus.OK).body(returnValue).build();
    }

    private boolean validatePositionFormat(String positionString) {
        // Check if the input is already in the desired format (0,0,0)
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

