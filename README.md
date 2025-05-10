User Guide: Stock Trader Application
Introduction
This guide provides instructions on how to set up and use the Stock
Trader application. This application allows you to view real-time
stock market data, monitor historical trends, and manage trades.
System Requirements
To run this application, you will need the following:
• Operating System: Windows, macOS, or Linux
• Java Development Kit (JDK): Version 17 or higher. You can
download it from Oracle or Eclipse Temurin.
• Integrated Development Environment (IDE): While not
strictly required, using an IDE is highly recommended. We
suggest:
◦ IntelliJ IDEA (Community Edition is sufficient)
◦ Eclipse
◦ NetBeans
• Maven: If you download the project as source code, you'll
need Maven to build it. You can download it from Maven. If you
download a pre-built JAR file, you won't need this.
• Alpha Vantage API Key: The application uses the Alpha
Vantage API to fetch stock data. You’ll need to sign up for a
free API key at Alpha Vantage and replace the placeholder
with your key in the code. One can go for the free API key but
that only comes with 25 requests per day , and this will run out
within a short time as the stocks keep refreshing , but will be
enough for the demonstration of the application. (I have
included the API key from my side already , but one can add
from their side as well)
Setup Instructions
Follow these steps to set up the application:
1. Install JDK
• Download the JDK 17 or a later version from the links provided
above.
• Follow the installation instructions for your operating system.
• Important: After installation, set the JAVA_HOME environment
variable and add the JDK's bin directory to your PATH. This
allows you to run Java commands from the command line.
2. Install an IDE (Recommended)
• Download and install your preferred IDE (IntelliJ IDEA, Eclipse,
or NetBeans) from the links provided above.
• Follow the installation instructions for your operating system.
3. Obtain the Project
You have a few options to obtain the project:
• Download Pre-built JAR (Simplest): If provided, download
the compiled .jar file. You can then skip to step 5.
• Download Source Code (For Developers): If you have the
source code, download or clone the project repository. You’ll
need to build the project using Maven.
4. Build the Project
If you have downloaded the project source code:
• Open a terminal or command prompt.
• Navigate to the project's root directory (the directory containing
the pom.xml file).
• Run the following command:
mvn clean install
•
•
This command will download dependencies, compile the code,
and create a .jar file in the target directory.
5. Configure the API Key
• Open the StockDataService.java file in your IDE. If you are using
the JAR file, you will not be able to do this. You would need to
obtain the source code, and then make the change.
• note: i have already put an API key in the code.
• Locate the following line:
private static final String API_KEY = "YOUR_API_KEY"; // Replace with
your actual API key
•
•
• Replace "YOUR_API_KEY" with your actual Alpha Vantage API
key. For example:
private static final String API_KEY = "YOURACTUALAPIKEY123";
•
•
• Important: Save the StockDataService.java file. If you are using
the JAR file, you would need to re-build the project.
6. Run the Application
• If you have the JAR file:
◦ Open a terminal or command prompt.
◦ Navigate to the directory containing the .jar file.
◦ Run the following command:
java -jar your-application-name.jar
(Replace your-application-name.jar with the actual name of your
JAR file.)
• If you are using an IDE:
◦ Open the project in your IDE.
◦ Locate the Main.java file (this is the entry point of the
application).
◦ Run the Main.java file as a Java application. Your IDE will
have a "Run" button or menu option for this.
Using the Application
Once the application is running, you'll see the main window with a
table displaying stock data. Heres how to use the application:
Main Window
• Stock Table: The table displays real-time stock data for the
symbols listed (AAPL, GOOG, MSFT by default). The columns
show:
◦ Symbol: The stock ticker symbol.
◦ Open: The opening price of the stock.
◦ High: The highest price of the stock during the day.
◦ Low: The lowest price of the stock during the day.
◦ Price: The current price of the stock.
◦ Change: The change in price since the previous days
close.
◦ Change Percent: The percentage change in price.
◦ Volume: The number of shares traded.
• Add Stock:
◦ Enter a stock symbol in the Add Stock text field.
◦ Click the Add button.
◦ The application will fetch and display data for the new
stock.
• Remove Stock:
◦ Select a stock from the table.
◦ Click the Remove button.
◦ The selected stock will be removed from the table.
• Buy/Sell:
◦ Select a stock from the table.
◦ Enter the quantity of shares you want to buy or sell in the
Quantity text field.
◦ Click the Buy or Sell button.
◦ The application will record the trade in a CSV file named
trades.csv.
• View Trade Records:
◦ Click the Trade Records button.
◦ A new window will appear, displaying a table of all
recorded trades. The table shows the timestamp, symbol,
quantity, price, and type of each trade.
•
• View Chart:
◦ Select a stock from the table.
◦ Click the View Chart button.
◦ A new window will appear, displaying a line chart of the
stocks historical price data.
• Important Notes
- API Key: The application relies on the Alpha Vantage API. If you
don’t configure the API key correctly, you will get errors. Free API
keys have limit of 25 requests per day from an IP address (one
can you use VPN with IP shifting as a work around).
- Data Updates: The application updates stock data periodically
(every 5 seconds).
- Trades File: The application saves trade records to a file named
trades.csv in the applications working directory. Ensure that the
application has write permissions in that directory.
- Error Handling: The application includes basic error handling,
such as displaying error messages if it cannot fetch data or parse
JSON responses. Check the console output for details.
- Rate Limits: Alpha Vantage has rate limit of 25 requests per day
from an IP address (one can you use VPN with IP shifting as a
work around). Be mindful of the number of requests you are
making to the API. If you exceed the limit, you will get an error
message.
