# Coding challenge for Backend Development

# Backend API Coding Challenge
The goal of this challenge is to implement improvements for an already existing API backend implementation that manages purchases of products.

The code is written in Java 8 using Spring Boot with Spring Web, JPA, and Gradle as the build system. The Database layer is mocked in memory, so there is no need to install anything else.

## Existing solution
The existing project contains a repository of products available for purchase, with different prices in EUR. In addition, the backend maintains a list of purchases, which contains some information about the time of purchase, customer, invoice, product identifiers and price of the purchase.

Finally, the existing solution outputs some statistics about the purchases in the last 30 days, such as the total value.

You can clone the existing project here: https://github.com/journiapp/backend-coding-challenge

Please modify this existing project to complete your tasks.

## Your Tasks
Your goal is to add support for multiple currencies to the project. What should be achieved is that all purchases in any currency should be stored with their converted value in EUR, so that we can calculate the statistics correctly in EUR as well.

For calculating the currency conversion itself, you can use the existing implementation of the CurrencyConverter, which supports many currencies including USD, CHF, among others.

Here is a quick summary of the existing endpoints and your tasks for each of them. The behaviors of each endpoint are described in the API Specification below.

List all products in their local currency.
The [GET] /products endpoint should return the currencyCode and convert any prices to the local currency. Write tests for this.
Add purchases in local currency.
The [POST] /purchases endpoint should have support for currencies. It should be able to receive a value in e.g. USD and store the price in EUR. Also write tests for this.
Fetching the statistics should be instant even for large amount of purchases.
The [GET] /purchases/statistics endpoint shows statistics such as the total value (EUR) of purchases in the last 30 days. The existing implementation runs slower with increasing number of purchases. Your task is to make this endpoint fast even for potentially large amounts of purchases, resulting in constant runtime (relative to the number of purchases).
Fix the statistics calculation
In addition, you should fix a bug in the existing implementation of the statistics endpoint (see above). Unfortunately the existing test is not very helpful, it should be improved to help finding the bug.
Feel free to implement new tests, change names, codes, documentation. You should take all available time to improve the existing implementation as much as possible, according to what you think are best practices.

API Specification

### [GET] /products – List all products
Parameters:

countryCode: string
Returns a list of all products, with the price converted to the currency of the given countryCode. If no countryCode is given or given countryCode is not supported, it returns the price in EUR by default.

(The current implementation does not contain the currencyCode and does not convert any prices)

Response:
```json
{
"id": string,
"description": string,
"price": double,
"currencyCode": string
}
```

### [POST] /purchases – Register a Purchase
Saves the given Purchase information. All fields are required.

The amount is the total amount of the purchase, in the currency of currencyCode.

(The current implementation does not support currencies. This endpoint should be able to receive values in different currencies by adding a currencyCode)

Request:
```json
{
"dateTime": date_iso,
"productIds": array[string],
"customerName": string,
"invoiceNumber": string,
"currencyCode": string,
"amount": number
}
```

Response:

Status 200 – Ok. Purchase saved
Status 400 – Invalid request

### [GET] /purchases/statistics – Purchases statistics
Returns a JSON object with statistics about the purchases in the last 30 days. All prices are in EUR. This function should be fast, running with O(1).

The totalAmount, avgAmount, minAmount, maxAmount returns the total, average, minimum and maximum value of the purchases in the last 30 days.

(The current implementation contains a bug. And also it does not have the required performance and runtime characteristics)

Response:
```json
{
  "from": date_iso,
  "to": date_iso,
  "countPurchases": integer,
  "totalAmount": double,
  "avgAmount": double,
  "minAmount": double,
  "maxAmount": double
}
```