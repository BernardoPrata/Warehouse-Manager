package ggc.app.partners;

import pt.tecnico.uilib.menus.Command;
import pt.tecnico.uilib.menus.CommandException;
import ggc.WarehouseManager;
import ggc.exceptions.NoSuchPartnerException;
import ggc.app.exceptions.UnknownPartnerKeyException;
import ggc.exceptions.NoSuchProductException;
import ggc.app.exceptions.UnknownProductKeyException;

/**
 * Toggle product-related notifications.
 */
class DoToggleProductNotifications extends Command<WarehouseManager> {

  DoToggleProductNotifications(WarehouseManager receiver) {
    super(Label.TOGGLE_PRODUCT_NOTIFICATIONS, receiver);
    addStringField("idPartner", Prompt.partnerKey());
    addStringField("idProduct", Prompt.productKey());

  }

  @Override
  public void execute() throws CommandException {
    try {
      _receiver.toggleProductNotifications(stringField("idPartner"), stringField("idProduct"));
    } catch (NoSuchPartnerException e) {
      throw new UnknownPartnerKeyException(e.getId());
    } catch (NoSuchProductException e1) {
      throw new UnknownProductKeyException(e1.getId());
    }
  }
}
