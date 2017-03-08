package hr.fer.zari.midom.rest.request;

public class ChangeAccountDetailsRequest {

    private String firstName;
    private String lastName;
    private String email;
    private String organisation;
    private String description;
    private String location;
    private String telephone;
    private String otherContact;

    public ChangeAccountDetailsRequest() {
    }

    public ChangeAccountDetailsRequest(String firstName, String lastName, String email, String organisation, String description, String location, String telephone, String otherContact) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.organisation = organisation;
        this.description = description;
        this.location = location;
        this.telephone = telephone;
        this.otherContact = otherContact;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getOtherContact() {
        return otherContact;
    }

    public void setOtherContact(String otherContact) {
        this.otherContact = otherContact;
    }

    @Override
    public String toString() {
        return "ChangeAccountDetailsRequest{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", organisation='" + organisation + '\'' +
                ", description='" + description + '\'' +
                ", location='" + location + '\'' +
                ", telephone='" + telephone + '\'' +
                ", otherContact='" + otherContact + '\'' +
                '}';
    }
}
