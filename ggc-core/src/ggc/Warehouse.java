package ggc;

import java.io.*;
import java.util.TreeMap;

import javax.swing.text.AbstractDocument.ElementEdit;

import java.util.Map;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ArrayList;

import ggc.exceptions.*;
import java.util.Comparator;

/**
 * Class Warehouse implements a warehouse.
 */
public class Warehouse implements Serializable {

  /** Serial number for serialization. */
  private static final long serialVersionUID = 202109192006L;

  /** Name of file storing current store. */
  private int _date = 0;

  /** Stores the available balance. */
  private double _balance = 0;

  /** Accounting balance */
  private double _globalBalance = 0;

  /** Products */
  private Map<String, Product> _products = new TreeMap<>(new CollatorWrapper());

  /** Partners */
  private Map<String, Partner> _partners = new TreeMap<>(new CollatorWrapper());

  /** Transactions */
  private Map<Integer, Transaction> _transactions = new TreeMap<>();

  /** Batches */
  private Map<String, List<Batch>> _batches = new TreeMap<>();

  private int _transactionNumber = 0;

  /**
   * @return the actual date
   */
  public int getDate() {
    return _date;
  }

  /**
   * Change Balance.
   */
  private void changeBalance(double amount) {
    _balance += amount;
  }

  /**
   * Change GlobalBalance.
   */
  private void changeGlobalBalance(double amount) {
    _globalBalance += amount;
  }

  /**
   * @return the available balance
   */
  public double getBalance() {
    return _balance;
  }

  /**
   * @return the accounting balance
   */
  public double getGlobalBalance() {
    return _globalBalance;
  }

  /**
   * Advances the date.
   * 
   * @param amount the amount to increase in date
   * @throws InvalidAdvanceDateException
   */
  public void advanceDate(int amount) throws InvalidAdvanceDateException {
    if (amount <= 0)
      throw new InvalidAdvanceDateException(amount);
    // misses updating global balance base on penalties
    _date += amount;
    updateBalance();
  }

  // updates balance based on todays penalties
  public void updateBalance() {
    double total = 0;
    for (Transaction transc : _transactions.values())
      total += transc.DifferenceInPrices(new PrintPriceSale(getDate()));
    changeGlobalBalance(total);
  }

  /**
   * Regists a Partner on the WareHouse.
   * 
   * @param id      the partner´s id
   * @param name    the partner´s name
   * @param address the partner´s address
   * @throws DuplicatePartnerException
   */
  public void registerPartner(String id, String name, String address) throws DuplicatePartnerException {
    if (partnerExists(id))
      throw new DuplicatePartnerException(id);
    Partner partner = new Partner(id, name, address);
    _partners.put(id.toUpperCase(), partner);
    addPartnerToNotifications(partner);
  }

  /**
   * Verifies the existence of a Partner, with the given id.
   * 
   * @param id the partner id to be searched
   * @return a boolean representing the existence of a partner
   */
  public boolean partnerExists(String id) {
    return _partners.containsKey(id.toUpperCase());
  }

  /**
   * @param id the partners id
   * @return the partner
   * @throws NoSuchPartnerException
   */
  public Partner getPartner(String id) throws NoSuchPartnerException {
    if (!_partners.containsKey(id.toUpperCase()))
      throw new NoSuchPartnerException(id);
    return _partners.get(id.toUpperCase());
  }

  /**
   * @param id the partners id
   * @return the partner's String
   * @throws NoSuchPartnerException
   */
  public String printPartner(String id) throws NoSuchPartnerException {
    if (!_partners.containsKey(id.toUpperCase()))
      throw new NoSuchPartnerException(id);
    return _partners.get(id.toUpperCase()).toStringWNotifications();
  }

  /**
   * Return all the associated partners as an unmodifiable collection.
   * 
   * @return a collection with all the partners.
   */
  public Collection<Partner> getPartners() {
    if (_partners.size() != 0)
      return Collections.unmodifiableCollection(_partners.values());
    return null;
  }

  /**
   * Regists a Product on the WareHouse.
   * 
   * @param id    the products id
   * @param price the products price
   * @param stock the amount in stock
   */
  public void registerProduct(String id, double price, int stock) {
    Product product = new Product(id, price, stock);
    _products.put(id.toUpperCase(), product);
  }

  /**
   * Regists a Derive Product on the Warehouse.
   * 
   * @param id          the products id
   * @param price       the products price
   * @param stock       the amount in stock
   * @param aggravation the aggravation
   * @param recipe      the products who make the final Product
   */
  public void registerDeriveProduct(String id, double price, int stock, double aggravation,
      Map<Product, Integer> recipe) {
    Product product = new DeriveProduct(id, price, stock, aggravation, recipe);
    _products.put(id.toUpperCase(), product);
  }

