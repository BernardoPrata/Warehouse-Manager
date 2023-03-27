package ggc.app.transactions;

import pt.tecnico.uilib.menus.Command;
import pt.tecnico.uilib.menus.CommandException;
import ggc.WarehouseManager;
import ggc.exceptions.*;
import ggc.app.exceptions.*;

/**
 * 
 */
public class DoRegisterSaleTransaction extends Command<WarehouseManager> {

  public DoRegisterSaleTransaction(WarehouseManager receiver) {
    super(Label.REGISTER_SALE_TRANSACTION, receiver);
    addStringField("idPartner", Prompt.partnerKey());
    addIntegerField("paymentDeadline", Prompt.paymentDeadline());
    addStringField("idProduct", Prompt.productKey());
    addIntegerField("amount", Prompt.amount());

  }

  @Override
  public final void execute() throws CommandException {
    try {
      _receiver.sellProduct(stringField("idProduct"), stringField("idPartner"), integerField("amount"),
          integerField("paymentDeadline"));
    } catch (UnAvailableProductException e) {
      throw new UnavailableProductException(e.getKey(), e.getRequested(), e.getAvailable());
    } catch (NoSuchPartnerException e1) {
      throw new UnknownPartnerKeyException(e1.getId());
    } catch (NoSuchProductException e2) {
      throw new UnknownProductKeyException(e2.getId());
    }
  }

}
