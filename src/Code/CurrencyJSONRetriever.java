package Code;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * This is an independent class in the application.
 * This class needs to be run explicitly.
 * It contains methods that extract and store currency data from the server.
 * This class must be run once before using the application driver : 'CurrencyConverter.java'.
 * Once run, the class creates the 'currencies.json' in the JSON folder.
 * The currencies are stored in a file named : 'currencies.json'.
 */

public class CurrencyJSONRetriever {

    /**
     * Stores the retrieved currency data from the server.
     */
    Map<String,Object> quotes;

    /*******************************************************************************************************************
     * Private Methods
     ******************************************************************************************************************/
    /**
     * Creates a .json file, 'currencies.json' and writes the data stored in the JSON node to the file.
     * @throws IOException Handles methods of the Mapper and Writer classes.
     */
    private void createJSONFile() throws IOException {
        String pathName = "src/Resources/JSON/currencies.json";
        ObjectMapper mapper = new ObjectMapper();
        ObjectWriter writer = mapper.writer();
        File currenciesFile = new File(pathName);
        writer.writeValue(currenciesFile, this.quotes.get("results"));
    }

    /*******************************************************************************************************************
     * Public Methods
     ******************************************************************************************************************/
    /**
     *Establishes a connection with the server and retrieves currency data in .json format.
     * @throws IOException Handles the Connection class and its methods.
     */
    public void retrieveCurrencies() throws IOException {
        String urlString = "https://free.currconv.com/api/v7/currencies?apiKey=";
        Connection connection = new Connection(urlString);
        int responseCode = connection.getConnection().getResponseCode();
        if (responseCode == 200) {
            InputStream stream = (InputStream) connection.getConnection().getContent();
            ObjectMapper mapper = new ObjectMapper();
            this.quotes = mapper.readValue(stream, HashMap.class);
        }
    }

    /**
     * Creates the class object and calls the method for currency data retrieval.
     * @param args Command line arguments.
     * @throws IOException Handles the retrieveCurrencies().
     */
    public static void main(String args []) throws IOException {
        CurrencyJSONRetriever obj = new CurrencyJSONRetriever();
        obj.retrieveCurrencies();
        obj.createJSONFile();
    }
}
