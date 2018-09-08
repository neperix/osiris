# osiris
A set of components used in everyday Java enterprise development

## Components
The osiris project currently consists of following components:
- messaging - basic messaging API
- messaging-inprocess-adapter - an implementation of messaging API used to exchange messages between components in the same JVM
- messaging-annotations - convenient spring-boot-like annotation for loading messaging components

## Usage
Reference the appropriate component in your project

Maven:
``` pom.xml
<dependency>
    <groupId>com.neperix.osiris</groupId>
    <artifactId>messaging</artifactId>
    <version>${neperix.osiris.version}</version>
</dependency>
<dependency>
    <groupId>com.neperix.osiris</groupId>
    <artifactId>messaging-annotations</artifactId>
    <version>${neperix.osiris.version}</version>
</dependency>
<dependency>
    <groupId>com.neperix.osiris</groupId>
    <artifactId>messaging-inprocess-adapter</artifactId>
    <version>${neperix.osiris.version}</version>
</dependency>
```

Gradle:
``` build.gradle
dependencies {
    compile "com.neperix.osiris:messaging:${neperix.osiris.version}"
    compile "com.neperix.osiris:messaging-annotations:${neperix.osiris.version}"
    compile "com.neperix.osiris:messaging-inprocess-adapter:${neperix.osiris.version}"
}
```

## Loading component

Add the `@EnableMessaging` annotation to one of your `@Configuration` classes. By default, the in-process messaging is loaded.

``` java
@EnableMessaging
@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
```

Now, you're ready to inject messaging components into yours.
``` java
class MyComponentThatSendsOutMessage {

    @Inject private final Source source;
    ...

    void methodThatSendsTheMessage(MyMessage message) {
        this.source.emit(message);
    }
}

class MyComponentThatReceivesTheMessage implements Destination<MyMessage> {

    @Override
    void receive(MyMessage message) {
        // do something useful
    }
}
```

In order for those messages successfully reach the destination you need to register appropriate entries in the address book:
``` java
@Bean
AddressBook addressBook(MyComponentThatReceivesTheMessage myComponentThatReceivesTheMessage) {
    return new InProcessAddressBook(Collections.singletonMap(MyMessage.class, myComponentThatReceivesTheMessage);
}
```

One message can have multiple destinations, and you can define them using the AddressBook API:
``` java
addressBook.register(MyMessage.class, anotherDestination);
```

You can control the number of threads the in-process component is using for handling messages using the config:
``` yaml
com:
  neperix:
    osiris:
      messaging:
        source:
          threads: # default is 5
```
