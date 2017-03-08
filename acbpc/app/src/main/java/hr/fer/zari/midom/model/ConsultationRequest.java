package hr.fer.zari.midom.model;

import android.widget.Adapter;
import android.widget.BaseAdapter;
import hr.fer.zari.midom.rest.RestClient;
import hr.fer.zari.midom.rest.response.GetStudyResponse;
import hr.fer.zari.midom.utils.FileHelper;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import java.io.IOException;

public class ConsultationRequest {

    private int id;
    private long creationTime;
    private String acceptanceTime;
    private int studyOwner;
    private String status;
    private int study;
    private Study studyObj;
    private byte[] avatar;
    public boolean avatarDownloadInProgress;

    public ConsultationRequest(int id, long creationTime, String acceptanceTime, int studyOwner, String status, int study) {
        this.id = id;
        this.creationTime = creationTime;
        this.acceptanceTime = acceptanceTime;
        this.studyOwner = studyOwner;
        this.status = status;
        this.study = study;
    }

    public void loadStudy(final BaseAdapter adapter) {
        new RestClient().getMidomService().getStudy(study, new Callback<GetStudyResponse>() {
            @Override
            public void success(GetStudyResponse getStudyResponse, Response response) {
                setStudyObj(getStudyResponse.getMessage());
                adapter.notifyDataSetChanged();
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }

    public void loadAvatar(final BaseAdapter adapter) {
        avatarDownloadInProgress = true;

        new RestClient().getMidomService().getAvatar(studyOwner, new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
                avatarDownloadInProgress = false;

                try {
                    byte[] bytes = FileHelper.getBytesFromStream(response.getBody().in());

                    setAvatar(bytes);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void failure(RetrofitError error) {
                avatarDownloadInProgress = false;
            }
        });
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(long creationTime) {
        this.creationTime = creationTime;
    }

    public String getAcceptanceTime() {
        return acceptanceTime;
    }

    public void setAcceptanceTime(String acceptanceTime) {
        this.acceptanceTime = acceptanceTime;
    }

    public int getStudyOwner() {
        return studyOwner;
    }

    public void setStudyOwner(int studyOwner) {
        this.studyOwner = studyOwner;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getStudy() {
        return study;
    }

    public void setStudy(int study) {
        this.study = study;
    }

    public Study getStudyObj() {
        return studyObj;
    }

    public void setStudyObj(Study studyObj) {
        this.studyObj = studyObj;
    }

    public byte[] getAvatar() {
        return avatar;
    }

    public void setAvatar(byte[] avatar) {
        this.avatar = avatar;
    }

    @Override
    public String toString() {
        return "ConsultationRequest{" +
                "id=" + id +
                ", creationTime=" + creationTime +
                ", acceptanceTime='" + acceptanceTime + '\'' +
                ", studyOwner=" + studyOwner +
                ", status='" + status + '\'' +
                ", study=" + study +
                '}';
    }
}
