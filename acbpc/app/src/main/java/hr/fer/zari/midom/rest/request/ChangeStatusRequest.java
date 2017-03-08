package hr.fer.zari.midom.rest.request;

public class ChangeStatusRequest {

    private String status;

    public ChangeStatusRequest(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "ChangeStatusRequest{" +
                "status='" + status + '\'' +
                '}';
    }
}
