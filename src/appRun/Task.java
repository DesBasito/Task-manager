package appRun;

import state.Priority;
import state.Status;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class Task {
    private String title;
    private String description;
    private Date completionDate;
    private Date createdDate;
    private Priority priority;
    private Status status;
    private int rating;
    public Task(String title, String description, String completionDate, Priority priority) throws ParseException {
        LocalDateTime ldt = LocalDateTime.now();
        DateTimeFormatter customFormatter = DateTimeFormatter.ofPattern("M/d/yyyy");
        String formattedString = ldt.format(customFormatter);

        this.title = title;
        this.description = description;
        this.completionDate = new SimpleDateFormat("M/d/yyyy").parse(completionDate);
        this.createdDate = new SimpleDateFormat("M/d/yyyy").parse(formattedString);
        this.priority = priority;
        this.status = Status.NEW;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCompletionDate(Date completionDate) {
        this.completionDate = completionDate;
    }

    public Date getCompletionDate() {
        return completionDate;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }


    public void displayTask() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("E MMM dd yyyy");

        System.out.println("Title: " + this.title);
        System.out.println("Description: " + this.description);

        String formattedCompletionDate = dateFormat.format(this.completionDate);
        System.out.println("Completion Date: " + formattedCompletionDate);

        String formattedCreatedDate = dateFormat.format(this.createdDate);
        System.out.println("Created Date: " + formattedCreatedDate);
        System.out.println("Priority: " + priority);
        System.out.println("Status: " + status);
        System.out.println("-------------------");
    }
}
