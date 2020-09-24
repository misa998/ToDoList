package datamodel;

import java.io.Serializable;
import java.time.LocalDate;

public class ToDoItem implements Serializable {

    private String shortDesc;
    private String details;
    private LocalDate deadline;

    private long serialVersionUID = 1L;

    public ToDoItem(String shortDesc, String details, LocalDate deadline) {
        this.shortDesc = shortDesc;
        this.details = details;
        this.deadline = deadline;
    }

    public String getShortDesc() {
        return shortDesc;
    }

    public void setShortDesc(String shortDesc) {
        this.shortDesc = shortDesc;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public LocalDate getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDate deadline) {
        this.deadline = deadline;
    }

    //@Override                                 //not needed, because the updateCell() adds this for us in the itemUpdate()
    //public String toString() {
    //    return shortDesc;
    //}
}
