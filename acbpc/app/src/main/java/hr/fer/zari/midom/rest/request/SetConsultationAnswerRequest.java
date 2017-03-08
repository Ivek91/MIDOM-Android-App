package hr.fer.zari.midom.rest.request;

public class SetConsultationAnswerRequest {

    private String comment;

    private String crId;

    public SetConsultationAnswerRequest(String comment, String crId) {
        this.comment = comment;
        this.crId = crId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getCrId() {
        return crId;
    }

    public void setCrId(String crId) {
        this.crId = crId;
    }
}
