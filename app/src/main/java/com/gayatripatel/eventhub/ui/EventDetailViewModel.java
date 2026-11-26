package com.gayatripatel.eventhub.ui;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.gayatripatel.eventhub.core.Event;
import com.gayatripatel.eventhub.core.OrderCalculator;
import com.gayatripatel.eventhub.core.Ticket;
import com.gayatripatel.eventhub.core.TicketTier;
import com.gayatripatel.eventhub.data.EventRepository;
import com.gayatripatel.eventhub.data.Resource;
import java.util.List;

public final class EventDetailViewModel extends ViewModel {

    /** Everything the order section needs, recomputed whenever the tier or quantity changes. */
    public static final class OrderState {
        public final Event event;
        public final TicketTier tier;
        public final int quantity;
        public final String error;
        public final OrderCalculator.Summary summary;
        public final boolean booking;

        OrderState(Event event, TicketTier tier, int quantity, String error, OrderCalculator.Summary summary, boolean booking) {
            this.event = event;
            this.tier = tier;
            this.quantity = quantity;
            this.error = error;
            this.summary = summary;
            this.booking = booking;
        }

        public boolean canBook() {
            return error == null && summary != null && !booking;
        }
    }

    private final EventRepository repository;
    private final String eventId;
    private final LiveData<Resource<List<Event>>> events;
    private final MutableLiveData<String> tierId = new MutableLiveData<>(null);
    private final MutableLiveData<Integer> quantity = new MutableLiveData<>(1);
    private final MutableLiveData<Boolean> booking = new MutableLiveData<>(false);
    private final MediatorLiveData<OrderState> state = new MediatorLiveData<>();
    private final MutableLiveData<String> messages = new MutableLiveData<>();
    private final MutableLiveData<Ticket> booked = new MutableLiveData<>();

    EventDetailViewModel(EventRepository repository, String eventId) {
        this.repository = repository;
        this.eventId = eventId;
        this.events = repository.events();
        state.addSource(events, x -> recompute());
        state.addSource(tierId, x -> recompute());
        state.addSource(quantity, x -> recompute());
        state.addSource(booking, x -> recompute());
    }

    private Event event() {
        Resource<List<Event>> r = events.getValue();
        if (r == null || r.data == null) return null;
        for (Event e : r.data) if (e.id.equals(eventId)) return e;
        return null;
    }

    private void recompute() {
        Event event = event();
        if (event == null) return;
        String id = tierId.getValue();
        if (id == null && event.cheapestAvailableTier() != null) {
            tierId.setValue(event.cheapestAvailableTier().id); // triggers another recompute
            return;
        }
        TicketTier tier = id == null ? null : event.tier(id);
        int qty = quantity.getValue() == null ? 1 : quantity.getValue();
        String error = OrderCalculator.validate(tier, qty);
        OrderCalculator.Summary summary = tier != null && !tier.isSoldOut() ? OrderCalculator.price(tier, Math.max(1, qty)) : null;
        state.setValue(new OrderState(event, tier, qty, error, summary, Boolean.TRUE.equals(booking.getValue())));
    }

    public LiveData<OrderState> state() {
        return state;
    }

    public LiveData<String> messages() {
        return messages;
    }

    public LiveData<Ticket> booked() {
        return booked;
    }

    public void selectTier(String id) {
        tierId.setValue(id);
    }

    public void changeQuantity(int delta) {
        int current = quantity.getValue() == null ? 1 : quantity.getValue();
        quantity.setValue(Math.max(1, Math.min(10, current + delta)));
    }

    public void book(String holderName) {
        OrderState s = state.getValue();
        if (s == null || !s.canBook()) return;
        booking.setValue(true);
        repository.book(s.event, s.tier, s.quantity, holderName, new EventRepository.Callback<Ticket>() {
            @Override
            public void onSuccess(Ticket ticket) {
                booking.setValue(false);
                booked.setValue(ticket);
            }

            @Override
            public void onError(String message) {
                booking.setValue(false);
                messages.setValue(message);
            }
        });
    }
}
