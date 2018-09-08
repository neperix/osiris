package com.neperix.osiris.messaging;

import java.util.List;
import java.util.concurrent.ExecutorService;

import lombok.RequiredArgsConstructor;

/**
 * An implementation of the {@link Source} that emits messages that are supposed to be consumed by
 * components running within the same process.
 */
@RequiredArgsConstructor
class InProcessSource implements Source {

    private final InProcessAddressBook addressBook;
    private final ExecutorService threadPool;

    @Override
    public void emit(Object message) {

        Class<?> messageType = message.getClass();
        List<Destination> destinations = this.addressBook.destinationsFor(messageType);
        for (Destination destination : destinations) {
            threadPool.execute(() -> destination.receive(message));
        }
    }
}
