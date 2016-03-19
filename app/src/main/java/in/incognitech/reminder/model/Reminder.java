package in.incognitech.reminder.model;

import android.graphics.Bitmap;
import android.location.Location;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import in.incognitech.reminder.util.DateUtils;

/**
 * Created by udit on 14/02/16.
 */
public class Reminder  {

    private String key;
    private String description;
    private String author;
    private String friend;
    private String type;
    private int priority;
    private String date;
    private String dateGMT;
    private String reminderDate;
    private String reminderDateGMT;
    private boolean isResponded;
    private String response;
    private Location location;
    private String locationType;
    private double locationRadius;
    private Bitmap thumb;

    public Bitmap getThumb() {
        return thumb;
    }

    public void setThumb(Bitmap thumb) {
        this.thumb = thumb;
    }





    public Reminder() {

        this.setPriority(10);
        this.setType("time");
        this.setIsResponded(false);

        Date date = new Date();
        String curDate = DateUtils.toString(date);
        String gmtDate = DateUtils.toGMT(date);

        this.setDate(curDate);
        this.setDateGMT(gmtDate);
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDateGMT() {
        return dateGMT;
    }

    public void setDateGMT(String dateGMT) {
        this.dateGMT = dateGMT;
    }

    public String getReminderDate() {
        return reminderDate;
    }

    public void setReminderDate(String reminderDate) {
        this.reminderDate = reminderDate;
    }

    public String getReminderDateGMT() {
        return reminderDateGMT;
    }

    public void setReminderDateGMT(String reminderDateGMT) {
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

    public double getLocationRadius() {
        return locationRadius;
    }

    public void setLocationRadius(double locationRadius) {
        this.locationRadius = locationRadius;
    }

    public Map<String, Object> convertToMap() {
        Map<String, Object> map = new HashMap<String, Object>();
        try {
            Class reminderClass = Class.forName("in.incognitech.reminder.model.Reminder");
            Method[] methods = reminderClass.getDeclaredMethods();
            for (Method m : methods) {
                if ( m.getName().startsWith("get") || m.getName().startsWith("is") ) {
                    Object value = (Object) m.invoke(this);
                    int offset = 0;
                    if(m.getName().startsWith("get")) {
                        offset = 3;
                    }
                    String key = m.getName().substring(offset);
                    key = Character.toLowerCase(key.charAt(0)) + key.substring(1);
                    map.put(key, (Object) value);
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return map;
    }
}
