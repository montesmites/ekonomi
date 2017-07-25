package se.montesmites.ekonomi.parser.vismaadmin200.v2015_0;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import se.montesmites.ekonomi.model.Account;
import se.montesmites.ekonomi.model.AccountId;
import se.montesmites.ekonomi.model.AccountStatus;
import se.montesmites.ekonomi.model.Year;
import se.montesmites.ekonomi.model.YearId;
import se.montesmites.ekonomi.parser.vismaadmin200.BinaryFile_VismaAdmin200;
import static se.montesmites.ekonomi.parser.vismaadmin200.DataType.*;
import se.montesmites.ekonomi.parser.vismaadmin200.Field;
import se.montesmites.ekonomi.parser.vismaadmin200.Record;
import se.montesmites.ekonomi.parser.vismaadmin200.RecordDefinition;

abstract class BinaryFile_2015_0<T> implements BinaryFile_VismaAdmin200<T> {

    public final static BinaryFile_2015_0<Year> YEARS
            = new BinaryFile_2015_0<Year>("BOKFAAR.DBF", 513, 89) {
        private final Field<String> YEARID = new Field("yearid", STRING, 1, 1);
        private final Field<String> YEAR = new Field("year", STRING, 2, 4);
        private final Field<LocalDate> FROM = new Field("from", DATE, 2, 8);
        private final Field<LocalDate> TO = new Field("to", DATE, 10, 8);
        private final Field<String> STATUS = new Field("status", STRING, 0, 1) {
            @Override
            public boolean filter(Record record) {
                return super.filter(record) && !this.extract(record).equals("*");
            }
        };

        @Override
        public List<Field<?>> getFields() {
            return Arrays.asList(STATUS, YEARID, YEAR, FROM, TO);
        }

        @Override
        public Year modelize(Record record) {
            return new Year(
                    new YearId(YEARID.extract(record)),
                    YEAR.extract(record),
                    FROM.extract(record),
                    TO.extract(record));
        }
    };

    public final static BinaryFile_2015_0<Account> ACCOUNTS
            = new BinaryFile_2015_0<Account>("KONTO.DBF", 833, 177) {
        private final Field<String> YEARID = new Field("yearid", STRING, 1, 1);
        private final Field<String> ID = new Field("id", STRING, 2, 4);
        private final Field<String> DESCR = new Field("descr", STRING, 8, 59);
        private final Field<String> REMOVED = new Field("removed", STRING, 0, 1);
        private final Field<String> CLOSED = new Field("closed", STRING, 158, 1);

        @Override
        List<Field<?>> getFields() {
            return Arrays.asList(YEARID, ID, DESCR, REMOVED, CLOSED);
        }

        @Override
        public boolean filter(Record record) {
            boolean superFilter = super.filter(record);
            if (superFilter) {
                Optional<AccountStatus> s = accountStatus(record);
                AccountStatus open = AccountStatus.OPEN;
                AccountStatus closed = AccountStatus.CLOSED;
                if (s.isPresent()) {
                    return s.get() == open || s.get() == closed;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        }

        @Override
        public Account modelize(Record record) {
            YearId yearId = new YearId(YEARID.extract(record));
            return new Account(
                    new AccountId(yearId, ID.extract(record)),
                    DESCR.extract(record).trim(),
                    accountStatus(record).get()
            );
        }

        private Optional<AccountStatus> accountStatus(Record record) {
            final String removed = REMOVED.extract(record);
            final String closed = CLOSED.extract(record);
            return AccountStatus.parse(removed, closed);
        }
    };

    private final String fileName;
    private final int start;
    private final int length;

    private BinaryFile_2015_0(String fileName, int start, int length) {
        this.fileName = fileName;
        this.start = start;
        this.length = length;
    }

    @Override
    public String getFileName() {
        return fileName;
    }

    @Override
    public RecordDefinition getRecordDefinition() {
        return new RecordDefinition(start, length, getFields());
    }

    abstract List<Field<?>> getFields();
}
