package hr.fer.zari.midom.model;

import java.util.List;

public class AccountDetails {

    private int id;
    private String username;
    private String firstName;
    private String lastName;
    private String organisation;
    private String description;
    private String location;
    private String telephon;
    private String email;
    private String otherContact;
    private boolean isAvailable;
    private List<Specialisation> specialisations;

    public AccountDetails(int id, String username, String firstName, String lastName, String organisation, String description, String location, String telephon, String email, String otherContact, boolean isAvailable, List<Specialisation> specialisations) {
        this.id = id;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.organisation = organisation;
        this.description = description;
        this.location = location;
        this.telephon = telephon;
        this.email = email;
        this.otherContact = otherContact;
        this.isAvailable = isAvailable;
        this.specialisations = specialisations;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getOrganisation() {
        return organisation;
    }

    public void setOrganisation(String organisation) {
        this.organisation = organisation;
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

    public String getTelephon() {
        return telephon;
    }

    public void setTelephon(String telephon) {
        this.telephon = telephon;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getOtherContact() {
        return otherContact;
    }

    public void setOtherContact(String otherContact) {
        this.otherContact = otherContact;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setIsAvailable(boolean isAvailable) {
        this.isAvailable = isAvailable;
    }

    public List<Specialisation> getSpecialisations() {
        return specialisations;
    }

    public void setSpecialisations(List<Specialisation> specialisations) {
        this.specialisations = specialisations;
    }

    @Override
    public String toString() {
        return "AccountDetails{" +
                "specialisations=" + specialisations +
                ", isAvailable=" + isAvailable +
                ", otherContact='" + otherContact + '\'' +
                ", email='" + email + '\'' +
                ", telephon='" + telephon + '\'' +
                ", location='" + location + '\'' +
                ", organisation='" + organisation + '\'' +
                ", description='" + description + '\'' +
                ", lastName='" + lastName + '\'' +
                ", firstName='" + firstName + '\'' +
                ", username='" + username + '\'' +
                ", id=" + id +
                '}';
    }

    public String getName() {
        return this.firstName + " " + this.lastName;
    }
}
