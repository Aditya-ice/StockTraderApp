package com.example.stocktrader;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map; // Import for Map
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;

import com.example.stocktrader.Trade;

public class Controller {

    @FXML
    private TableView<StockInfo> stockTableView;
    @FXML
    private TableColumn<StockInfo, String> symbolColumn;
    @FXML
    private TableColumn<StockInfo, Double> openColumn;
    @FXML
    private TableColumn<StockInfo, Double> highColumn;
    @FXML
    private TableColumn<StockInfo, Double> lowColumn;
    @FXML
    private TableColumn<StockInfo, Double> priceColumn;
    @FXML
    private TableColumn<StockInfo, Double> changeColumn;
    @FXML
    private TableColumn<StockInfo, String> changePercentColumn;
    @FXML
    private TableColumn<StockInfo, Long> volumeColumn;
    @FXML
    private TextField addStockTextField;
    @FXML
    private Button addButton;
    @FXML
    private Button removeButton;
    @FXML
    private TextField quantityTextField;
    @FXML
    private Button buyButton;
    @FXML
    private Button sellButton;
    @FXML
    private Button tradeRecordsButton;
    @FXML // Add this annotation
    private Button viewChartButton;

    private final StockDataService stockDataService = new StockDataService();
    private final ObservableList<StockInfo> stockData = FXCollections.observableArrayList();
    private final List<String> symbols = new ArrayList<>(Arrays.asList("AAPL", "GOOG", "MSFT"));
    private final String TRADES_FILE = "trades.csv";
    private Timeline timeline;

