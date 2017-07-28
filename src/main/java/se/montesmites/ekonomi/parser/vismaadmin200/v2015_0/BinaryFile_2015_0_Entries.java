package se.montesmites.ekonomi.parser.vismaadmin200.v2015_0;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import se.montesmites.ekonomi.model.AccountId;
import se.montesmites.ekonomi.model.Currency;
import se.montesmites.ekonomi.model.Entry;
import se.montesmites.ekonomi.model.EntryStatus;
import static se.montesmites.ekonomi.model.EntryStatus.Status.ACTIVE;
import se.montesmites.ekonomi.model.EventId;
import se.montesmites.ekonomi.model.Series;
import se.montesmites.ekonomi.model.YearId;
import static se.montesmites.ekonomi.parser.vismaadmin200.DataType.CURRENCY;
import static se.montesmites.ekonomi.parser.vismaadmin200.DataType.INTEGER;
import static se.montesmites.ekonomi.parser.vismaadmin200.DataType.STRING;
import se.montesmites.ekonomi.parser.vismaadmin200.Field;
import se.montesmites.ekonomi.parser.vismaadmin200.Record;

public class BinaryFile_2015_0_Entries extends BinaryFile_2015_0<Entry> {

    public BinaryFile_2015_0_Entries() {
        super("VERRAD.DBF", 642, 162);
    }
    private final Field<String> YEARID = new Field("yearid", STRING, 0, 1);
    private final Field<String> SERIES = new Field("series", STRING, 150, 1);
    private final Field<String> FLAGS = new Field("flags", STRING, 1, 4);
    private final Field<String> ACCOUNT = new Field("account", STRING, 24, 4);
    private final Field<Currency> AMOUNT = new Field("mnt", CURRENCY, 28, 14);
    private final Field<Integer> ID = new Field("id", INTEGER, 5, 7) {
        @Override
        public boolean filter(Record record) {
            return super.filter(record) && ID.extract(record) > 0;
        }
    };

    @Override
    public boolean filter(Record record) {
        boolean superFilter = super.filter(record);
        if (superFilter) {
            Optional<EntryStatus> s = entryStatus(record);
            return s.isPresent() && s.get().getStatus() == ACTIVE;
        } else {
            return false;
        }
    }

    @Override
    List<Field<?>> getFields() {
        return Arrays.asList(YEARID, ID, SERIES, FLAGS, ACCOUNT, AMOUNT);
    }

    @Override
    public Entry modelize(Record record) {
        YearId yearid = new YearId(YEARID.extract(record));
        Series series = new Series(SERIES.extract(record));
        EventId eventid = new EventId(yearid, ID.extract(record), series);
        AccountId accountid = new AccountId(yearid, ACCOUNT.extract(record));
        EntryStatus status = entryStatus(record).get();
        Currency amount = AMOUNT.extract(record);
        return new Entry(eventid, accountid, amount, status);
    }

    private Optional<EntryStatus> entryStatus(Record record) {
        return EntryStatus.parse(FLAGS.extract(record));
    }
}
