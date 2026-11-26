package com.gayatripatel.eventhub.ui;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import com.gayatripatel.eventhub.core.Ticket;
import com.gayatripatel.eventhub.data.EventRepository;
import java.util.List;

public final class TicketsViewModel extends ViewModel {
    private final LiveData<List<Ticket>> tickets;

    TicketsViewModel(EventRepository repository) {
        this.tickets = repository.tickets();
    }

    public LiveData<List<Ticket>> tickets() {
        return tickets;
    }
}
