package se.montesmites.ekonomi.parser.vismaadmin200;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class Record {

    private final Map<FieldKey<?>, Optional<?>> inner;

    Record() {
        this(new HashMap<>());
    }

    Record(FieldKey<?> key, Optional<?> value) {
        this(
                new HashMap<>() {
                    {
                        put(key, value);
                    }
        });
    }

    private Record(Map<FieldKey<?>, Optional<?>> map) {
        this.inner = map;
    }

    private Record(Map<FieldKey<?>, Optional<?>> map1, Map<FieldKey<?>, Optional<?>> map2) {
        this();
        inner.putAll(map1);
        inner.putAll(map2);
    }

    <T> Optional<T> get(FieldKey<T> key) {
        return inner.get(key).map(key::asInstanceOf);
    }

    <T> T extract(FieldKey<T> key) {
        return get(key).get();
    }

    Record merge(Record that) {
        return new Record(this.inner, that.inner);
    }
}
