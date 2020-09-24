package sample;

import datamodel.ToDoData;
import datamodel.ToDoItem;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.util.Callback;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.Optional;
import java.util.function.Predicate;

public class Controller {

    @FXML
    private ListView<ToDoItem> todoListView;
    @FXML
    private TextArea textArea;
    @FXML
    private Label deadlineLabel;
    @FXML
    private BorderPane mainBorderPane;
    @FXML
    private ContextMenu listContextMenu;
    @FXML
    private ToggleButton filterToggleButton;

    private FilteredList<ToDoItem> filteredList;
    private Predicate<ToDoItem> wantAllItems;
    private Predicate<ToDoItem> wantTodaysItems;

    public void initialize(){
        listContextMenu = new ContextMenu();
        MenuItem deleteMenuItem = new MenuItem("Delete");               //adding item
        deleteMenuItem.setOnAction(new EventHandler<ActionEvent>() {       //handler for handling right click on item
            @Override
            public void handle(ActionEvent actionEvent) {
                ToDoItem item = todoListView.getSelectionModel().getSelectedItem();
                deleteItem(item);
            }
        });
        MenuItem editItem = new MenuItem("Edit");
        editItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                showEditItemDialog();
            }
        });
        listContextMenu.getItems().addAll(deleteMenuItem, editItem);                  //adding items

        todoListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<ToDoItem>() {            //listener for any change made on a listView item
            @Override
            public void changed(ObservableValue<? extends ToDoItem> observableValue, ToDoItem toDoItem, ToDoItem t1) {  //showing details of selected item, if any
                if(t1 != null){
                    ToDoItem item = todoListView.getSelectionModel().getSelectedItem();
                    textArea.setText(item.getDetails());
                    DateTimeFormatter df = DateTimeFormatter.ofPattern("MMMM d, yyyy");
                    deadlineLabel.setText(df.format(item.getDeadline()));
                } else if(t1 == null){                                                                                  //if no items exist, showing nothing
                    textArea.setText("");
                    deadlineLabel.setText("");
                }
            }
        });
        wantAllItems = new Predicate<ToDoItem>() {
            @Override
            public boolean test(ToDoItem item) {
                return true;
            }
        };
        wantTodaysItems = new Predicate<ToDoItem>() {
            @Override
            public boolean test(ToDoItem item) {
                return (item.getDeadline().equals(LocalDate.now()));
            }
        };
        filteredList = new FilteredList<>(ToDoData.getInstance().getToDoItems(), wantAllItems);

        //using observable arraylist to store the todolist items but to wrap it in a sorted list instance and than use the sorted list to populate the list view
        SortedList<ToDoItem> sortedList = new SortedList<ToDoItem>(filteredList, new Comparator<ToDoItem>() {      //comparator used as an anonymous class
            @Override
            public int compare(ToDoItem o1, ToDoItem o2) {                                                          //returns negative value of o1 is less than o2
                return o1.getDeadline().compareTo(o2.getDeadline());
            }
        });
        todoListView.setItems(sortedList);
                          //getting data from singleton bellow
        todoListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);        //only one item can be selected at the time
        todoListView.getSelectionModel().selectFirst();

        todoListView.setCellFactory(new Callback<ListView<ToDoItem>, ListCell<ToDoItem>>() {
            @Override
            public ListCell<ToDoItem> call(ListView<ToDoItem> toDoItemListView) {       //we've set a cell factory by calling the ListView setCellFactory() and we pass an anonymous class that implements the callBack interface
                ListCell<ToDoItem> cell = new ListCell<>(){
                    @Override
                    protected void updateItem(ToDoItem item, boolean b) {               //runs whenever the ListView wants to paint a single cell
                        super.updateItem(item, b);
                        if(b){
                            setText(null);
                        } else {
                            setText(item.getShortDesc());
                            if(item.getDeadline().isBefore(LocalDate.now())){
                                setTextFill(Color.valueOf("AAAAAA"));
                            } else if(item.getDeadline().equals(LocalDate.now())){
                                setTextFill(Color.valueOf("DD0000"));
                            } else if(item.getDeadline().equals(LocalDate.now().plusDays(1))) {
                                setTextFill(Color.valueOf("CC6600"));
                            } else if(item.getDeadline().equals(LocalDate.now().plusDays(2)) || item.getDeadline().isAfter(LocalDate.now().plusDays(2))) {
                                setTextFill(Color.valueOf("111111"));
                            }
                        }
                    }
                };
                cell.emptyProperty().addListener(
                        (obs, wasEmpty, isNowEmpty) -> {
                            if (isNowEmpty) {
                                cell.setContextMenu(null);              //turning context menu off, if the cell is empty
                            } else {
                                cell.setContextMenu(listContextMenu);
                            }
                });
                return cell;
            }
        });
    }

    @FXML
    public void handleKeyPressed(KeyEvent keyEvent){
        ToDoItem selectedItem = todoListView.getSelectionModel().getSelectedItem();
        if(selectedItem != null){
            if(keyEvent.getCode().equals(KeyCode.DELETE)){
                deleteItem(selectedItem);
            }else if(keyEvent.getCode().equals(KeyCode.ENTER)){
                showEditItemDialog();
            }
        }
    }

    @FXML
    public void showNewItemDialog(){
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(mainBorderPane.getScene().getWindow());
        dialog.setTitle("Add new Todo item");
        dialog.setHeaderText("Use this dialog to create a new todo item");
        FXMLLoader fxmlLoader = new FXMLLoader();                                       //loading fxml in steps bellow
        fxmlLoader.setLocation(getClass().getResource("todoItemDialog.fxml"));
        try{
            dialog.getDialogPane().setContent(fxmlLoader.load());
        } catch(IOException e){
            System.out.println("Couldn't load the dialog");
            e.printStackTrace();
            return;
        }
        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);         //already defined button types
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);

        Optional<ButtonType> result = dialog.showAndWait();                 //showing the dialog and than handler waits for user interaction
        if(result.isPresent() && result.get() == ButtonType.OK){
            DialogController controller = fxmlLoader.getController();
            ToDoItem newItem = controller.processResults();                             //storing the values that is returned from the process results method
            //           todoListView.getItems().setAll(ToDoData.getInstance().getToDoItems());      //solution for updating the list. Replacing content with anything from the base. It's commented because of data binding
            todoListView.getSelectionModel().select(newItem);                           //this also
            }
    }

    @FXML
    public void showEditItemDialog(){
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(mainBorderPane.getScene().getWindow());
        dialog.setTitle("Edit a Todo item");
        dialog.setHeaderText("Use this dialog to edit an existing Todo item");
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("todoItemDialog.fxml"));
        try{
            dialog.getDialogPane().setContent(fxmlLoader.load());
        } catch(IOException e){
            System.out.println("Couldn't load the dialog");
            e.printStackTrace();
            return;
        }
        DialogController controller = fxmlLoader.getController();
        ToDoItem selectedItem = todoListView.getSelectionModel().getSelectedItem();

        if(selectedItem == null){
            Alert alert = new Alert(Alert.AlertType.NONE,"There is nothing to edit", ButtonType.OK);
            alert.setTitle("No items");
            alert.show();
        }else if(selectedItem != null) {
            controller.showSelected(selectedItem);

            dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
            dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);

            Optional<ButtonType> result = dialog.showAndWait();

            if (result.isPresent() && result.get() == ButtonType.OK) {
                controller.processEdit(selectedItem);
                todoListView.getCellFactory().call(todoListView);                                          //for coloring items after creating new ones
                initialize();
                todoListView.getSelectionModel().select(selectedItem);
            }
        }
    }

    public void deleteItem(ToDoItem item){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);                          //before delete
        alert.setTitle("Delete Todo item");
        alert.setHeaderText("Delete item: " + item.getShortDesc());
        alert.setContentText("Are you sure?");
        Optional<ButtonType> result = alert.showAndWait();              //showing the dialog
        if(result.isPresent() && (result.get() == ButtonType.OK)){
            ToDoData.getInstance().deleteTodoItem(item);
        }
    }

    public void handleFilterButton(){
        ToDoItem selectedItem = todoListView.getSelectionModel().getSelectedItem();         //storing the selected item
        if(filterToggleButton.isSelected()){
            filteredList.setPredicate(wantTodaysItems);
            if(filteredList.isEmpty()){
                textArea.setText("");
                deadlineLabel.setText("");
            } else if(filteredList.contains(selectedItem)){
                todoListView.getSelectionModel().select(selectedItem);
            } else {
                todoListView.getSelectionModel().selectFirst();
            }
        } else {
            filteredList.setPredicate(wantAllItems);
            todoListView.getSelectionModel().select(selectedItem);                          //selecting the item after button click
        }
    }
}
