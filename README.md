# Technicals Parser from Tradingview

This app provides a `microservice-built` application that parses data from [TradingView](https://tradingview.com), and emulates trading based on technicals' prediction.

## How does it work?

1. Once an hour an `Parser microservice` parses data from tradingview asynchronously using `selenium`
2. These data are added to the Kafka broker
3. Based on the data, the `Trader microservice` emulates trading and shows potential profit, shows the biggest loss while trade is opened.

Step 3 is not fully implemented yet.

If you want to use to operate your own money, it's not a financial advice etc :D
