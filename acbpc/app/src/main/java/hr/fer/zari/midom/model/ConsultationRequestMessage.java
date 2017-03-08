package hr.fer.zari.midom.model;

public class ConsultationRequestMessage {

    private int id;
    private String comment;
    private String creationTime;
    private String msSender;
    private String spSender;

    public ConsultationRequestMessage(int id, String comment, String creationTime, String msSender, String spSender) {
        this.id = id;
        this.comment = comment;
        this.creationTime = creationTime;
        this.msSender = msSender;
        this.spSender = spSender;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(String creationTime) {
        this.creationTime = creationTime;
    }

    public String getMsSender() {
        return msSender;
    }

    public void setMsSender(String msSender) {
        this.msSender = msSender;
    }

    public String getSpSender() {
        return spSender;
    }

    public void setSpSender(String spSender) {
        this.spSender = spSender;
    }
}
