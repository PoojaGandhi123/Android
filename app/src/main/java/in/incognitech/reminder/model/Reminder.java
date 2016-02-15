package in.incognitech.reminder.model;

import android.location.Location;

import java.util.Date;

/**
 * Created by udit on 14/02/16.
 */
public class Reminder {

    private String description;
    private String author;
    private String friend;
    private String type;
    private int priority;
    private Date date;
    private Date dateGMT;
    private Date reminderDate;
    private Date reminderDateGMT;
    private boolean isResponded;
    private String response;
    private Location location;
    private String locationType;
    private double locationRadius;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getFriend() {
        return friend;
    }

    public void setFriend(String friend) {
        this.friend = friend;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Date getDateGMT() {
        return dateGMT;
    }

    public void setDateGMT(Date dateGMT) {
        this.dateGMT = dateGMT;
    }

    public Date getReminderDate() {
        return reminderDate;
    }

    public void setReminderDate(Date reminderDate) {
        this.reminderDate = reminderDate;
    }

    public Date getReminderDateGMT() {
        return reminderDateGMT;
    }

    public void setReminderDateGMT(Date reminderDateGMT) {
        this.reminderDateGMT = reminderDateGMT;
    }

    public boolean isResponded() {
        return isResponded;
    }

    public void setIsResponded(boolean isResponded) {
        this.isResponded = isResponded;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getLocationType() {
        return locationType;
    }

    public void setLocationType(String locationType) {
        this.locationType = locationType;
    }

    public float getLocationRadius() {
        return locationRadius;
    }

    public void setLocationRadius(float locationRadius) {
        this.locationRadius = locationRadius;
    }
}
