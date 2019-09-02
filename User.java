/**
 * User
 */
public class User implements java.lang.Comparable<User>{
    public String userId;
    public String firstName;
    public String lastName;
    public int version;
    public String insuranceCompany;
    public String originalRowValue;

    public User() {
       
    }

    @Override
    public int compareTo(User user) {
        if (this.firstName.equals(user.firstName)) {
            return this.lastName.compareTo(user.lastName);
        }
        return this.firstName.compareTo(user.firstName);
    }

    public String getOriginal() {
        return originalRowValue;
    }
}