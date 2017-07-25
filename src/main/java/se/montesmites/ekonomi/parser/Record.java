package se.montesmites.ekonomi.parser;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

class Record {

    private final Map<FieldKey<?>, Optional<?>> inner;

    Record() {
        this(new HashMap<>());
    }

    Record(FieldKey<?> key, Optional<?> value) {
        this(new HashMap<FieldKey<?>, Optional<?>>() {
            {
                put(key, value);
            }
        });
    }

    Record(Map<FieldKey<?>, Optional<?>> map) {
        this.inner = map;
    }

    Record(Map<FieldKey<?>, Optional<?>> map1, Map<FieldKey<?>, Optional<?>> map2) {
        this();
        inner.putAll(map1);
        inner.putAll(map2);
    }

    <T> Optional<T> get(FieldKey<T> key) {
        return inner.get(key).map(value -> key.asInstanceOf(value));
    }

    <T> T extract(FieldKey<T> key) {
        return get(key).get();
    }

    Record merge(Record that) {
        return new Record(this.inner, that.inner);
    }
}
