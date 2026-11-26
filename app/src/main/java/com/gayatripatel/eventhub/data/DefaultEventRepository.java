package com.gayatripatel.eventhub.data;

import android.os.Handler;
import android.os.Looper;
import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import com.gayatripatel.eventhub.core.Event;
import com.gayatripatel.eventhub.core.Ticket;
import com.gayatripatel.eventhub.core.TicketTier;
import com.google.gson.Gson;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Events come from the API (kept in memory for the session); booked tickets are stored in Room so they're
 * available offline at the venue door. Retrofit calls run on OkHttp's threads and Room writes on a single
 * background executor; results are delivered on the main thread.
 */
public final class DefaultEventRepository implements EventRepository {
    private final EventApi api;
    private final TicketDao tickets;
    private final Executor io = Executors.newSingleThreadExecutor();
    private final Handler main = new Handler(Looper.getMainLooper());
    private final MutableLiveData<Resource<List<Event>>> events = new MutableLiveData<>();
    private final Gson gson = new Gson();

    public DefaultEventRepository(EventApi api, TicketDao tickets) {
        this.api = api;
        this.tickets = tickets;
    }

    @Override
    public LiveData<Resource<List<Event>>> events() {
        if (events.getValue() == null) refresh();
        return events;
    }

    @Override
    public void refresh() {
        Resource<List<Event>> current = events.getValue();
        events.setValue(Resource.loading(current == null ? null : current.data));
        api.events().enqueue(new retrofit2.Callback<List<Event>>() {
            @Override
            public void onResponse(@NonNull Call<List<Event>> call, @NonNull Response<List<Event>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    events.setValue(Resource.success(new ArrayList<>(response.body())));
                } else {
                    events.setValue(Resource.error("Couldn't load events (" + response.code() + ")", previous()));
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Event>> call, @NonNull Throwable t) {
                events.setValue(Resource.error("You're offline. Pull to retry.", previous()));
            }
        });
    }

    private List<Event> previous() {
        Resource<List<Event>> r = events.getValue();
        return r == null ? null : r.data;
    }

    @Override
    public LiveData<List<Ticket>> tickets() {
        MediatorLiveData<List<Ticket>> mapped = new MediatorLiveData<>();
        mapped.addSource(tickets.observeAll(), entities -> {
            List<Ticket> out = new ArrayList<>();
            for (TicketEntity e : entities) out.add(e.toTicket());
            mapped.setValue(out);
        });
        return mapped;
    }

    @Override
    public void book(Event event, TicketTier tier, int quantity, String holderName, Callback<Ticket> callback) {
        api.book(new ApiModels.BookingRequest(event.id, tier.id, quantity, holderName))
                .enqueue(new retrofit2.Callback<ApiModels.BookingResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<ApiModels.BookingResponse> call,
                                           @NonNull Response<ApiModels.BookingResponse> response) {
                        ApiModels.BookingResponse body = response.body();
                        if (!response.isSuccessful() || body == null) {
                            callback.onError(errorMessage(response));
                            return;
                        }
                        Ticket ticket = new Ticket(body.bookingRef, event.id, event.title, event.startsAtMillis,
                                event.venue, tier.name, quantity, body.totalCents, holderName);
                        io.execute(() -> {
                            tickets.insert(TicketEntity.from(ticket));
                            main.post(() -> callback.onSuccess(ticket));
                        });
                    }

                    @Override
                    public void onFailure(@NonNull Call<ApiModels.BookingResponse> call, @NonNull Throwable t) {
                        callback.onError("Booking failed. Check your connection and try again.");
                    }
                });
    }

    private String errorMessage(Response<?> response) {
        try (okhttp3.ResponseBody error = response.errorBody()) {
            if (error != null) {
                ApiModels.ErrorResponse parsed = gson.fromJson(error.string(), ApiModels.ErrorResponse.class);
                if (parsed != null && parsed.error != null) return parsed.error;
            }
        } catch (IOException | RuntimeException ignored) {
            // fall through to the generic message
        }
        return "Booking failed (" + response.code() + ")";
    }
}