  /**
   * Verifies the existence of a Partner, with the given id.
   * 
   * @param id
   */
  public boolean productExists(String id) {
    return _products.containsKey(id.toUpperCase());
  }

  /**
   * @param id the product id
   * @return the product
   */
  public Product getProduct(String id) throws NoSuchProductException {
    /*
     * No exception needed to be thrown yet , as it´s all handle before with
     * productExists in the methods that call this one.
     */
    if (!_products.containsKey(id.toUpperCase()))
      throw new NoSuchProductException(id);
    return _products.get(id.toUpperCase());
  }

  /**
   * Returns all the products as an unmodifiable collection.
   * 
   * @return a collection with all the products.
   */
  public Collection<Product> getProducts() {
    if (_products.size() != 0)
      return Collections.unmodifiableCollection(_products.values());
    return null;
  }

  /**
   * Simple function to parse basic information about partners and batches.
   * 
   * PARTNER|id|name|address
   * 
   * BATCH_S|idProduct|idProduct|price|stock
   * 
   * BATCH_M|idProduct|idProduct|price|stock|aggravation|component-1:amount-1#...#component-n:amount-n
   *
   * @param filename input file
   * @throws IOException
   * @throws BadEntryException
   * @throws DuplicatePartnerException
   * @throws NoSuchPartnerException
   */
  void importFile(String txtfile)
      throws IOException, BadEntryException, DuplicatePartnerException, NoSuchPartnerException, NoSuchProductException {
    {

      BufferedReader in = new BufferedReader(new FileReader(txtfile));
      String s;
      while ((s = in.readLine()) != null) {
        String line = new String(s.getBytes(), "UTF-8");
        if (line.charAt(0) == '#')
          continue;
        String[] fields = line.split("\\|");
        switch (fields[0]) {

        case "PARTNER" -> registerPartner(fields[1], fields[2], fields[3]);
        case "BATCH_S" -> registerSimpleBatch(fields[1], fields[2], Double.parseDouble(fields[3]),
            Integer.parseInt(fields[4]));
        case "BATCH_M" -> registerDeriveBatch(fields[1], fields[2], Double.parseDouble(fields[3]),
            Integer.parseInt(fields[4]), Double.parseDouble(fields[5]), fields[6]);
        default -> throw new BadEntryException(fields[0]);

        }
      }

    }
  }

  /**
   * Regists a new Batch on the WareHouse.
   * 
   * @param product the product related to the batch
   * @param partner the batch´s partner
   * @param stock   the amount in stock
   * @param price   the maximum price
   */
  public Batch registerBatch(Product product, Partner partner, int stock, double price) {
    Batch batch = new Batch(product, partner, stock, price);
    List<Batch> list = _batches.get(product.getId());
    if (list == null) {
      list = new ArrayList<>();
    }
    list.add(batch);
    product.setBatch(list);
    _batches.put(product.getId().toUpperCase(), list);
    return batch;
  }

  /**
   * Removes batch from WH database.
   * 
   * @param batch the batch to be removed
   */
  public void removeBatch(Batch batch) {
    String productId = batch.getProductId();
    List<Batch> list = _batches.get(productId);
    if (list != null) {
      if (list.size() == 1) {
        _batches.remove(productId);
      }
      list.remove(batch);
    }

  }

  /**
   * Returns all the batches.
   * 
   * @return a list with all the batches.
   */
  public List<Batch> getBatches() {
    List<Batch> result = new ArrayList<>();
    for (Map.Entry<String, List<Batch>> lst : _batches.entrySet())
      if (lst.getValue() != null)
        result.addAll(lst.getValue());
    Collections.sort(result, new BatchComparator());
    return result;
  }

  /**
   * Registers a batch of a simple product in the Warehouse.
   * 
   * @param idProduct the product's id
   * @param idPartner the partner´s id
   * @param price     the maximum price
   * @param stock     the amount in stock
   * @throws NoSuchPartnerException
   */
  public void registerSimpleBatch(String idProduct, String idPartner, double price, int stock)
      throws NoSuchPartnerException, NoSuchProductException {

    /** If given product doesn´t exist already ,one is created. **/
    if (!productExists(idProduct)) {
      registerProduct(idProduct, price, 0);
      addNotifications(getProduct(idProduct));
    }
    Product product = getProduct(idProduct);

    /** Update the stock and maximum price, if needed. **/
    product.increaseStock(stock);
    if (price > product.getPrice())
      product.setMaxPrice(price);
    Partner partner = getPartner(idPartner);

    registerBatch(product, partner, stock, price);
  }

