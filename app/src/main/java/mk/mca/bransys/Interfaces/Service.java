package mk.mca.bransys.Interfaces;

import mk.mca.bransys.ModelData.Feed;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;

public interface Service {

    @Headers({"Accept: application/json"})
    @GET("/v2/networks")
    Call<Feed> getAllCompanies();

}
