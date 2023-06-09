package ggc.app.main;

import pt.tecnico.uilib.menus.Command;
import pt.tecnico.uilib.menus.CommandException;
import ggc.WarehouseManager;
import ggc.exceptions.MissingFileAssociationException;
import java.io.FileNotFoundException;
import java.io.IOException;
import pt.tecnico.uilib.forms.Form;

/**
 * Save current state to file under current name (if unnamed, query for name).
 */
class DoSaveFile extends Command<WarehouseManager> {

  /** @param receiver */
  DoSaveFile(WarehouseManager receiver) {
    super(Label.SAVE, receiver);

  }

  @Override
  public final void execute() throws CommandException {
    try {
      _receiver.save();
    } catch (MissingFileAssociationException e) {
      try {
        _receiver.saveAs(Form.requestString(Prompt.newSaveAs()));
      } catch (MissingFileAssociationException e1) {
        e1.printStackTrace();
      } catch (IOException e1) {
        e1.printStackTrace();
      }
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

}
