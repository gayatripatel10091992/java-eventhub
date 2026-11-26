package com.gayatripatel.eventhub.data;

import android.os.SystemClock;
import com.gayatripatel.eventhub.core.Event;
import com.gayatripatel.eventhub.core.OrderCalculator;
import com.gayatripatel.eventhub.core.SampleEvents;
import com.gayatripatel.eventhub.core.TicketTier;
import com.google.gson.Gson;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.List;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;

/**
 * Serves the demo API from inside OkHttp, so the full Retrofit + Gson stack runs without a server.
 * Swap it out of {@link ApiClient} to point the app at a real backend.
 */
public final class MockApiInterceptor implements Interceptor {
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private static final String REF_CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";

    private final Gson gson;
    private final Clock clock;
    private final SecureRandom random = new SecureRandom();

    public MockApiInterceptor(Gson gson, Clock clock) {
        this.gson = gson;
        this.clock = clock;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        String path = request.url().encodedPath();
        SystemClock.sleep(350); // simulated network latency, on OkHttp's background thread

        if ("GET".equals(request.method()) && path.endsWith("/v1/events")) {
            return respond(request, 200, gson.toJson(SampleEvents.events(clock.now())));
        }
        if ("POST".equals(request.method()) && path.endsWith("/v1/bookings")) {
            ApiModels.BookingRequest booking = gson.fromJson(readBody(request), ApiModels.BookingRequest.class);
            TicketTier tier = findTier(SampleEvents.events(clock.now()), booking.eventId, booking.tierId);
            String problem = OrderCalculator.validate(tier, booking.quantity);
            if (problem != null) {
                return respond(request, 409, gson.toJson(new ApiModels.ErrorResponse(problem)));
            }
            long total = OrderCalculator.price(tier, booking.quantity).totalCents;
            return respond(request, 201, gson.toJson(new ApiModels.BookingResponse(newRef(), total)));
        }
        return respond(request, 404, gson.toJson(new ApiModels.ErrorResponse("Not found")));
    }

    private static TicketTier findTier(List<Event> events, String eventId, String tierId) {
        for (Event e : events) if (e.id.equals(eventId)) return e.tier(tierId);
        return null;
    }

    private String newRef() {
        StringBuilder sb = new StringBuilder("EVH-");
        for (int i = 0; i < 5; i++) sb.append(REF_CHARS.charAt(random.nextInt(REF_CHARS.length())));
        return sb.toString();
    }

    private static String readBody(Request request) throws IOException {
        if (request.body() == null) return "";
        Buffer buffer = new Buffer();
        request.body().writeTo(buffer);
        return buffer.readUtf8();
    }

    private static Response respond(Request request, int code, String json) {
        return new Response.Builder()
                .request(request)
                .protocol(Protocol.HTTP_1_1)
                .code(code)
                .message(code < 300 ? "OK" : "Error")
                .body(ResponseBody.create(json, JSON))
                .build();
    }
}
