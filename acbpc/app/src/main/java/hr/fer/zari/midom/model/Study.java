package hr.fer.zari.midom.model;

import android.widget.BaseAdapter;
import hr.fer.zari.midom.rest.RestClient;
import hr.fer.zari.midom.rest.response.AccountDetailsResponse;
import hr.fer.zari.midom.rest.response.GetStudyResponse;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class Study {

    private int id;
    private String name;
    private long creationDate;
    private int ownerId;
    private boolean open;
    private AccountDetails ownerObj;

    public Study(int id, String name, long creationDate, int ownerId, boolean open) {
        this.id = id;
        this.name = name;
        this.creationDate = creationDate;
        this.ownerId = ownerId;
        this.open = open;
    }

    public void loadOwner(final BaseAdapter adapter) {
        new RestClient().getMidomService().getAccount(ownerId, new Callback<AccountDetailsResponse>() {
            @Override
            public void success(AccountDetailsResponse accountDetailsResponse, Response response) {
                setOwnerObj(accountDetailsResponse.getMessage());
                adapter.notifyDataSetChanged();
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(long creationDate) {
        this.creationDate = creationDate;
    }

    public int getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(int ownerId) {
        this.ownerId = ownerId;
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    public AccountDetails getOwnerObj() {
        return ownerObj;
    }

    public void setOwnerObj(AccountDetails ownerObj) {
        this.ownerObj = ownerObj;
    }
}
