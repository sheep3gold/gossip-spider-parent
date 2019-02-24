import java.util.ResourceBundle;

public class PropertiesDemo {
    public static void main(String[] args) {
        ResourceBundle db = ResourceBundle.getBundle("db");
        String s = db.getString("driverclass");
        String url = db.getString("url");
        String username = db.getString("username");
        String password = db.getString("password");
        System.out.println(s);
        System.out.println(url);
        System.out.println(username);
        System.out.println(password);
    }
}