  /**
   * Registers a batch of a derive product in the Warehouse.
   * 
   * @param idProduct   the derive product's id
   * @param idPartner   the partner´s id
   * @param price       the maximum price
   * @param stock       the amount in stock
   * @param aggravation the aggravation factor on price
   * @param components  all compoments that make final product
   * @throws NoSuchPartnerException
   */
  public void registerDeriveBatch(String idProduct, String idPartner, double price, int stock, double aggravation,
      String components) throws NoSuchPartnerException, NoSuchProductException {

    /*
     * If product doesn´t exist one is created with the given recipe. It´s assumed
     * all Derive Products are made of already existing Products(components) when
     * read only from .import files.
     */
    if (!productExists(idProduct)) {
      Map<Product, Integer> recipe = createRecipe(components);
      registerDeriveProduct(idProduct, price, 0, aggravation, recipe);
      addNotifications(getProduct(idProduct));
    }

    Product product = getProduct(idProduct);
    Partner partner = getPartner(idPartner);

    /** Update the stock and maximum price, if needed. **/
    product.increaseStock(stock);
    if (price > product.getPrice())
      product.setMaxPrice(price);
    registerBatch(product, partner, stock, price);
  }

  /**
   * @param id the partners id
   * @return a list of all partner´s batches
   * @throws NoSuchPartnerException
   */
  public List<Batch> getBatchesByPartner(String idPartner) throws NoSuchPartnerException {
    if (!partnerExists(idPartner))
      throw new NoSuchPartnerException(idPartner);
    List<Batch> batchesByGivenPartner = new ArrayList<Batch>();
    if (batchesByGivenPartner != null) {
      for (Batch batch : getBatches()) {
        if (batch.getPartnerId().equals(idPartner))
          batchesByGivenPartner.add(batch);
      }

      Collections.sort(batchesByGivenPartner, new BatchComparator());
    }
    return batchesByGivenPartner;
  }

  /**
   * @param id the products id
   * @return a list of all product´s batches
   * @throws NoSuchPartnerException
   */
  public List<Batch> getBatchesByProduct(String idProduct) throws NoSuchProductException {
    if (!productExists(idProduct))
      throw new NoSuchProductException(idProduct);
    List<Batch> getBatchesByProduct = getProduct(idProduct).getBatches();
    if (getBatchesByProduct != null)
      Collections.sort(getBatchesByProduct, new BatchComparator());
    return getBatchesByProduct;
  }

  /**
   * @param price max price to lookup for
   * @return all batches under given price
   * @throws NoSuchPartnerException
   */
  public List<Batch> getBatchesUnderPrice(double price) {
    List<Batch> list = new ArrayList<Batch>();
    if (price > 0) {
      List<Batch> allBatches = new ArrayList<Batch>();
      for (List<Batch> s : _batches.values()) {
        allBatches.addAll(s);
      }
      for (Batch batch : allBatches) {
        if (batch.getPrice() < price)
          list.add(batch);
      }
      Collections.sort(list, new BatchComparator());
    }
    return list;
  }

  /**
   * Regists a Transaction on the WareHouse.
   * 
   * @param transaction a transaction
   */
  public void registerTransaction(Transaction transaction) {
    transaction.setId(_transactionNumber);
    _transactions.put(_transactionNumber++, transaction);
    transaction.getPartner().storeTransaction(transaction);
  }

  /**
   * Handle the action of buying an already existing product on the WareHouse.
   * Buys the given amount, for the given price from the given partner id.
   * 
   * @param idProduct the product
   * @param idPartner the partner
   * @param price     the price
   * @param amount    the amount
   * @throws NoSuchProductException
   * @throws NoSuchPartnerException
   */
  public void buyExistingProduct(String idProduct, String idPartner, double price, int amount)
      throws NoSuchProductException, NoSuchPartnerException {
    Product product = getProduct(idProduct);
    Partner partner = getPartner(idPartner); // throws NoSuchPartnerException
    if (price > product.getPrice())
      product.setMaxPrice(price);
    product.sendNotification(price, amount); // case stock =0 , now its different - conditions to actually send the
    // notification held inside
    product.increaseStock(amount);
    buyProduct(product, partner, price, amount);
  }

  /**
   * Registers the the purchased products on the WareHouse,as well as the
   * respective transaction and batch.
   * 
   * @param idProduct the product
   * @param idPartner the partner
   * @param price     the price
   * @param amount    the amount
   */
  public void buyProduct(Product product, Partner partner, double price, int amount) {
    Transaction transaction = new Acquisiton(product, partner, amount, price, getDate());
    registerTransaction(transaction);
    registerBatch(product, partner, amount, price);
    changeBalance(-(price * amount)); // immediat payment
    changeGlobalBalance(-(price * amount));

  }

