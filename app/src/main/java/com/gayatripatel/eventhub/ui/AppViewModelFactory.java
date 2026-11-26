package com.gayatripatel.eventhub.ui;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import com.gayatripatel.eventhub.data.EventRepository;
import com.gayatripatel.eventhub.data.ServiceLocator;

/** Builds view models with their dependencies from {@link ServiceLocator}. */
final class AppViewModelFactory implements ViewModelProvider.Factory {
    private final EventRepository repository;
    private final String eventId;

    AppViewModelFactory(Context context, String eventId) {
        this.repository = ServiceLocator.repository(context);
        this.eventId = eventId;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass == EventListViewModel.class) {
            return modelClass.cast(new EventListViewModel(repository, ServiceLocator.clock(), ServiceLocator.zone()));
        }
        if (modelClass == EventDetailViewModel.class) {
            return modelClass.cast(new EventDetailViewModel(repository, eventId));
        }
        if (modelClass == TicketsViewModel.class) {
            return modelClass.cast(new TicketsViewModel(repository));
        }
        throw new IllegalArgumentException("Unknown view model " + modelClass);
    }
}
