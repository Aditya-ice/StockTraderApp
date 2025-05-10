package com.example.stocktrader;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Trade {
    private final LocalDateTime timestamp;
    private final String symbol;
    private final int quantity;
    private final double price;
    private final String type; // "BUY" or "SELL"

    public Trade(LocalDateTime timestamp, String symbol, int quantity, double price, String type) {
        this.timestamp = timestamp;
        this.symbol = symbol;
        this.quantity = quantity;
        this.price = price;
        this.type = type;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getSymbol() {
        return symbol;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getPrice() {
        return price;
    }

    public String getType() {
        return type;
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return formatter.format(timestamp) + "," + symbol + "," + quantity + "," + String.format("%.2f", price) + "," + type;
    }

    public String getCsvRow() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return formatter.format(timestamp) + "," + symbol + "," + quantity + "," + String.format("%.2f", price) + "," + type + "\n";
    }
}