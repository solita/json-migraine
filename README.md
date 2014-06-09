JSON Migraine
=============

JSON data migration library for Java

Licensed under the MIT License

[![Build Status](https://secure.travis-ci.org/solita/json-migraine.png)](http://travis-ci.org/solita/json-migraine)


Getting Started
---------------

First add to your project a dependency to [json-migraine](http://search.maven.org/#search%7Cga%7C1%7Ca%3A%22json-migraine%22). That will also give you a transitive dependency to the [Jackson](http://jackson.codehaus.org/) JSON library.

Then annotate each of your serialized class with `@Upgradeable` and create an initial upgrader for it:

```java
import fi.solita.jsonmigraine.api.Upgradeable;

@Upgradeable(FooUpgrader.class)
public class Foo {

    public String oldField;
}
```

```java
import fi.solita.jsonmigraine.api.ObjectUpgrader;
import org.codehaus.jackson.node.ObjectNode;

public class FooUpgrader extends ObjectUpgrader {

    @Override
    public int version() {
        return 1;
    }

    @Override
    public ObjectNode upgrade(ObjectNode data, int version) {
        return data;
    }
}
```

JSON Migraine is then used like this so serialize and deserialize the ugpradeable classes:

```java
import fi.solita.jsonmigraine.JsonMigraine;
import fi.solita.jsonmigraine.api.TypeRenames;
import org.codehaus.jackson.map.ObjectMapper;

TypeRenames renames = new TypeRenames();
JsonMigraine jsonMigraine = new JsonMigraine(new ObjectMapper(), renames);

String serialized = jsonMigraine.serialize(original);
Object upgraded = jsonMigraine.deserialize(serialized);
```


Changing Classes
----------------

When a class is changed, increment the version number of its upgrader and add the data migration rules:

```java
import fi.solita.jsonmigraine.api.Upgradeable;

@Upgradeable(FooUpgrader.class)
public class Foo {

    public String newField;
}
```

```java
import fi.solita.jsonmigraine.api.*;
import org.codehaus.jackson.node.ObjectNode;

public class FooUpgrader extends ObjectUpgrader {

    @Override
    public int version() {
        return 2;
    }

    @Override
    public ObjectNode upgrade(ObjectNode data, int version) {
        if (version == 1) {
            Refactor.renameField(data, "oldField", "newField");
        }
        return data;
    }
}
```

The `upgrade` method should have one `if` statement for each old version. When upgrading old data, the method will be called potentially multiple times, once for each successive version.


Renaming Classes
----------------

If an upgradeable class is renamed, you will need to configure JSON Migraine to apply those renames when upgrading:

```java
TypeRenames renames = new TypeRenames();
renames.rename("examples.Foo", "examples.Bar");
JsonMigraine jsonMigraine = new JsonMigraine(new ObjectMapper(), renames);
```


Class Hierarchies
-----------------

If the upgradeable class extends other upgradeable classes or contains fields of an upgradeable type, then JSON Migraine will upgrade them also.



Testing Tips
------------

When there are multiple upgrades, things get easily complex, so having good unit tests for your data migration is strongly recommended. One way is to store serialized old versions of example objects and check that they upgrade to the latest version correctly. The [Refactoring Databases](http://databaserefactoring.com/) book should give some ideas for a data migration stragegy.


Release Notes
-------------

### JSON Migraine 1.0.2 (2014-06-09)

- Use the context class loader for deserializing by default

### JSON Migraine 1.0.1 (2013-04-15)

- Fixed `ClassNotFoundException` when removing classes from the class hierarchy

### JSON Migraine 1.0.0 (2012-11-02)

- Initial release
