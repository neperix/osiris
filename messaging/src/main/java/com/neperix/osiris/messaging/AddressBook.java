package com.neperix.osiris.messaging;

/**
 * Used to keep track of to which destinations a specific message should be routed to.
 */
public interface AddressBook {

    /**
     * Registers the given destination to receive messages of the given type.
     *
     * @param <T> The type of the message.
     * @param messageType The type of the message.
     * @param destination The destination.
     */
    <T> void register(Class<T> messageType, Destination<T> destination);

    /**
     * Unregisters the given destination from registry.
     *
     * @param <T> The type of message this destination is subscribed to.
     * @param messageType The type of message.
     * @param destination The destination.
     */
    <T> void unregister(Class<T> messageType, Destination<T> destination);
}
