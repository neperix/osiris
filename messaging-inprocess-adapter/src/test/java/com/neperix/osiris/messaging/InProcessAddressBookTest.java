package com.neperix.osiris.messaging;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import org.junit.Test;

import lombok.EqualsAndHashCode;

/**
 * @author petarmitrovic
 */
public class InProcessAddressBookTest {

    @Test
    public void itShouldKeepTrackOfAllSourcesWhenRegisteredConcurrently() throws Exception {

        Map<Class, Set<Destination>> backingMap = new HashMap<>();
        InProcessAddressBook addressBook = new InProcessAddressBook(backingMap);
        
        new ConcurrentRun<Void>(5) {
            @Override
            Void perform(int counter) {

                addressBook.register(Type1.class, new Destination1());

                return null;
            }
        }.run();

        assertThat(addressBook.destinationsFor(Type1.class), hasSize(5));
    }

    @Test
    public void itShouldNotRegisterTheSameDestinationMoreThanOnce() throws ExecutionException, InterruptedException {
        final Destination2 destination2 = new Destination2();

        Map<Class, Set<Destination>> backingMap = new HashMap<>();
        InProcessAddressBook addressBook = new InProcessAddressBook(backingMap);

        new ConcurrentRun<Void>(5) {
            @Override
            Void perform(int counter) {

                addressBook.register(Type2.class, destination2);

                return null;
            }
        }.run();

        List<Destination> destinations = addressBook.destinationsFor(Type2.class);
        assertThat(destinations, hasSize(1));
        assertThat(destinations.get(0), equalTo(destination2));
    }

    @Test
    public void itShouldReturnAnEmptyCollectionWhenNoDestinationsAreRegisteredForTheGivenMessageType() {
        Map<Class, Set<Destination>> backingMap = new HashMap<>();
        InProcessAddressBook addressBook = new InProcessAddressBook(backingMap);

        Destination1 destination1 = new Destination1();
        addressBook.register(Type1.class, destination1);

        List<Destination> destinations = addressBook.destinationsFor(Type2.class);
        assertThat(destinations, hasSize(0));
    }

    @Test
    public void itShouldRemoveDestinationFromAddressBookOnceItsUnregistered() {
        Map<Class, Set<Destination>> backingMap = new HashMap<>();
        InProcessAddressBook addressBook = new InProcessAddressBook(backingMap);

        Destination1 destination1 = new Destination1();
        addressBook.register(Type1.class, destination1);

        List<Destination> destinations = addressBook.destinationsFor(Type1.class);
        assertThat(destinations, hasSize(1));

        addressBook.unregister(Type1.class, destination1);
        destinations = addressBook.destinationsFor(Type1.class);
        assertThat(destinations, hasSize(0));
    }

    static final class Type1 {}
    static final class Type2 {}

    @EqualsAndHashCode(of = "uuid")
    static final class Destination1 implements Destination<Type1> {

        private final String uuid = UUID.randomUUID().toString();

        @Override
        public void receive(Type1 message) {

        }
    }

    @EqualsAndHashCode(of = "uuid")
    static final class Destination2 implements Destination<Type2> {

        private final String uuid = UUID.randomUUID().toString();

        @Override
        public void receive(Type2 message) {

        }
    }
}
