package se.montesmites.ekonomi.parser.vismaadmin200.v2015_0;

import se.montesmites.ekonomi.model.*;
import se.montesmites.ekonomi.parser.vismaadmin200.Field;
import se.montesmites.ekonomi.parser.vismaadmin200.Record;

import java.util.List;
import java.util.Optional;

import static se.montesmites.ekonomi.model.EntryStatus.Status.ACTIVE;
import static se.montesmites.ekonomi.parser.vismaadmin200.DataType.*;

public class BinaryFile_2015_0_Entries extends BinaryFile_2015_0<Entry> {

    BinaryFile_2015_0_Entries() {
        super("VERRAD.DBF", 642, 162);
    }

    private final Field<String> YEARID = new Field<>("yearid", STRING, 0, 1);
    private final Field<String> SERIES = new Field<>("series", STRING, 150, 1);
    private final Field<String> FLAGS = new Field<>("flags", STRING, 1, 4);
    private final Field<String> ACCOUNT = new Field<>("account", STRING, 24, 4);
    private final Field<Currency> AMOUNT = new Field<>("mnt", CURRENCY, 28, 14);
    private final Field<Integer> ID =
            new Field<>("id", INTEGER, 5, 7) {
        @Override
        public boolean filter(Record record) {
            return super.filter(record) && ID.extract(record) > 0;
        }
            };

    @Override
    public boolean filter(Record record) {
        if (super.filter(record)) {
            var status = entryStatus(record);
            return status.isPresent() && status.get().getStatus() == ACTIVE;
        } else {
            return false;
        }
    }

    @Override
    List<Field<?>> getFields() {
        return List.of(YEARID, ID, SERIES, FLAGS, ACCOUNT, AMOUNT);
    }

    @Override
    public Entry modelize(Record record) {
        var yearid = new YearId(YEARID.extract(record));
        var series = new Series(SERIES.extract(record));
        var eventid = new EventId(yearid, ID.extract(record), series);
        var accountid = new AccountId(yearid, ACCOUNT.extract(record));
        var status = entryStatus(record).get();
        var amount = AMOUNT.extract(record);
        return new Entry(eventid, accountid, amount, status);
    }

    private Optional<EntryStatus> entryStatus(Record record) {
        return EntryStatus.parse(FLAGS.extract(record));
    }
}
