package com.gayatripatel.eventhub.data;

import androidx.lifecycle.LiveData;
import com.gayatripatel.eventhub.core.Event;
import com.gayatripatel.eventhub.core.Ticket;
import com.gayatripatel.eventhub.core.TicketTier;
import java.util.List;

public interface EventRepository {

    interface Callback<T> {
        void onSuccess(T value);

        void onError(String message);
    }

    LiveData<Resource<List<Event>>> events();

    void refresh();

    LiveData<List<Ticket>> tickets();

    void book(Event event, TicketTier tier, int quantity, String holderName, Callback<Ticket> callback);
}