  /**
   * * Handles the action of buying an non-existing Simple product on the
   * WareHouse. Buys the given amount, for the given price from the given partner
   * id.
   * 
   * @param idProduct the product
   * @param idPartner the partner
   * @param price     the price
   * @param amount    the amount
   * @throws NoSuchPartnerException
   * @throws NoSuchProductException
   */
  public void buySimpleProduct(String idProduct, String idPartner, double price, int amount)
      throws NoSuchPartnerException, NoSuchProductException {
    // METHOD ONLY CALLED WHEN PRODUCT DOESNT EXIST

    Partner partner = getPartner(idPartner); // throws NoSuchPartnerException
    registerProduct(idProduct, price, amount);
    Product product = getProduct(idProduct);
    addNotifications(product); // NEW PRODUCT SO ADD NOTIFICATION
    buyProduct(product, partner, price, amount);
  }

  /**
   * * Handles the action of buying an non-existing Derive product on the
   * WareHouse. Buys the given amount, for the given price from the given partner
   * id.
   * 
   * @param idProduct   the product
   * @param idPartner   the partner
   * @param price       the price
   * @param amount      the amount
   * @param aggravation the aggravation on creating the product
   * @param recipeMap   the product´s recipe
   * @throws NoSuchPartnerException
   * @throws NoSuchProductException
   */
  public void buyDeriveProduct(String idProduct, String idPartner, double price, int amount, double aggravation,
      Map<Integer, String> recipeMap) throws NoSuchPartnerException, NoSuchProductException {
    // METHOD ONLY CALLED WHEN PRODUCT DOESNT EXIST - !!! by now, no exception
    // launched cuz of no product in recipe

    Map<Product, Integer> recipe = createRecipe(recipeMap);
    Partner partner = getPartner(idPartner); // throws NoSuchPartnerException
    System.out.printf("O parceiro %s existe! \n", partner);
    registerDeriveProduct(idProduct, price, amount, aggravation, recipe);
    Product product = getProduct(idProduct);

    addNotifications(product); // NEW PRODUCT ,SO ADD NOTIFICATION
    buyProduct(product, partner, price, amount);
  }

  /**
   * Returns the recipe in the proper format for the WareHouse to function.
   * 
   * @param recipe the recipe
   * @return the recipe in the proper format
   * @throws NoSuchProductException
   */
  public Map<Product, Integer> createRecipe(Map<Integer, String> recipe) throws NoSuchProductException {
    Map<Product, Integer> recipeFinal = new LinkedHashMap<Product, Integer>();
    for (Map.Entry<Integer, String> entry : recipe.entrySet()) {
      Product component = getProduct(entry.getValue());
      // if (component == null)
      // throw new NoSuchProductException(entry.getValue());
      int amountComponent = entry.getKey();
      recipeFinal.put(component, amountComponent);
    }
    return recipeFinal;
  }

  /**
   * Returns the recipe in the proper format for the WareHouse to function.
   * 
   * @param components the components, and amounts
   * @return the recipe in the proper format
   * @throws NoSuchProductException
   */
  public Map<Product, Integer> createRecipe(String components) throws NoSuchProductException {
    Map<Product, Integer> recipe = new LinkedHashMap<Product, Integer>();
    String[] fields = components.split("#");
    for (int i = 0; i < fields.length; i++) {

      String[] componentAndAmount = fields[i].split(":");
      Product component = getProduct(componentAndAmount[0]);
      int amount = Integer.parseInt(componentAndAmount[1]);

      recipe.put(component, amount);
    }

    return recipe;
  }

  /**
   * Sells a product to given partner, according to all WareHouse rules.
   * 
   * @param idProduct the products id
   * @param idPartner the partners id
   * @param amount    the amount to be sold
   * @param limitDate the limit date for payment
   * @throws UnAvailableProductException
   * @throws NoSuchPartnerException
   * @throws NoSuchProductException
   */
  public void sellProduct(String idProduct, String idPartner, int amount, int limitDate)
      throws UnAvailableProductException, NoSuchPartnerException, NoSuchProductException {
    Partner partner = getPartner(idPartner);
    Product product = getProduct(idProduct);
    double price = product.sell(amount); // throws UnAvailableProductException
    Transaction transaction = new Sale(product, partner, amount, limitDate, price);
    registerTransaction(transaction);
    changeGlobalBalance(price); // update global balance
  }

