## Druid Client

This is a client that can be used to make Druid queries from the JVM. Note that unlike the
[JSON query API](http://druid.io/docs/latest/querying/querying.html), this depends on non-stable Druid
APIs. You may need to adjust code even for minor Druid updates.

### Installation

To install this library, run `mvn install`. You can then include it in projects with Maven by using
the dependency:

```xml
<dependency>
  <groupId>io.imply</groupId>
  <artifactId>druid-client</artifactId>
  <version>0.1-SNAPSHOT</version>
</dependency>
```

### Example

Check out ExampleMain.java for example code.
