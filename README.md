# Warehouse Manager Application

This application was developed for the Object-Oriented Programming (OOP) course at IST in the second year of the bachelor's degree. It manages the inventory of a warehouse of natural resources and their derivatives (formed by one or more products or their derivatives). The warehouse awards loyalty prizes to good partners (who buy and sell products), based on their business volume (purchases and sales). The functionality of the application includes, among other actions, manipulating product data for negotiation, registering/manipulating partner data, registering/manipulating purchase and sale transactions, and performing various searches on the stored information.

# How to run

- Open a terminal and navigate to the po-uilib directory.
- Execute the makefile in the po-uilib directory by running the command `make`.

- Navigate to the ggc-core directory.
- Execute the makefile in the ggc-core directory by running the command `make`.

- Navigate to the ggc-app directory.
- Execute the makefile in the ggc-app directory by running the command `make`.

- Add the paths to the generated jars to the CLASSPATH environment variable. For example, if the jars are located in the ggc-app, ggc-core, and po-uilib directories, you would run the command `export CLASSPATH=/path/to/po-uilib:/path/to/ggc-core:/path/to/ggc-app:.`

- Navigate back to the ggc-app directory.
  Run the application by executing the command java `ggc.app.App`.

- And now you are ready to go :D ; unfortunately the app interface is written in portuguese so you may not understand the basic commands

Note: The steps may vary depending on your operating system and setup. Make sure to replace the /path/to/ placeholders with the actual paths to the directories where the jars are located.

## Entities

- ### Warehouse

  The warehouse has an initial balance of 0 (zero).

- ### Products

  Products can be simple or derived. A derived product has a manufacturing recipe. The recipe corresponds to the discrimination of the identifiers of the components (simple or derived) that make up the product and their quantities. It is not possible to define recipes whose components are not previously known.

  Both simple and derived products are bought, sold, or manufactured (in the case of derived products) in batches. Each batch has a supplier (the partner from whom the product is purchased), the number of units available for the product in the batch, and the price of each unit. There may be several batches of the same product, with the same supplier, with the same or different prices. The price of a product is always a non-negative floating-point number. When a batch is exhausted, it is removed from the data record.

  The price of the products is set by the partner at the time of purchase (by the warehouse). This process is uniform for all products, except in the indicated operations (see below).

  The warehouse can perform aggregation and disaggregation operations. An aggregation operation consists of creating a derived product from the stocks of its components. The price of the derived products resulting from aggregations is calculated based on the prices of the components, according to the recipe, having a multiplicative surcharge (a positive floating-point number), defined by the recipe, relative to the added value associated with the combination. For example: water is made of hydrogen (2x) and oxygen (1x). Its price is: PH2O = (1+α) × (2 × PH + PO) (where Px are prices and α is the surcharge factor, e.g., 0.1, corresponding to a 10% increase in the value of the components). The resource reserve for the construction of a certain quantity of a derived product contemplates the totality of the construction (e.g., it is not possible to respect an order for 10 units of water if only 8 units of oxygen are available). In general, consumption operations of stocks always start with the lowest-priced stocks, consuming one or more batches, totally or partially, in the process.

  It is possible to disaggregate a derived product and recover its components. There are no product losses in this operation, but there may be a loss of value: the value of the derived product may be higher than the sum of the prices of its components. The partner who requests the disaggregation operation pays the differential value, if positive, and a disaggregation transaction is recorded (with the value of the differential, even if negative).

- ### Partners

  Each partner has a name (string) and an address (string). Associated with each partner is also information regarding their transactions.

  A partner also has a status (e.g., Elite, etc. - see below), to which points are credited. The status has an impact on the partner's relationship with the warehouse.

  Given a partner, it is possible to access the history of their transactions (see below).

- ### Transactions
  All transactions are identified by a unique integer key, automatically managed by the application. The identifier starts at 0 (zero) and is incremented when a new transaction is registered. The sequence of identifiers is shared by all types of transactions. All transactions have a date and time of occurrence (in the format YYYY-MM-DD hh:mm:ss).
  There are three types of transactions: purchase, sale, and aggregation/desaggregation. Each transaction is associated with a partner and one or more products.
  In the case of a purchase or sale transaction, the application records the number of units and the price per unit for each product involved in the transaction. The total amount of the transaction is automatically calculated as the sum of the products of the number of units and the price per unit for each product.
  In the case of an aggregation or desaggregation transaction, the application records the resulting product and its components. For an aggregation transaction, the components are subtracted from the inventory, and the resulting product is added. For a desaggregation transaction, the resulting components are added to the inventory, and the original product are subtracted.
  Each transaction also has an associated loyalty points value, which is calculated based on the volume of the transaction (in terms of the total amount). The loyalty points are awarded to the partner who performed the transaction, and the amount of points awarded is based on their loyalty status (e.g., Elite partners receive more points).
  Finally, the application also provides a search functionality for transactions, allowing the user to filter transactions by date, partner, and type. The search results includes a summary of the transaction, including the date, partner, type, products involved, and loyalty points awarded.
