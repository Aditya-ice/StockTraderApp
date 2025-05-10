package com.example.stocktrader;

// Helper class to hold stock information for the TableView
public class StockInfo {
    private final String symbol;
    private final double open;
    private final double high;
    private final double low;
    private final double price;
    private final double change;
    private final String changePercent;
    private final long volume;

    public StockInfo(String symbol, double open, double high, double low, double price, double change, String changePercent, long volume) {
        this.symbol = symbol;
        this.open = open;
        this.high = high;
        this.low = low;
        this.price = price;
        this.change = change;
        this.changePercent = changePercent;
        this.volume = volume;
    }

    public String getSymbol() {
        return symbol;
    }

    public double getOpen() {
        return open;
    }

    public double getHigh() {
        return high;
    }

    public double getLow() {
        return low;
    }

    public double getPrice() {
        return price;
    }

    public double getChange() {
        return change;
    }

    public String getChangePercent() {
        return changePercent;
    }

    public long getVolume() {
        return volume;
    }
}
