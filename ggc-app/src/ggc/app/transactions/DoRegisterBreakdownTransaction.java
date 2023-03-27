package ggc.app.transactions;

import pt.tecnico.uilib.menus.Command;
import pt.tecnico.uilib.menus.CommandException;
import ggc.WarehouseManager;
import ggc.exceptions.UnAvailableProductException;
import ggc.exceptions.NoSuchPartnerException;
import ggc.exceptions.NoSuchProductException;
import ggc.app.exceptions.UnavailableProductException;
import ggc.app.exceptions.UnknownPartnerKeyException;
import ggc.app.exceptions.UnknownProductKeyException;

/**
 * Register order.
 */
public class DoRegisterBreakdownTransaction extends Command<WarehouseManager> {

  public DoRegisterBreakdownTransaction(WarehouseManager receiver) {
    super(Label.REGISTER_BREAKDOWN_TRANSACTION, receiver);
    addStringField("idPartner", Prompt.partnerKey());
    addStringField("idProduct", Prompt.productKey());
    addIntegerField("amount", Prompt.amount());
  }

  @Override
  public final void execute() throws CommandException {
    try {
      _receiver.requestBreakDown(stringField("idProduct"), stringField("idPartner"), integerField("amount"));
    } catch (UnAvailableProductException e) {
      throw new UnavailableProductException(e.getKey(), e.getRequested(), e.getAvailable());
    } catch (NoSuchPartnerException e1) {
      throw new UnknownPartnerKeyException(e1.getId());
    } catch (NoSuchProductException e2) {
      throw new UnknownProductKeyException(e2.getId());
    }
  }

}
