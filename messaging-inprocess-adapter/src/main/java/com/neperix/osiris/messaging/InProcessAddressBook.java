package com.neperix.osiris.messaging;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * An implementation of {@link AddressBook} that keeps records of destinations in the same process.
 */
@Slf4j
@RequiredArgsConstructor
class InProcessAddressBook implements AddressBook {

    private final Map<Class, Set<Destination>> subscriptions;

    InProcessAddressBook() {
        this.subscriptions = new HashMap<>();
    }

    @Override
    public synchronized  <T> void register(Class<T> messageType, Destination<T> destination) {

        log.info("Registering {} for messages of type {}", destination, messageType.getName());

        this.subscriptions.putIfAbsent(messageType, new HashSet<>());
        this.subscriptions.get(messageType).add(destination);
    }

    @Override
    public synchronized <T> void unregister(Class<T> messageType, Destination<T> destination) {

        log.info("Unregistering {} for messages of type {}", destination, messageType.getName());

        this.subscriptions.getOrDefault(messageType, Collections.emptySet()).remove(destination);
    }

    synchronized List<Destination> destinationsFor(Class messageType) {
        Set<Destination> destinations = this.subscriptions.getOrDefault(messageType, Collections.emptySet());
        return new ArrayList<>(destinations);
    }
}