    @FXML
    public void initialize() {
        // Set up the columns in the TableView
        symbolColumn.setCellValueFactory(new PropertyValueFactory<>("symbol"));
        openColumn.setCellValueFactory(new PropertyValueFactory<>("open"));
        highColumn.setCellValueFactory(new PropertyValueFactory<>("high"));
        lowColumn.setCellValueFactory(new PropertyValueFactory<>("low"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        changeColumn.setCellValueFactory(new PropertyValueFactory<>("change"));
        changePercentColumn.setCellValueFactory(new PropertyValueFactory<>("changePercent"));
        volumeColumn.setCellValueFactory(new PropertyValueFactory<>("volume"));

        // Set the data source for the TableView
        stockTableView.setItems(stockData);

        // Fetch initial stock data
        fetchAndDisplayStocks(new ArrayList<>(symbols));// Pass a copy to avoid concurrent modification
        buyButton.setOnAction(event -> handleBuy());
        sellButton.setOnAction(event -> handleSell());
        tradeRecordsButton.setOnAction(event -> showTradeRecords());
        viewChartButton.setOnAction(event -> handleViewChart());
        addButton.setOnAction(event -> handleAddStock());  // Added action handler
        removeButton.setOnAction(event -> handleRemoveStock());

        // Set up periodic updates
        setUpRealTimeUpdates();
    }

    private void setUpRealTimeUpdates() {
        Duration interval = Duration.seconds(5); // Adjust the interval as needed (consider API limits)
        KeyFrame keyFrame = new KeyFrame(interval, event -> {
            fetchAndDisplayStocks(new ArrayList<>(symbols));
        });
        timeline = new Timeline(keyFrame);
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    @FXML
    private void handleAddStock() {
        String newSymbol = addStockTextField.getText().trim().toUpperCase();
        if (!newSymbol.isEmpty() && !symbols.contains(newSymbol)) {
            symbols.add(newSymbol);
            fetchAndDisplayStocks(List.of(newSymbol)); // Fetch and display only the new stock
            addStockTextField.clear();
        }
    }

    @FXML
    private void handleBuy() {
        recordTrade("BUY");
    }

    @FXML
    private void handleSell() {
        recordTrade("SELL");
    }

    @FXML
    private void showTradeRecords() {
        List<Trade> trades = readTradesFromCSV();
        if (!trades.isEmpty()) {
            TableView<Trade> tradeTableView = new TableView<>();

            TableColumn<Trade, String> timestampCol = new TableColumn<>("Timestamp");
            timestampCol.setCellValueFactory(new PropertyValueFactory<>("timestamp"));

            TableColumn<Trade, String> symbolCol = new TableColumn<>("symbol");
            symbolCol.setCellValueFactory(new PropertyValueFactory<>("symbol"));

            TableColumn<Trade, Integer> quantityCol = new TableColumn<>("quantity");
            quantityCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));

            TableColumn<Trade, Double> priceCol = new TableColumn<>("price");
            priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));

            TableColumn<Trade, String> typeCol = new TableColumn<>("type");
            typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));

            tradeTableView.getColumns().addAll(timestampCol, symbolCol, quantityCol, priceCol, typeCol);
            tradeTableView.setItems(FXCollections.observableArrayList(trades));

            Stage stage = new Stage();
            stage.setTitle("Trade Records");
            stage.setScene(new Scene(tradeTableView, 600, 400));
            stage.show();
        } else {
            System.out.println("No trade records found.");
            // Optionally show an alert to the user
        }
    }

    private List<Trade> readTradesFromCSV() {
        List<Trade> trades = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(TRADES_FILE))) {
            String line;
            boolean isHeader = true;
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            while ((line = br.readLine()) != null) {
                if (isHeader) {
                    isHeader = false;
                    continue; // Skip the header row
                }
                String[] values = line.split(",");
                if (values.length == 5) {
                    LocalDateTime timestamp = LocalDateTime.parse(values[0], formatter);
                    String symbol = values[1];
                    int quantity = Integer.parseInt(values[2]);
                    double price = Double.parseDouble(values[3]);
                    String type = values[4];
                    trades.add(new Trade(timestamp, symbol, quantity, price, type));
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading trade records from CSV: " + e.getMessage());
            // Optionally show an alert to the user
        }
        return trades;
    }
    @FXML
    private void handleViewChart() {
        StockInfo selectedStock = stockTableView.getSelectionModel().getSelectedItem();
        if (selectedStock != null) {
            String symbol = selectedStock.getSymbol();
            stockDataService.fetchHistoricalData(symbol, this::displayStockChart);
        } else {
            System.out.println("Please select a stock to view the chart.");
            // Optionally show an alert
        }
    }

    private void displayStockChart(Map<LocalDate, Double> historicalData) {
        if (historicalData != null && !historicalData.isEmpty()) {
            CategoryAxis xAxis = new CategoryAxis();
            NumberAxis yAxis = new NumberAxis();
            xAxis.setLabel("Date");
            yAxis.setLabel("Closing Price");

            LineChart<String, Number> lineChart = new LineChart<>(xAxis, yAxis);
            lineChart.setTitle("Price Trend for " + stockTableView.getSelectionModel().getSelectedItem().getSymbol());

            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("Closing Price");

            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            historicalData.forEach((date, price) -> {
                series.getData().add(new XYChart.Data<>(dateFormatter.format(date), price));
            });

            lineChart.getData().add(series);

            Stage stage = new Stage();
            stage.setTitle("Stock Chart");
            stage.setScene(new Scene(lineChart, 800, 600));
            stage.show();
        } else {
            System.out.println("Could not fetch historical data or no data available.");
            // Optionally show an alert
        }
    }

    @FXML
    private void handleRemoveStock() {
        StockInfo selectedStock = stockTableView.getSelectionModel().getSelectedItem();
        if (selectedStock != null) {
            symbols.remove(selectedStock.getSymbol());
            stockData.remove(selectedStock);
        }
    }

    private void recordTrade(String type) {
        StockInfo selectedStock = stockTableView.getSelectionModel().getSelectedItem();
        String quantityStr = quantityTextField.getText().trim();

        if (selectedStock != null && !quantityStr.isEmpty()) {
            try {
                int quantity = Integer.parseInt(quantityStr);
                LocalDateTime now = LocalDateTime.now();
                // We use the current price from the UI for the trade record
                Trade trade = new Trade(now, selectedStock.getSymbol(), quantity, selectedStock.getPrice(), type);
                writeTradeToCSV(trade);
                quantityTextField.clear(); // Clear quantity after trade
            } catch (NumberFormatException e) {
                System.err.println("Invalid quantity entered.");
                // Optionally show an alert to the user
            }
        } else {
            System.err.println("Please select a stock and enter a quantity.");
            // Optionally show an alert to the user
        }
    }

    private void writeTradeToCSV(Trade trade) {
        try (FileWriter fw = new FileWriter(TRADES_FILE, true)) {
            java.io.File file = new java.io.File(TRADES_FILE);
            if (file.length() == 0) {
                // Write header if the file is new or empty
                fw.write("Timestamp,Symbol,Quantity,Price,Type\n");
            }
            fw.write(trade.getCsvRow());
        } catch (IOException e) {
            System.err.println("Error writing trade to CSV file: " + e.getMessage());
            e.printStackTrace();
            // Optionally show an alert to the user
        }
    }

    private void fetchAndDisplayStocks(List<String> symbolsToFetch) {
        List<StockInfo> fetchedStocks = stockDataService.fetchStockData(symbolsToFetch);
        for (StockInfo stock : fetchedStocks) {
            for (int i = 0; i < stockData.size(); i++) {
                if (stockData.get(i).getSymbol().equals(stock.getSymbol())) {
                    stockData.set(i, stock); // Update existing stock data
                    System.out.println("Updated: " + stock.getSymbol() + " - Open: " + stock.getOpen() + " - High: " + stock.getHigh() + " - Low: " + stock.getLow() + " - Price: " + stock.getPrice() + " - Change: " + stock.getChange() + " - Change%: " + stock.getChangePercent() + " - Volume: " + stock.getVolume());
                    return; // Exit after updating
                }
            }
            // If the stock is not already in the list (e.g., when adding a new one)
            stockData.add(stock);
            System.out.println("Fetched: " + stock.getSymbol() + " - Open: " + stock.getOpen() + " - High: " + stock.getHigh() + " - Low: " + stock.getLow() + " - Price: " + stock.getPrice() + " - Change: " + stock.getChange() + " - Change%: " + stock.getChangePercent() + " - Volume: " + stock.getVolume());
        }
    }
}
