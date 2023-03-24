package org.example;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class JSONListPractice {
    public static void main(String[] args) {
        //GOAL : turn the list of cereals into a list of
        //CerealDTOs

        /*
        SETUP:
        1. get the JSON
        2. make the object mapper
        PROCESS:
        3. read the json + map to DTO
        AFTER:
        4. play with the object
         */

        File jsonFile = new File("ListOfCereals.json");
        ObjectMapper mapper = new ObjectMapper();
        TypeReference<List<CerealDTO_Generated>> dataType = new TypeReference<>() {
        };
        //<what type of data do you want>

        try {
            List<CerealDTO_Generated> cereals = mapper.readValue(jsonFile, dataType);
            for (CerealDTO_Generated cereal : cereals) {
                System.out.println(cereal.getName());
            }
            //converting the DTO BACK to json
            CerealDTO_Generated grapeNuts = cereals.get(2);
            mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
            String grapeJSON = mapper.writeValueAsString(grapeNuts);
            System.out.println(grapeJSON);
            String JSONPUTString = "{\"name\":\"test\",\"salary\":\"123\",\"age\":\"23\"}";
            String response = makePUTRequest("https://dummy.restapiexample.com/api/v1/update/21", JSONPUTString);
            System.out.println(response);

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        //PUT requests
        //have a different link than get requests
        //taking data in a JSON and putting it in a server
    }

    public static String makePUTRequest(String url, String requestBody) {
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create(url);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();
        try {
            HttpResponse<String> httpResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
            int statusCode = httpResponse.statusCode();
            if (statusCode == 201 || statusCode == 202 || statusCode == 200) {
                return httpResponse.body();
            } else {
                return String.format("GET request failed: %d status code received", statusCode);
            }
        } catch (IOException | InterruptedException e) {
            return e.getMessage();
        }
    }
}
