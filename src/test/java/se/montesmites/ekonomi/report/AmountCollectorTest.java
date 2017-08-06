package se.montesmites.ekonomi.report;

import java.time.Month;
import java.time.YearMonth;
import java.util.function.Function;
import java.util.stream.Stream;
import org.junit.Before;
import org.junit.Test;
import se.montesmites.ekonomi.model.Entry;
import se.montesmites.ekonomi.model.EventId;

public class AmountCollectorTest {

    private Function<EventId, YearMonth> dateProvider;

    @Before
    public void before() {
        this.dateProvider = eventId -> YearMonth.of(2012, Month.JANUARY);
    }

    @Test
    public void collectEmptyStream() {
        AmountAggregate act
                = Stream.<Entry>empty()
                        .collect(new AmountCollector(dateProvider));
    }
}
