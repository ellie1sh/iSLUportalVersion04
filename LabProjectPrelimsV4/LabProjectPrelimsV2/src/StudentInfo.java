/**
 * Data class to hold student information
 */
public class StudentInfo {
    private String id;
    private String lastName;
    private String firstName;
    private String middleName;
    private String dateOfBirth;
    private String password;
    
    public StudentInfo(String id, String lastName, String firstName, String middleName, String dateOfBirth, String password) {
        this.id = id;
        this.lastName = lastName;
        this.firstName = firstName;
        this.middleName = middleName;
        this.dateOfBirth = dateOfBirth;
        this.password = password;
    }
    
    // Getters
    public String getId() { return id; }
    public String getLastName() { return lastName; }
    public String getFirstName() { return firstName; }
    public String getMiddleName() { return middleName; }
    public String getDateOfBirth() { return dateOfBirth; }
    public String getPassword() { return password; }
    
    public String getFullName() {
        return firstName.toUpperCase() + " " + lastName.toUpperCase();
    }
    
    public String toDatabaseFormat() {
        return id + "," + lastName + "," + firstName + "," + middleName + "," + dateOfBirth + "," + password;
    }
}