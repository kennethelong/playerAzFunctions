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

import java.util.Optional;

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
     PlayerRecord updatePR = new PlayerRecord("231", "Psyck", "Ops", "ODENSE", "(0,0,0)", "yes");

     String returnedString = fakeDB.initialize();

     @FunctionName("HttpTrigger-Java")
     public HttpResponseMessage run(
             @HttpTrigger(name = "req", 
             methods = {HttpMethod.GET, HttpMethod.POST}, 
             route="{customRoute}", 
             authLevel = AuthorizationLevel.FUNCTION) HttpRequestMessage<Optional<String>> request,
             @BindingName("customRoute")String route,
             final ExecutionContext context) {
         context.getLogger().info("Java HTTP trigger processed a request.");
     
         if(route.equals("home")){
             return request.createResponseBuilder(HttpStatus.OK).body("Home route request").build();
         }
         else if(route.equals("id")){
             return request.createResponseBuilder(HttpStatus.OK).body("Id route request").build();
         }
         else{
             return request.createResponseBuilder(HttpStatus.NOT_FOUND).body("Not a valid route").build();
         }
     }

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
            return request.createResponseBuilder(HttpStatus.OK).body("Player with: " + id + " does not exist").build();
        }    
        return request.createResponseBuilder(HttpStatus.OK).body("ID: " + id + " PlayerRecord is: " + pr.toString()).build();
    }

        //Update or add player
        @FunctionName("updatePlayer")
        public HttpResponseMessage updatePlayer(
                @HttpTrigger(name = "req", 
                methods = {HttpMethod.POST}, 
                route="v1/players", 
                authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request,
                final ExecutionContext context) {

                String name = request.getBody().orElseGet(() -> request.getQueryParameters().get("name"));

 
            return request.createResponseBuilder(HttpStatus.OK).body("ID: PlayerRecord is: " + name).build();
        }

        @FunctionName("test")
        public HttpResponseMessage hello(
            @HttpTrigger(name = "req", 
            methods = {HttpMethod.POST},
            route="v1/players/test/",  
            authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context)  {

        context.getLogger().info("\nRequest body is: " + request.getBody().orElse(null) + "\n");
        /*JsonCon
        String name = request.getQueryParameters().getOrDefault("name", "");
        context.getLogger().info("\nName is: " + name + "\n");*/

    
        return request.createResponseBuilder(HttpStatus.OK).body("\n").build();
    
    }
}

