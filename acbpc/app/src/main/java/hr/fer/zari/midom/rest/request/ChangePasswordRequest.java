package hr.fer.zari.midom.rest.request;

public class ChangePasswordRequest {

    private String password;
    private String repeatedPassword;

    public ChangePasswordRequest(String password, String repeatedPassword) {
        this.password = password;
        this.repeatedPassword = repeatedPassword;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRepeatedPassword() {
        return repeatedPassword;
    }

    public void setRepeatedPassword(String repeatedPassword) {
        this.repeatedPassword = repeatedPassword;
    }

    @Override
    public String toString() {
        return "ChangePasswordRequest{" +
                "password='" + password + '\'' +
                ", repeatedPassword='" + repeatedPassword + '\'' +
                '}';
    }
}
