package nl.bertriksikken.gls;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Retrofit interface for a geolocation service API.
 */
public interface IGeoLocationRestApi {

    @POST("/v1/geolocate")
    public Call<GeoLocationResponse> geoLocate(@Query("key") String key, @Body GeoLocationRequest request);

}
