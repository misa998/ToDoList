package sample;

import datamodel.ToDoData;
import datamodel.ToDoItem;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.LocalDate;

public class DialogController {
    @FXML
    private TextField shortDescription;
    @FXML
    private TextArea detailsArea;
    @FXML
    private DatePicker deadlinePicker;
    @FXML
    private ListView<ToDoItem> todoListView;

    public ToDoItem processResults(){

            String shortDesc = shortDescription.getText().trim();
            String details = detailsArea.getText().trim();
            LocalDate deadlineValue = deadlinePicker.getValue();

            if(shortDesc.isEmpty() || shortDesc.trim().isEmpty()){
                if(details.isEmpty() || details.trim().isEmpty()){
                    Alert alert = new Alert(Alert.AlertType.NONE,"Todo item has to have details", ButtonType.OK);
                    alert.setTitle("No details");
                    alert.show();
                    return null;
                }else if(details.contains(details)) {
                    shortDesc = emptyFields(shortDesc);
                }
            }
            if(deadlineValue == null){
                deadlineValue = LocalDate.now();
            }

            ToDoItem newItem = new ToDoItem(shortDesc, details, deadlineValue);
            ToDoData.getInstance().addTodoItem(newItem);
            return newItem;
    }

    public void showSelected(ToDoItem selectedItem){
        shortDescription.setText(selectedItem.getShortDesc());
        detailsArea.setText(selectedItem.getDetails());
        deadlinePicker.setValue(selectedItem.getDeadline());
    }

    public String emptyFields(String shortDesc){
        shortDesc = detailsArea.getText().trim();               // preuzima unesen tekst
        String[] kkk = shortDesc.split(" ", 4);      // taj tekst pretvara u niz podeljen razmakom na par dela
        if(kkk.length >= 4) {                                   // proverava stanje delova
//            StringBuilder sb = new StringBuilder();
//            sb.append(kkk[0]); sb.append(" "); sb.append(kkk[1]);   // string builder spaja prva 3 stringa iz niz i odvaja ih razmakom
//            sb.append(" "); sb.append(kkk[2]);
//            return shortDesc = sb.toString();                       // sb.toString() mu daje krajnji rezultat

            String output = kkk[0] + " " + kkk[1] + " " + kkk[2];
            return output;
        }else
            return shortDesc;

    }

    public void processEdit(ToDoItem item){
        item.setShortDesc(shortDescription.getText().trim());
        item.setDetails(detailsArea.getText().trim());
        item.setDeadline(deadlinePicker.getValue());

        if(item.getShortDesc().isEmpty() || item.getShortDesc().trim().isEmpty()){
            item.setShortDesc(emptyFields(item.getShortDesc()));
        }

//        ToDoItem newItem = new ToDoItem(shortDesc, details, deadlineValue);
//        ToDoData.getInstance().addTodoItem(newItem);
//        return newItem;
    }
}
