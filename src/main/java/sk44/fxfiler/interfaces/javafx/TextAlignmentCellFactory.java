/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sk44.fxfiler.interfaces.javafx;

import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

/**
 * http://stackoverflow.com/questions/13455326/javafx-tableview-text-alignment
 *
 * @author sk
 */
public class TextAlignmentCellFactory<S> implements Callback<TableColumn<S, String>, TableCell<S, String>> {

    public enum Alignment {

        LEFT("CENTER-LEFT"), RIGHT("CENTER-RIGHT");
        private final String style;

        private Alignment(String style) {
            this.style = style;
        }
    }

    public TextAlignmentCellFactory(Alignment alignment) {
        this.alignment = alignment;
    }
    private final Alignment alignment;

    @Override
    public TableCell<S, String> call(TableColumn<S, String> p) {
        TableCell<S, String> cell = new TableCell<S, String>() {
            @Override
            public void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : getString());
                setGraphic(null);
            }

            private String getString() {
                return getItem() == null ? "" : getItem().toString();
            }
        };

        cell.setStyle("-fx-alignment: " + alignment.style + ";");
        return cell;
    }
}
