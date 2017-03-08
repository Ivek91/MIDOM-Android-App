package hr.fer.zari.midom.rest;

import hr.fer.zari.midom.utils.Constants;
import retrofit.RestAdapter;

public class RestClient {

    private MidomService midomService;

    /**
     * @TODO: dodati interceptor koji ce vratiti usera na LoginActivity ako API bilo kada vrati da user nije ulogiran
     */
    public RestClient() {

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setEndpoint(Constants.LINK)
                .build();

        midomService = restAdapter.create(MidomService.class);

    }

    public MidomService getMidomService() {
        return midomService;
    }
}
