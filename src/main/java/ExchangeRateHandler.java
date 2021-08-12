import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.time.LocalDate;
import java.util.*;

public class ExchangeRateHandler {
    private static ExchangeRateHandler handler = null;
    private static List<String> currencyCodes;
    private final String apiAddress = "https://api.frankfurter.app/";


    private ExchangeRateHandler() {
        setCurrencyCodes();
    }

    public static ExchangeRateHandler getInstance() {
        if (handler == null) {
            handler = new ExchangeRateHandler();
        }
        return handler;
    }

    public Map<String, Double> getExchangeRates(String base) throws IOException {
        String query = new StringBuilder(apiAddress).append("latest?from=").append(base.toUpperCase()).toString();
        Map<String, Object> jsonFileAsMap = new ObjectMapper().readValue(downloadData(query),
                new TypeReference<Map<String, Object>>() {
                });
        Map<String, Double> ratesMap = new HashMap<>();
        ((Map<String, Object>) jsonFileAsMap.get("rates")).forEach((k, v) ->
                ratesMap.put(k, Double.valueOf(v.toString())));
        return ratesMap;
    }

    public Map<String, Double> getExchangeRates(String base, LocalDate date) throws IOException {
        String query = new StringBuilder(apiAddress).append(date.getYear()).append("-").append(date.getMonth()).
                append("-").append(date.getDayOfMonth()).append("?from=").append(base.toUpperCase()).append("&to=").
                toString();
        Map<String, Object> jsonFileAsMap = new ObjectMapper().readValue(downloadData(query),
                new TypeReference<Map<String, Object>>() {
                });
        Map<String, Double> ratesMap = new HashMap<>();
        ((Map<String, Object>) jsonFileAsMap.get("rates")).forEach((k, v) ->
                ratesMap.put(k, Double.valueOf(v.toString())));
        return ratesMap;
    }

    public Map<String, Double> getExchangeRates(String base, String outputCurrency) throws IOException {
        String query = new StringBuilder(apiAddress).append("latest?from=").append(base.toUpperCase()).
                append("&to=").append(outputCurrency).toString();
        Map<String, Object> jsonFileAsMap = new ObjectMapper().readValue(downloadData(query),
                new TypeReference<Map<String, Object>>() {
                });
        Map<String, Double> ratesMap = new HashMap<>();
        ((Map<String, Object>) jsonFileAsMap.get("rates")).forEach((k, v) -> {
            ratesMap.put(k, Double.valueOf(v.toString()));
        });
        return ratesMap;
    }

    public Map<String, Double> getExchangeRates(String base, String outputCurrency, LocalDate startDate, LocalDate endDate) throws IOException {

        String query = new StringBuilder(apiAddress).append(new StringBuilder(startDate.toString())).append("..").
                append(new StringBuilder(endDate.toString())).append("?from=").append(base.toUpperCase()).append("&to=").
                append(outputCurrency).toString();
        Map<String, Object> jsonFileAsMap = new ObjectMapper().readValue(downloadData(query),
                new TypeReference<Map<String, Object>>() {
                });
        Map<String, Double> ratesMap = new TreeMap<>();
        ((Map<String, Object>) jsonFileAsMap.get("rates")).forEach((k, v) -> {
            ratesMap.put(k, Double.parseDouble(v.toString().split("[=}]")[1]));
        });
        return ratesMap;


    }

    private String downloadData(String query) throws IOException {
        URL url = new URL(query);
        BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
        StringBuilder data = new StringBuilder();
        String temp = "";
        while ((temp = reader.readLine()) != null) {
            data.append(temp);
        }
        return data.toString();
    }

    public List<String> getCurrencyCodes() {
        return currencyCodes;
    }

    private void setCurrencyCodes() {
        currencyCodes = new ArrayList<>();
        String query = new StringBuilder(apiAddress).append("currencies").toString();
        Map<String, String> jsonFileAsMap = null;
        try {
            jsonFileAsMap = new ObjectMapper().readValue(downloadData(query),
                    new TypeReference<Map<String, Object>>() {
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }
        jsonFileAsMap.forEach((k, v) -> currencyCodes.add(k.concat(" | ").concat(v)));
    }
}
