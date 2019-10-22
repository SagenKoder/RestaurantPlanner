package app.sagen.restaurantplanner.data;

public class Restaurant {

    public static final String TABLE_RESTAURANTS = "restaurant";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_ADDRESS = "address";
    public static final String COLUMN_PHONE = "phone";
    public static final String COLUMN_TYPE = "type";

    private long id;
    private String name;
    private String address;
    private String phone;
    private String type;

    public Restaurant() {
    }

    public Restaurant(String name, String address, String phone, String type) {
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.type = type;
    }

    public Restaurant(long id, String name, String address, String phone, String type) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.type = type;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
