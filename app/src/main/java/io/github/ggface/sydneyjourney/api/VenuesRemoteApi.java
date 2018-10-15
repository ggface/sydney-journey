package io.github.ggface.sydneyjourney.api;

import io.github.ggface.sydneyjourney.api.pojo.VenuesDto;
import io.reactivex.Single;
import retrofit2.http.GET;

public interface VenuesRemoteApi {

    @GET("test-locations")
    Single<VenuesDto> getVenues();
}