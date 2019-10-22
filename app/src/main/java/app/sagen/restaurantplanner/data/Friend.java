package app.sagen.restaurantplanner.data;

public class Friend {

    public static String TABLE_FRIENDS = "friend";
    public static String COLUMN_ID = "id";
    public static String COLUMN_NAME = "name";
    public static String COLUMN_PHONE = "phone";

    private long id;
    private String name;
    private String phone;

    public Friend() {
    }

    public Friend(String firstName, String phone) {
        this.name = firstName;
        this.phone = phone;
    }

    public Friend(long _ID, String firstName, String phone) {
        this.id = _ID;
        this.name = firstName;
        this.phone = phone;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
