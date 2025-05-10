module com.example.stocktrader {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.base;
    requires YahooFinanceAPI;
    requires com.fasterxml.jackson.databind;
//    requires yahoofinanceapi;


    opens com.example.stocktrader to javafx.fxml;
    exports com.example.stocktrader;
}