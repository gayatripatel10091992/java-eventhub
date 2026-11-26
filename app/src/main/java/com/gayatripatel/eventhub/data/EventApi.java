package com.gayatripatel.eventhub.data;

import com.gayatripatel.eventhub.core.Event;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface EventApi {
    @GET("v1/events")
    Call<List<Event>> events();

    @POST("v1/bookings")
    Call<ApiModels.BookingResponse> book(@Body ApiModels.BookingRequest request);
}
