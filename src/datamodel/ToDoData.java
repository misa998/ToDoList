package datamodel;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;

public class ToDoData {
    private static ToDoData instance = new ToDoData();          //makes only 1 instance
    private static String filename = "TodoListItems.txt";

    private ObservableList<ToDoItem> toDoItems;                           //list to store items (helps with binding)
    private DateTimeFormatter formatter;                        //for formatting date

    public static ToDoData getInstance(){
        return instance;
    }

    private ToDoData(){                                         //private constructor prevents instantiating a new version of the class as an object
        formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    }

    public ObservableList<ToDoItem> getToDoItems() {
        return toDoItems;
    }

    public void addTodoItem(ToDoItem item){
        toDoItems.add(item);
    }

    //public void setToDoItems(List<ToDoItem> toDoItems) {
    //    this.toDoItems = toDoItems;
    //}

    public void loadTodoItems() throws IOException {
//        try (DataInputStream itemInput = new DataInputStream(new BufferedInputStream(new FileInputStream("toDoItems.dat")))){
//            boolean eof = false;
//            toDoItems = FXCollections.observableArrayList();
//            String input;
//            while(!eof){                                                        //until end of file
//                try {
//                    while ((input = itemInput.readUTF()) != null) {
//                        String[] itemPieces = input.split("\t");
//                        String shortDesc = itemPieces[0];
//                        String details = itemPieces[1];
//                        String dateString = itemPieces[2];
//                        LocalDate date = LocalDate.parse(dateString, formatter);
//                        ToDoItem toDoItem = new ToDoItem(shortDesc, details, date);
//                        toDoItems.add(toDoItem);
//                    }
//                }
//                    catch (EOFException e){                                       //end of file exception
//                    eof = true;
//                }
//            }
//        }
        try (ObjectInputStream itemInput = new ObjectInputStream(new BufferedInputStream(new FileInputStream("toDoItems2.dat")))) {
            toDoItems = FXCollections.observableArrayList();
            boolean eof = false;
            while (!eof) {
                try {
                    ToDoItem toDoItem = (ToDoItem) itemInput.readObject();
                    toDoItems.add(toDoItem);
                } catch (EOFException e) {
                    eof = true;
                }
            }
        } catch (IOException io){
                System.out.println("IO Exception " + io.getMessage());
        } catch (ClassNotFoundException e) {
            System.out.println("Class not found exception " + e.getMessage());
        }
    }


//        toDoItems = FXCollections.observableArrayList();        //because setAll() is used in controller.java and it wants observable AL... FXCollections is used because it's optimized for ui
//        Path path = Paths.get(filename);
//        BufferedReader br = Files.newBufferedReader(path);
//        String input;
//        try{
//            while((input = br.readLine()) != null){
//                String[] itemPieces = input.split("\t");
//
//                String shortDesc = itemPieces[0];
//                String details = itemPieces[1];
//                String dateString = itemPieces[2];
//
//                LocalDate date = LocalDate.parse(dateString, formatter);
//                ToDoItem toDoItem = new ToDoItem(shortDesc, details, date);
//                toDoItems.add(toDoItem);
//            }
//        } finally {
//            if(br != null){
//                br.close();
//            }
//        }
//        }

    public void storeTodoItems() throws IOException {
//        try(DataOutputStream itemsOutput = new DataOutputStream(new BufferedOutputStream(new FileOutputStream("toDoItems.dat")))){
//            Iterator<ToDoItem> iter = toDoItems.iterator();
//            while(iter.hasNext()){
//                ToDoItem item = iter.next();
//                itemsOutput.writeUTF(String.format("%s\t%s\t%s",
//                        item.getShortDesc(), item.getDetails(),
//                        item.getDeadline().format(formatter)));
//                itemsOutput.writeUTF("\n");
//            }
//        }
        try (ObjectOutputStream itemsOutput = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream("toDoItems2.dat")))) {
            Iterator<ToDoItem> iter = toDoItems.iterator();
            while (iter.hasNext()) {
                ToDoItem item = iter.next();
                itemsOutput.writeObject(item);
            }

//        Path path = Paths.get(filename);
//        BufferedWriter bw = Files.newBufferedWriter(path);          //Writes text to a character-output stream, buffering characters so as to provide for the efficient writing of single characters, arrays, and strings.
//        try{
//            Iterator<ToDoItem> iter = toDoItems.iterator();  //for cycling through
//            while(iter.hasNext()){
//                ToDoItem item = iter.next();
//                bw.write(String.format("%s\t%s\t%s",
//                        item.getShortDesc(), item.getDetails(),
//                        item.getDeadline().format(formatter)));
//                bw.newLine();
//            }
//        } finally {
//            if(bw != null){
//                bw.close();
//            }
//        }
        }
    }

    public void deleteTodoItem(ToDoItem item){
        toDoItems.remove(item);
    }
}
