package com.neperix.osiris.messaging;

/**
 * Defines the API for the components receiving the message.
 *
 * @param <T> The type of messages this destination consumes.
 */
public interface Destination<T> {

    /**
     * Invoked by messaging mechanism when message is passed to this source.
     *
     * @param message The message being received.
     */
    void receive(T message);
}
