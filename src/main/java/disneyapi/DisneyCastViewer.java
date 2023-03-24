package disneyapi;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;
import java.util.Scanner;

public class DisneyCastViewer {
    //GOAL: use scanner to prompt the user for a movie
    final static String ROOT_URL = "https://api.disneyapi.dev/character"; //MUST BE STATIC

    //GOAL: return CastDTO from JSON
    public static CastDTO convert(String json){
        ObjectMapper mapper = new ObjectMapper();
        try {
            CastDTO dto = mapper.readValue(json,CastDTO.class);
            return dto;
        } catch (Exception e){
            System.out.println(e.getMessage() + "something has gone wrong.");
            return null;
        }
    }

    public static String getFilm(){
        Scanner scan = new Scanner(System.in);
        System.out.print("Please enter your favorite Disney movie title: ");
        String film = scan.nextLine();
        return film;
    }

    //GOAL: take in a movie name and return a formattedURL
    public static String formatURL(String URL, String movieTitle){
        movieTitle = movieTitle.replaceAll(" ", "%20").replaceAll("'", "%27");
        String bigBoyURL = ROOT_URL + "?films=" + movieTitle;
        return bigBoyURL;
    }

    public static String letsGETit(String URL){
        //setting up the query/call
        HttpClient client = HttpClient.newHttpClient();
        //HttpClient connects us to the internet
        URI uri = URI.create(URL);
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
            //the MAGIC (^-^)
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
    //letsGETit

    //GOAL : take in a list of characters, and return the list of URLS
    public static List<String> grabURL(List<Data> characters, String film){
        //collect URLS from the characters
        ArrayList<String> castURL = new ArrayList<>();
        for (Data c : characters){
            if(c.getFilms().contains(film)){
                String image = c.getImageUrl();
                castURL.add(image);
            }
        }
        return castURL;
    }

    //GOAL : write an HTML file that has all the image URLS
    /* HTMLS
    <html>
        <head> </head>
        <body>
    <img src = "link">
        </body>
    </html>
     */
    public static void writeToFile(List<String> imgURL){
        try {
            FileWriter fw = new FileWriter("index.html");
            BufferedWriter writer = new BufferedWriter(fw);
            writer.write("<html>\n");
            writer.write("<head></head>\n");
            writer.write("<body>\n");
            //TODO: insert image links
            for(int i = 0; i<imgURL.size(); i++){
                String currURL = imgURL.get(i);
                writer.write("<img src=\"");
                writer.write(currURL);
                writer.write("\">\n");

            }

            writer.write("</body>\n");
            writer.write("</html>");

            writer.close();
            //DON'T FORGET TO CLOSE IT
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }

    public static void main(String[] args) {

        String film = getFilm();
        System.out.println(film);

        String URL = formatURL(ROOT_URL, film);
        System.out.println(URL);

        String json = letsGETit(URL);
        System.out.println(json);

        CastDTO dto = convert(json);
        List<Data> characters = dto.getData();


        /*
        for(Data character : characters){
            if (character.getFilms().contains(film)) {
            System.out.println(character.getName());
            }
        }
         */

        writeToFile(grabURL(characters, film)); 


    }
}
