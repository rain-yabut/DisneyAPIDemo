package org.example;

import java.io.IOException;
import java.util.Scanner;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class APIConnector {

    //GOAL: write a method that takes in a JSON string and returns a BabyTVDTO
    public static BabyTVDTO convertBaby(String jsonString){
        ObjectMapper mapper = new ObjectMapper();
        try {
            BabyTVDTO dto = mapper.readValue(jsonString, BabyTVDTO.class);
            return dto;

        } catch (Exception e){
            System.out.println(e.getMessage());
            return null;
        }
    }

    public static String makeGETRequest(String url){
        //setting up the query/call
        HttpClient client = HttpClient.newHttpClient();
            //HttpClient connects us to the internet
        URI uri = URI.create(url);
            //URI is an umbrella term for URL
        HttpRequest request = HttpRequest.newBuilder()
                //this object holds the data about the query
                .uri(uri)
                    //holds the URI
                .header("Accept", "application/json")
                    //holds the meta-data(information about data)
                    //saying you want it in JSON and you want to accept it
                .GET()
                    //getting information; receiving stuff on the internet
                .build();
        try {
            HttpResponse<String> httpResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
                //the MAGIC
            int statusCode = httpResponse.statusCode();
                //a secret code that tells you how it went
                //in general 200s are good; 400-500s are bad
            if (statusCode == 200) { //if everything went OK
                return httpResponse.body();
            } else {
                // String.format is fun! Worth a Google if you're interested
                return String.format("GET request failed: %d status code received", statusCode);
            }
        } catch (IOException | InterruptedException e) {
            return e.getMessage();
        }
    }

    public static void main(String[] args) {
        //GOAL #1 : make a dynamic URL
        /*
        use scanner to read in a TV show
        then build the URL for that show
         */
        //https://api.tvmaze.com/singlesearch/shows?q=cutthroat%20kitchen

        final String ROOT_URL = "api.tvmaze.com";
        final String PROTOCOL = "https://";
        String path = "/singlesearch/shows";

        //3 steps for userInput
        /*
        0. import scanner
        1. initialize scanner
        2. prompt the user
        3. receive input
         */
        Scanner scan = new Scanner(System.in);
        System.out.print("Enter the name of a tv show: ");
        String tvShowInput = scan.nextLine();

        String queryValue = tvShowInput.replaceAll(" ","%20");
        queryValue = queryValue.toLowerCase();

        String URL = PROTOCOL + ROOT_URL + path + "?q=" + queryValue;
        //DON'T FORGET ABOUT "?q="!!!!
        System.out.println(URL);

        String jsonResponse = makeGETRequest(URL);
        System.out.println(jsonResponse);

        BabyTVDTO DTO = convertBaby(jsonResponse);
        System.out.println("\n\n-~-~-~-~-~-~-~-~-~-~");
        System.out.println("Show Name: " + DTO.getName());
        System.out.println("Show Language: " + DTO.getLanguage());
        System.out.println("Show Summary: " + DTO.getSummary());


    }
}
