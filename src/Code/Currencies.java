package Code;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.*;
import java.util.regex.*;

/**
 * This class contains data structures to store currencies and methods to manipulate them.
 * The currencies are loaded from the currencies.json file.
 */
public class Currencies {

    /*******************************************************************************************************************
     * Instance Variables.
     ******************************************************************************************************************/
    private Map<String, JsonNode> currencies;
    private List<String> currencyIds;
    private List<String> currencyNames;
    private Map<String, String> namesToId;

    /**
     * Default Constructor.
     * Creates and initializes data structure instances.
     * @throws IOException Handles loadCurrencies().
     */
    public Currencies()throws IOException{
        currencyIds = new ArrayList<>();
        currencyNames = new ArrayList<>();
        namesToId = new HashMap<>();
        loadCurrencies();
        loadCurrencyIds();
        loadCurrencyNames();
        loadNamesToId();
    }

    /*******************************************************************************************************************
     * Private Methods
     ******************************************************************************************************************/
    /**
     * Computes the currencies that are not supported by either the API or Java's Currency class.
     * Creates a set of API supported currencies and a set of 'Currency' class currencies.
     * Computes the currencies that are not present in the intersection of the two sets mentioned above.
     * @return Currencies not present in the intersection of the Currency class set and API currency set.
     */
    private Set<String> computeSkipCurrencies() {
        Set<String> javaCurrencies = new HashSet<>();
        Set<Currency> inbuiltCurrency = Currency.getAvailableCurrencies();
        for(Currency item : inbuiltCurrency) {
            javaCurrencies.add(item.toString());
        }
        Set<String> apiCurrencies = new HashSet<String>(this.currencies.keySet());
        Set<String> commonCurrencies = new HashSet<>(javaCurrencies);
        commonCurrencies.retainAll(apiCurrencies);
        Set<String> finalCurrencies = apiCurrencies;
        finalCurrencies.removeAll(commonCurrencies);
        return finalCurrencies;
    }

    /**
     * Rounds decimal numbers to two decimal places by using HALF_UP strategy.
     * In case of numbers such as 123.0000000023456, the method formats such numbers to 123.0000000023.
     * @param number Number to be rounded to two decimal places.
     * @return Returns a rounded number to two decimal places.
     */
    private BigDecimal round(BigDecimal number) {
        String patternString = "(.*\\.0*...).*";
        String numberString = number.toString();
        Pattern pattern = Pattern.compile(patternString);
        Matcher match = pattern.matcher(numberString);
        if (match.matches()) {
            BigDecimal finalAmount = new BigDecimal(match.group(1));
            int n = finalAmount.scale();
            if(n > 2) {
                finalAmount = finalAmount.stripTrailingZeros();
            }
            if(n > 2) {
                finalAmount = finalAmount.setScale(n - 1, RoundingMode.HALF_UP);
            }
            return finalAmount;
        }
        return number;
    }

    /**
     * Populates the 'currencies'(Map< String, JsonNode>) data structure with currency data from the 'currencies.json' file.
     * @throws IOException Handles readValue().
     */
    private void loadCurrencies() throws IOException{
        ObjectMapper mapper = new ObjectMapper();
        InputStream inputStream = ClassLoader.getSystemClassLoader().getResourceAsStream("Resources/JSON/currencies.json");
        currencies = mapper.readValue(inputStream, mapper.getTypeFactory().
                constructMapLikeType(Map.class, String.class, JsonNode.class));
        removeSkipCurrencies(computeSkipCurrencies());
    }

    /**
     * Populates the 'currencyIds' data structure with currency 'id' from the 'currencies' data structure.
     */
    private void loadCurrencyIds() {
        Collection<JsonNode> currencyObjects= currencies.values();
        for (JsonNode item : currencyObjects) {
            currencyIds.add(item.get("id").textValue());
        }
    }

    /**
     * Populates the 'currencyNames' data structure with 'currency names' from the 'currencies' data structure.
     */
    private void loadCurrencyNames() {
        Collection<JsonNode> currencyObjects = currencies.values();
        for(JsonNode item : currencyObjects) {
            currencyNames.add(item.get("currencyName").textValue());
        }
        Collections.sort(currencyNames);
    }

    /**
     * Creates a mapping from currency names to their corresponding 3 character code.
     * The mapping is done using the Map< String, String> data structure.
     */
    private void loadNamesToId() {
        for(Map.Entry<String, JsonNode> item : currencies.entrySet()) {
            JsonNode value = item.getValue();
            String id = value.get("id").textValue();
            String name = value.get("currencyName").textValue();
            namesToId.put(name, id);
        }
    }

    /**
     * Removes the currencies returned by 'computeSkipCurrencies()' from the 'currencies' data structure.
     * @param skipCurrencies A set of currencies to be eliminated from the 'currencies' data structure.
     */
    private void removeSkipCurrencies(Set<String> skipCurrencies) {
        for(String item : skipCurrencies) {
            this.currencies.remove(item);
        }
    }

    /*******************************************************************************************************************
     * Public Methods
     ******************************************************************************************************************/
    /**
     * Performs Conversion from the base currency to the target currency.
     * @param amount The amount to be converted to the target currency.
     * @param conversionRate The conversion rate from the base to target currency.
     * @return Returns the converted amount.
     */
    public BigDecimal convert(BigInteger amount, double conversionRate) {
        BigDecimal newAmount = new BigDecimal(amount);
        BigDecimal finalAmount = newAmount.multiply(new BigDecimal(conversionRate));
        if(finalAmount.compareTo(BigDecimal.valueOf(0)) == 0) {
            return BigDecimal.valueOf(0);
        }
        finalAmount = round(finalAmount);
        return finalAmount;
    }

    /**
     *Returns a list of available currency names.
     * @return Returns a list currency names.
     */
    public List<String> getCurrencyNames() {
        return currencyNames;
    }

    /**
     * Returns the corresponding three character code of the given currency string.
     * @param name The currency string.
     * @return Returns the corresponding three character code
     */
    public String getNameToId(String name) {
        String id = namesToId.get(name);
        return id;
    }

    /**
     * Returns the conversion rate from base to target currency.
     * A connection is created with the currency API server.
     * Returns -1 if some error occurs during connection establishment.
     * @param fromCurrency The base currency.
     * @param toCurrency The target currency.
     * @return Returns the conversion rate.
     * @throws IOException Handles the Connection(), getResponseCode(), getContent().
     */
    public double getRate(Currency fromCurrency, Currency toCurrency) throws IOException {
        String urlQuery = "https://free.currconv.com/api/v7/convert?q="
                + fromCurrency.getCurrencyCode() + "_"
                + toCurrency.getCurrencyCode()
                + "&compact=ultra&apiKey=";
        Connection connection = new Connection(urlQuery);
        if(connection.getConnection().getContentLength() == -1) {
            return -1;
        }
        int responseCode = connection.getConnection().getResponseCode();
        if (responseCode == 200) {
            InputStream stream = (InputStream) connection.getConnection().getContent();
            Scanner scanner = new Scanner(stream);
            String quote = scanner.nextLine();
            String number = quote.substring(quote.indexOf(':') + 1, quote.indexOf('}'));
            double conversionRate = Double.parseDouble(number);
            return conversionRate;
        }
        return -1;
    }
}
