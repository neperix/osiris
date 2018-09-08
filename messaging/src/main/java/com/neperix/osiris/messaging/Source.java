package com.neperix.osiris.messaging;

/**
 * Provides the API for emitting messages.
 */
public interface Source {

    /**
     * Emits the given message.
     *
     * @param message The message being emitted.
     */
    void emit(Object message);
}