  /**
   * Handles request of a product breakdown made by partner, according to all
   * WareHouse rules.
   * 
   * @param idProduct the products id
   * @param idPartner the partners id
   * @param amount    the amount
   * @throws UnAvailableProductException
   * @throws NoSuchPartnerException
   * @throws NoSuchProductException
   */
  public void requestBreakDown(String idProduct, String idPartner, int amount)
      throws NoSuchProductException, NoSuchPartnerException, UnAvailableProductException {
    Product product = getProduct(idProduct);
    Partner partner = getPartner(idPartner);
    Breakdown transaction = product.breakDown(partner, amount, getDate()); // actualy do the breakdown, and all needed
                                                                           // verification
    
    
                                                                           if (transaction != null) {
      // meaning a breakdown occured
      registerTransaction(transaction);
      changeBalance(transaction.getToPayValue());
      changeGlobalBalance(transaction.getToPayValue());
    }
  }

  /**
   * @param idTransaction the transactions id
   * @return the transaction
   * @throws NoSuchTransactionException
   */
  public Transaction getTransaction(int idTransaction) throws NoSuchTransactionException {
    if (!_transactions.containsKey(idTransaction) || idTransaction < 0)
      throw new NoSuchTransactionException(idTransaction);
    Transaction transaction = _transactions.get(idTransaction);
    transaction.accept(new PrintSale(getDate())); // updates if needed sales , to print
    return transaction;
  }

  /**
   * Handles payment of Sale.
   * 
   * @param idTransaction the transactions id
   * @throws NoSuchTransactionException
   */
  public void paySale(int idTransaction) throws NoSuchTransactionException {
    if (!_transactions.containsKey(idTransaction) || idTransaction < 0)
      throw new NoSuchTransactionException(idTransaction);
    double price = _transactions.get(idTransaction).pay(getDate());
    if (price > 0)
      changeBalance(price);
  }

  /**
   * Returns all the Acquisitons transactions with partner.
   * 
   * @param idPartner the partners id
   * @return a list with the asked transactions
   * @throws NoSuchPartnerException
   */
  public List<Transaction> getAcquisitionsWPartner(String idPartner) throws NoSuchPartnerException {
    Partner partner = getPartner(idPartner);
    List<Transaction> list = new ArrayList<>();
    for (Transaction el : partner.getTransactions()) {
      Transaction transaction = el.accept(new PrintAcquisition());
      if (transaction != null)
        list.add(transaction);
    }
    return list;
  }

  /**
   * Returns all the Sales/Breakdowns transactions with partner.
   * 
   * @param idPartner the partners id
   * @return a list with the asked transactions
   * @throws NoSuchPartnerException
   */
  public List<Transaction> getSalesWPartner(String idPartner) throws NoSuchPartnerException {
    Partner partner = getPartner(idPartner);
    List<Transaction> list = new ArrayList<>();
    for (Transaction el : partner.getTransactions()) {
      Transaction transaction = el.accept(new PrintSale(getDate()));
      if (transaction != null) {
        list.add(transaction);

      }

    }
    return list;
  }

  /**
   * Returns all the Payed transactions by partner.
   * 
   * @param idPartner the partners id
   * @return a list with the asked transactions
   * @throws NoSuchPartnerException
   */
  public List<Transaction> getPaidTransactionsByPartner(String idPartner) throws NoSuchPartnerException {
    Partner partner = getPartner(idPartner);
    List<Transaction> list = new ArrayList<>();
    for (Transaction el : partner.getTransactions()) {
      Transaction transaction = el.accept(new PrintSale(getDate()));
      if (transaction != null && transaction.isPayed())
        list.add(transaction);

    }
    return list;
  }

  /**
   * Toggles Product Notifications associated with given partner and product.
   * 
   * @param idPartner the partners id
   * @param idProduct the products id
   * @throws NoSuchPartnerException
   * @throws NoSuchProductException
   */
  public void toggleProductNotifications(String idPartner, String idProduct)
      throws NoSuchPartnerException, NoSuchProductException {
    Partner partner = getPartner(idPartner);
    Product product = getProduct(idProduct);
    if (product.isObserver(partner)) {
      product.removeObserver(partner);
    } else {
      product.registerObserver(partner);
    }
  }

  /**
   * Associate all partners as interested in receiving notifications of the given
   * product.
   * 
   * @param product the product
   */
  public void addNotifications(Product product) {
    for (Map.Entry<String, Partner> observer : _partners.entrySet())
      product.registerObserver(observer.getValue());
  }

  public void addPartnerToNotifications(Partner partner) {
    for (Product product : _products.values())
      product.registerObserver(partner);
  }

}