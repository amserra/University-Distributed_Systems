import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.simple.JSONObject;

public class Yandex {

    public static void main(String[] args) {
        /*
         *  The main function just loops asking input from the user.
         *
         *  tiles
         *  	=> Lists the positions and colors of all tiles that are not empty (have been created)
         *
         *  dot x y r g b
         *  	=> Paints a dot in position (x, y) with the color (r, g, b)
         *
         *  line x y direction length r g b
         *  	=> draws a line of 'length' starting in point (x, y), with direction as 'dir' (RIGHT, LEFT, UP, DOWN) and color (r, g, b)
         *
         */

        Yandex yandex = new Yandex();

        BufferedReader in = new BufferedReader(new InputStreamReader(
                System.in));

        String key = "trnsl.1.1.20191206T165834Z.50ed042d59e192aa.81c30e2830afbe9bdc5fa2e4c7113646e5ee9ed7";

        String cmd;
        System.out.println("Choose your command:");
        try {
            while( (cmd = in.readLine()) != null) {
                if ( cmd.startsWith("lingua") ) {
                    String [] cmdArgs = cmd.split(" ");
                    if (cmdArgs.length >= 2) {
                        String text = "";
                        for(int i = 1; i < cmdArgs.length; i++){
                            text += cmdArgs[i] + " ";
                        }

                        String res = yandex.getLanguage(key, text);

                        System.out.println("A pagina está em " + res);
                    }

                } else if(cmd.startsWith("traduzir"))  {
                    String [] cmdArgs = cmd.split(" ");
                    if (cmdArgs.length >= 3) {
                        String text = "";
                        for(int i = 1; i < cmdArgs.length - 1; i++){
                            text += cmdArgs[i] + " ";
                        }

                        String lang = cmdArgs[cmdArgs.length - 1];

                        String res = yandex.tranlasteText(key, text, lang);

                        System.out.println("Traducao: " + res);
                    }
                } else {
                    System.out.println("Please choose one of the available commands");
                }
                System.out.println("Choose your command:");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    private void debug(HttpURLConnection connection) throws IOException {
        // This function is used to debug the resulting code from HTTP connections.

        // Response code such as 404 or 500 will give you an idea of what is wrong.
        System.out.println("Response Code:" + connection.getResponseCode());

        // The HTTP headers returned from the server
        System.out.println("_____ HEADERS _____");
        for ( String header : connection.getHeaderFields().keySet()) {
            System.out.println(header + ": " + connection.getHeaderField(header));
        }

        // If there is an error, the response body is available through the method
        // getErrorStream, instead of regular getInputStream.
        BufferedReader in = new BufferedReader(new InputStreamReader(
                connection.getErrorStream()));
        StringBuilder builder = new StringBuilder();
        String inputLine;
        while ((inputLine = in.readLine()) != null)
            builder.append(inputLine);
        in.close();
        System.out.println("Body: " + builder);
    }


    private String getLanguage(String key, String text) {
        try {
            URL url = new URL("https://translate.yandex.net/api/v1.5/tr.json/detect");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // Let's try to paint a tile and hope that there is no tile there yet
            // Method is now POST for creating a new tile
            connection.setRequestMethod("GET");

            connection.setDoOutput(true);
            connection.setInstanceFollowRedirects(false);
            connection.setRequestProperty("Accept", "application/json");

            // We can use getOkeytputStream() for passing the values for r, g and b in the request's body
            OutputStream os = connection.getOutputStream();
            os.write(("key=" + key + "&text=" + text).getBytes());
            os.flush();

            if (connection.getResponseCode() >= 300) {
                debug(connection);
            } else {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                String inputLine;
                StringBuffer response = new StringBuffer();
                while((inputLine = in.readLine()) != null){
                    response.append(inputLine);
                }
                in.close();

                System.out.println(response.toString());

                JSONParser parser = new JSONParser();
                JSONObject json = (JSONObject) parser.parse(response.toString());

                return (String) json.get("lang");
            }

        } catch(IOException | ParseException e) {
            e.printStackTrace();
        }

        return "Não obtida";

    }

    private String tranlasteText(String key, String text, String lang) {
        try {
            URL url = new URL("https://translate.yandex.net/api/v1.5/tr.json/translate");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // Let's try to paint a tile and hope that there is no tile there yet
            // Method is now POST for creating a new tile
            connection.setRequestMethod("POST");

            connection.setDoOutput(true);
            connection.setInstanceFollowRedirects(false);
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("User-agent", "Pablo v1");

            // We can use getOkeytputStream() for passing the values for r, g and b in the request's body
            OutputStream os = connection.getOutputStream();
            os.write(("key=" + key + "&text=" + text + "&lang=" + lang).getBytes());
            os.flush();

            if (connection.getResponseCode() >= 300) {
                debug(connection);
            } else {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                String inputLine;
                StringBuffer response = new StringBuffer();
                while((inputLine = in.readLine()) != null){
                    response.append(inputLine);
                }
                in.close();

                JSONParser parser = new JSONParser();
                JSONObject json = (JSONObject) parser.parse(response.toString());

                JSONArray jsonArray = (JSONArray) json.get("text");

                return (String) jsonArray.get(0);
            }

        } catch(IOException | ParseException e) {
            e.printStackTrace();
        }

        return "Erro na traducao";
    }

}
