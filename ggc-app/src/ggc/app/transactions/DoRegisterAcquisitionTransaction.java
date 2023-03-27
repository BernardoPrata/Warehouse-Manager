package ggc.app.transactions;

import pt.tecnico.uilib.menus.Command;
import pt.tecnico.uilib.menus.CommandException;

import java.util.Map;
import java.util.LinkedHashMap;

import ggc.WarehouseManager;
import pt.tecnico.uilib.forms.Form;
import ggc.exceptions.NoSuchPartnerException;
import ggc.exceptions.NoSuchProductException;
import ggc.app.exceptions.UnknownPartnerKeyException;
import ggc.app.exceptions.UnknownProductKeyException;

/**
 * Register order.
 */
public class DoRegisterAcquisitionTransaction extends Command<WarehouseManager> {

  public DoRegisterAcquisitionTransaction(WarehouseManager receiver) {
    super(Label.REGISTER_ACQUISITION_TRANSACTION, receiver);
    addStringField("idPartner", Prompt.partnerKey());
    addStringField("idProduct", Prompt.productKey());
    addRealField("price", Prompt.price());
    addIntegerField("amount", Prompt.amount());

  }

  @Override
  public final void execute() throws CommandException {
    try {
      // If product already exists on WH
      _receiver.buyExistingProduct(stringField("idProduct"), stringField("idPartner"), realField("price"),
          integerField("amount"));

    } catch (NoSuchProductException e) {
      // A new Product will be created - A derive or Simple one
      // Este try catch falta aind !!!!!-> Vou fazer aqui, mas acho que tenho q ir ver
      // isto do recipe no
      // core, elançar
      // NoRecipeException e depois é que aqui posso pedir receita neste catch

      try {
        if (Form.confirm(Prompt.addRecipe())) {
          int size = Form.requestInteger(Prompt.numberOfComponents());
          double aggravation = Form.requestReal(Prompt.alpha());
          Map<Integer, String> map = new LinkedHashMap<>();
          for (int i = 0; i < size; i++) {
            String id = Form.requestString(Prompt.productKey());
            int amount = Form.requestInteger(Prompt.amount());
            map.put(amount, id);
          }
          _receiver.buyDeriveProduct(stringField("idProduct"), stringField("idPartner"), realField("price"),
              integerField("amount"), aggravation, map);
        } else {
          _receiver.buySimpleProduct(stringField("idProduct"), stringField("idPartner"), realField("price"),
              integerField("amount"));
        }

      } catch (NoSuchPartnerException e1) {
        throw new UnknownPartnerKeyException(e1.getId());
      } catch (NoSuchProductException e2) {
        throw new UnknownProductKeyException(e2.getId());
      }

    } catch (NoSuchPartnerException e3) {
      throw new UnknownPartnerKeyException(e3.getId());
    }
  }

}
