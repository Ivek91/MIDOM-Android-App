package hr.fer.zari.midom.rest.request;

public class AcceptConsultationRequest {

    private String requestId;

    public AcceptConsultationRequest(String requestId) {
        this.requestId = requestId;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    @Override
    public String toString() {
        return "AcceptConsultationRequest{" +
                "requestId='" + requestId + '\'' +
                '}';
    }
}
