package ggc.app.transactions;

import pt.tecnico.uilib.menus.Command;
import pt.tecnico.uilib.menus.CommandException;
import ggc.WarehouseManager;
import ggc.exceptions.NoSuchTransactionException;
import ggc.app.exceptions.UnknownTransactionKeyException;

/**
 * Receive payment for sale transaction.
 */
public class DoReceivePayment extends Command<WarehouseManager> {

  public DoReceivePayment(WarehouseManager receiver) {
    super(Label.RECEIVE_PAYMENT, receiver);
    addIntegerField("idSale", Prompt.transactionKey());

  }

  @Override
  public final void execute() throws CommandException {
    try {
      _receiver.paySale(integerField("idSale"));
    } catch (NoSuchTransactionException e) {
      throw new UnknownTransactionKeyException(e.getId());
    }
  }

}
