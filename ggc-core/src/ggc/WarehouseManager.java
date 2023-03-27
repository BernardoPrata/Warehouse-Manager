package ggc;

import java.io.*;
import ggc.exceptions.*;
import java.util.Map;
import java.util.Collection;
import java.util.DuplicateFormatFlagsException;
import java.util.List;

/** Façade for access. */
public class WarehouseManager {

  /** Name of file storing current store. */
  private String _filename = "";

  /** The warehouse itself. */
  private Warehouse _warehouse = new Warehouse();

  /**
   * @@throws IOException
   * @@throws FileNotFoundException
   * @@throws MissingFileAssociationException
   */
  public void save() throws IOException, FileNotFoundException, MissingFileAssociationException {
    if (!hasAssociatedFileName())
      throw new MissingFileAssociationException();
    try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(_filename))) {
      out.writeObject(_warehouse);
      out.close();
    }
  }

  /**
   * @@param filename
   * @@throws MissingFileAssociationException
   * @@throws IOException
   * @@throws FileNotFoundException
   */
  public void saveAs(String filename) throws MissingFileAssociationException, FileNotFoundException, IOException {
    _filename = filename;
    save();
  }

  /**
   * @@param filename
   * @@throws UnavailableFileException
   */
  public void load(String filename) throws UnavailableFileException {
    try {
      ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(new FileInputStream(filename)));
      _warehouse = (Warehouse) ois.readObject();
      ois.close();
      _filename = filename;
    } catch (IOException | ClassNotFoundException e) {
      throw new UnavailableFileException(filename);
    }

  }

  /**
   * @param textfile
   * @throws ImportFileException
   */
  public void importFile(String textfile) throws ImportFileException {
    try {
      _warehouse.importFile(textfile);
    } catch (IOException | BadEntryException | NoSuchProductException | DuplicatePartnerException
        | NoSuchPartnerException e) {
      throw new ImportFileException(textfile);
    }
  }

  /**
   * @param daysToAdvance
   * @throws ImportFileException
   * 
   */
  public void advanceDate(int amount) throws InvalidAdvanceDateException {
    _warehouse.advanceDate(amount);
  }

  /**
   * Return the present WareHouse date.
   * 
   * @return integer representing the data
   */
  public int displayDate() {
    return _warehouse.getDate();
  }

  /**
   * @param id
   * @param name
   * @param address
   * @throws DuplicatePartnerException
   */
  public void registerPartner(String id, String name, String address) throws DuplicatePartnerException {
    _warehouse.registerPartner(id, name, address);

  }

  /**
   * Return all the partners as an unmodifiable collection.
   * 
   * @return a collection with all the partners
   */
  public Collection<Partner> partners() {
    return _warehouse.getPartners();
  }

  /**
   * @param id the partner number.
   * @return the partner
   * @throws NoSuchPartnerException
   */
  public String partner(String id) throws NoSuchPartnerException {
    return _warehouse.printPartner(id);

  }

  /**
   * Return all the products as an unmodifiable collection.
   * 
   * @return a collection with all the products
   */
  public Collection<Product> products() {
    return _warehouse.getProducts();
  }

  /**
   * Return all the products as an unmodifiable collection.
   * 
   * @return a collection with all the products
   */
  public List<Batch> batches() {
    return _warehouse.getBatches();
  }

  /**
   * @return a boolean meaning if WHM has associated File
   */
  public boolean hasAssociatedFileName() {
    boolean value = _filename.equals("");
    return !value;
  }

  /**
   * @return the available balance
   */
  public double getBalance() {
    return _warehouse.getBalance();
  }

  /**
   * @return the accounting balance
   */
  public double getGlobalBalance() {
    return _warehouse.getGlobalBalance();
  }

  public List<Batch> getBatchesByPartner(String idPartner) throws NoSuchPartnerException {
    return _warehouse.getBatchesByPartner(idPartner);
  }

  public List<Batch> getBatchesByProduct(String idProduct) throws NoSuchProductException {
    return _warehouse.getBatchesByProduct(idProduct);
  }

  public void buyExistingProduct(String idProduct, String idPartner, double price, int amount)
      throws NoSuchProductException, NoSuchPartnerException {
    _warehouse.buyExistingProduct(idProduct, idPartner, price, amount);
  }

  public void buySimpleProduct(String idProduct, String idPartner, double price, int amount)
      throws NoSuchPartnerException, NoSuchProductException {
    _warehouse.buySimpleProduct(idProduct, idPartner, price, amount);
  }

  public void buyDeriveProduct(String idProduct, String idPartner, double price, int amount, double aggravation,
      Map<Integer, String> recipeMap) throws NoSuchPartnerException, NoSuchProductException {
    _warehouse.buyDeriveProduct(idProduct, idPartner, price, amount, aggravation, recipeMap);
  }

  public void sellProduct(String idProduct, String idPartner, int amount, int limitDate)
      throws UnAvailableProductException, NoSuchPartnerException, NoSuchProductException {
    _warehouse.sellProduct(idProduct, idPartner, amount, limitDate);
  }

  public void requestBreakDown(String idProduct, String idPartner, int amount)
      throws NoSuchProductException, NoSuchPartnerException, UnAvailableProductException {
    _warehouse.requestBreakDown(idProduct, idPartner, amount);
  }

  public Transaction getTransaction(int idTransaction) throws NoSuchTransactionException {
    return _warehouse.getTransaction(idTransaction);
  }

  public void paySale(int idTransaction) throws NoSuchTransactionException {
    _warehouse.paySale(idTransaction);
  }

  public List<Transaction> getAcquisitionsWPartner(String idPartner) throws NoSuchPartnerException {
    return _warehouse.getAcquisitionsWPartner(idPartner);
  }

  public List<Transaction> getSalesWPartner(String idPartner) throws NoSuchPartnerException {
    return _warehouse.getSalesWPartner(idPartner);
  }

  public List<Transaction> getPaidTransactionsByPartner(String idPartner) throws NoSuchPartnerException {
    return _warehouse.getPaidTransactionsByPartner(idPartner);
  }

  public List<Batch> getBatchesUnderPrice(double price) {
    return _warehouse.getBatchesUnderPrice(price);
  }

  public void toggleProductNotifications(String idPartner, String idProduct)
      throws NoSuchPartnerException, NoSuchProductException {
    _warehouse.toggleProductNotifications(idPartner, idProduct);
  }
}
