package com.example.stocktrader;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.function.Consumer;

public class StockDataService {

    private static final String API_KEY = "ADD API key"; // Replace with your actual API key
    private static final String API_URL = "https://www.alphavantage.co/query?function=GLOBAL_QUOTE&symbol=%s&apikey=%s";
    private static final String TIME_SERIES_DAILY_URL = "https://www.alphavantage.co/query?function=TIME_SERIES_DAILY&symbol=%s&outputsize=compact&apikey=%s";
    private static final int MAX_RETRIES = 3;
    private static final long INITIAL_BACKOFF = 1000;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public List<StockInfo> fetchStockData(List<String> symbols) { // Changed return type to StockInfo
        List<StockInfo> stockList = new ArrayList<>();
        for (String symbol : symbols) {
            StockInfo stockInfo = fetchStock(symbol);
            if (stockInfo != null) {
                stockList.add(stockInfo);
            }
        }
        return stockList;
    }

    private StockInfo fetchStock(String symbol) { // Changed return type to StockInfo
        int retryCount = 0;
        long currentBackoff = INITIAL_BACKOFF;

        while (retryCount < MAX_RETRIES) {
            try {
                String url = String.format(API_URL, symbol, API_KEY);
                URL obj = new URL(url);
                HttpURLConnection con = (HttpURLConnection) obj.openConnection();
                con.setRequestMethod("GET");

                int responseCode = con.getResponseCode();
                if (responseCode == 200) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    String inputLine;
                    StringBuilder response = new StringBuilder();
                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();
                    return parseStockData(symbol, response.toString());
                } else if (responseCode == 429) {
                    System.out.println("Rate limit exceeded. Retrying in " + currentBackoff + " ms.");
                    Thread.sleep(currentBackoff);
                    currentBackoff *= 2;
                    retryCount++;
                } else {
                    System.out.println("HTTP error: " + responseCode);
                    break;
                }
            } catch (IOException | InterruptedException e) {
                System.out.println("Exception: " + e.getMessage());
                e.printStackTrace();
                break;
            }
        }
        System.out.println("Failed to fetch data for " + symbol + " after " + MAX_RETRIES + " attempts.");
        return null;
    }

    private StockInfo parseStockData(String symbol, String jsonResponse) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(jsonResponse);
            JsonNode globalQuote = root.get("Global Quote");
            if (globalQuote != null &&
                    globalQuote.get("01. symbol") != null &&
                    globalQuote.get("02. open") != null &&
                    globalQuote.get("03. high") != null &&
                    globalQuote.get("04. low") != null &&
                    globalQuote.get("05. price") != null &&
                    globalQuote.get("09. change") != null &&
                    globalQuote.get("10. change percent") != null &&
                    globalQuote.get("06. volume") != null) { // Corrected volume field name in check
                return new StockInfo(
                        globalQuote.get("01. symbol").asText(),
                        Double.parseDouble(globalQuote.get("02. open").asText()), // Parse as double
                        Double.parseDouble(globalQuote.get("03. high").asText()), // Parse as double
                        Double.parseDouble(globalQuote.get("04. low").asText()),  // Parse as double
                        globalQuote.get("05. price").asDouble(),
                        globalQuote.get("09. change").asDouble(),
                        globalQuote.get("10. change percent").asText(),
                        globalQuote.get("06. volume").asLong() // Corrected volume field name
                );
            } else {
                System.out.println("Could not parse stock data for " + symbol + " from JSON: " + jsonResponse);
                return null;
            }
        } catch (Exception e) {
            System.out.println("Error parsing JSON: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    public void fetchHistoricalData(String symbol, Consumer<Map<LocalDate, Double>> callback) {
        int retryCount = 0;
        long currentBackoff = INITIAL_BACKOFF;

        while (retryCount < MAX_RETRIES) {
            try {
                String url = String.format(TIME_SERIES_DAILY_URL, symbol, API_KEY);
                URL obj = new URL(url);
                HttpURLConnection con = (HttpURLConnection) obj.openConnection();
                con.setRequestMethod("GET");

                int responseCode = con.getResponseCode();
                if (responseCode == 200) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    String inputLine;
                    StringBuilder response = new StringBuilder();
                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();
                    Map<LocalDate, Double> historicalData = parseHistoricalData(response.toString());
                    callback.accept(historicalData);
                    return; // Important: Exit the method on success
                } else if (responseCode == 429) {
                    System.out.println("Rate limit exceeded. Retrying in " + currentBackoff + " ms.");
                    Thread.sleep(currentBackoff);
                    currentBackoff *= 2;
                    retryCount++;
                } else {
                    System.out.println("HTTP error: " + responseCode);
                    break; // Exit the retry loop
                }
            } catch (IOException | InterruptedException e) {
                System.out.println("Exception: " + e.getMessage());
                e.printStackTrace();
                break; // Exit the retry loop
            }
        }
        System.out.println("Failed to fetch historical data for " + symbol + " after " + MAX_RETRIES + " attempts.");
        callback.accept(null); // Ensure callback is always called, even on failure
    }

    private Map<LocalDate, Double> parseHistoricalData(String jsonResponse) {
        Map<LocalDate, Double> dailyPrices = new HashMap<>();
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(jsonResponse);
            JsonNode timeSeriesDaily = root.get("Time Series (Daily)");
            if (timeSeriesDaily != null) { // Check if Time Series Daily exists
                Iterator<Map.Entry<String, JsonNode>> fieldsIterator = timeSeriesDaily.fields(); // Get iterator
                while (fieldsIterator.hasNext()) {
                    Map.Entry<String, JsonNode> entry = fieldsIterator.next(); // Get the next entry
                    LocalDate date = LocalDate.parse(entry.getKey(), DATE_FORMATTER);
                    JsonNode dailyData = entry.getValue();
                    JsonNode closePriceNode = dailyData.get("4. close");
                    if (closePriceNode != null) {
                        double closePrice = closePriceNode.asDouble();
                        dailyPrices.put(date, closePrice);
                    }
                }
            }
            else{
                System.out.println("Could not parse historical data: Time Series Daily is null");
            }
        } catch (Exception e) {
            System.out.println("Error parsing historical data JSON: " + e.getMessage());
            e.printStackTrace();
        }
        return dailyPrices;
    }
}
