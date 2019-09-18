package mk.mca.bransys.ModelData;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import mk.mca.bransys.Interfaces.Service;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitInstance {
    private static final String URL = "http://api.citybik.es/";
    private static mk.mca.bransys.Interfaces.Service service;


    public static Service getClient() {
        if (service == null) {
            Gson gson = new GsonBuilder()
                    .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                    .create();

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("http://api.citybik.es")
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();

            service = retrofit.create(mk.mca.bransys.Interfaces.Service.class);
        }
        return service;
    }
}




