package model;

public class Appointment {
    int appointment_id;
    int user_id;
    int customer_id;
    int contact_id;
    String title;
    String description;
    String location;
    String type;
    String customer_name;
    String contact_name;
    String user_name;
    String start;
    String end;
    String startUTC;
    String endUTC;

    public Appointment() {}

    public Appointment(int appointment_id, int user_id, int customer_id, int contact_id, String title, String description, String location, String type, String start, String end, String customer_name, String contact_name, String user_name, String startUTC, String endUTC) {
        this.appointment_id = appointment_id;
        this.user_id = user_id;
        this.customer_id = customer_id;
        this.contact_id = contact_id;
        this.title = title;
        this.description = description;
        this.location = location;
        this.type = type;
        this.start = start;
        this.end = end;
        this.customer_name = customer_name;
        this.contact_name = contact_name;
        this.user_name = user_name;
        this.startUTC = startUTC;
        this.endUTC = endUTC;
    }



    public int getAppointment_id() {
        return appointment_id;
    }

    public void setAppointment_id(int appointment_id) {
        this.appointment_id = appointment_id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public int getCustomer_id() {
        return customer_id;
    }

    public void setCustomer_id(int customer_id) {
        this.customer_id = customer_id;
    }

    public int getContact_id() {
        return contact_id;
    }

    public void setContact_id(int contact_id) {
        this.contact_id = contact_id;
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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public String getStartUTC() {
        return startUTC;
    }

    public void setStartUTC(String startUTC) {
        this.startUTC = startUTC;
    }

    public String getEndUTC() {
        return endUTC;
    }

    public void setEndUTC(String endUTC) {
        this.endUTC = endUTC;
    }

    public String getCustomer_name() {
        return customer_name;
    }

    public void setCustomer_name(String customer_name) {
        this.customer_name = customer_name;
    }

    public String getContact_name() {
        return contact_name;
    }

    public void setContact_name(String contact_name) {
        this.contact_name = contact_name;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }
}
