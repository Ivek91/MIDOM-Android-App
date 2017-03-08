package hr.fer.zari.midom.rest.request;

public class RejectConsultationRequest {

    private String requestId;

    public RejectConsultationRequest(String requestId) {
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
        return "RejectConsultationRequest{" +
                "requestId='" + requestId + '\'' +
                '}';
    }
}
