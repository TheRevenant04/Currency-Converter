package Code;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * This class is used to create and manage connections between the application and the currency API server.
 * An API key is required to make requests to the API server.
 * The API key is read from the APIKey.json file.
 * A connection is established using HTTPS protocol.
 */
public class Connection {

    /*******************************************************************************************************************
     * Instance Variables.
     ******************************************************************************************************************/
    private HttpsURLConnection connection;
    private String API_KEY;

    /**
     * Parameterized constructor.
     * @param urlString Specifies the URL to the API server.
     * @throws IOException Handles setKey() and createConnection().
     */
    public Connection(String urlString) throws IOException {
        setKey();
        createConnection(urlString);
    }

    /*******************************************************************************************************************
     * Private Methods
     ******************************************************************************************************************/
    /**
     * Establishes a connection to the server by creating a URL from the URL path provided using an HTTPS connection.
     * Uses 'GET' request method for requesting data.
     * @param urlString Specifies the path to the API server.
     * @throws IOException Handles URL(), openConnection() and setRequestMethod().
     */
    private void createConnection(String urlString) throws IOException {
        URL url = new URL(urlString+API_KEY);
        connection = (HttpsURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
    }

    /**
     * Reads the API key from APIKey.json and stores it in the API_Key instance variable.
     * @throws IOException Handles readTree().
     */
    private void setKey() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        InputStream inputStream = ClassLoader.getSystemClassLoader().getResourceAsStream("Resources/JSON/APIKey.json");
        JsonNode APIKeyJSON = mapper.readTree(inputStream);
        API_KEY = APIKeyJSON.get("API_Key").textValue();
    }

    /*******************************************************************************************************************
     * Public methods
     *******************************************************************************************************************/
    /**
     * Returns the connection object.
     * @return Returns the HttpsURLConnection object.
     */
    public HttpsURLConnection getConnection() {
        return connection;
    }
}

