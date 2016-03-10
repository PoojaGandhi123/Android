package in.incognitech.reminder.model;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by udit on 01/03/16.
 */
public class User {

    private String id;
    private String name;
    private String email;
    private String photoUrl;
    private String number;
    private boolean isActive;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }

    public Map<String, Object> convertToMap() {
        Map<String, Object> map = new HashMap<String, Object>();
        try {
            Class reminderClass = Class.forName("in.incognitech.reminder.model.User");
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
