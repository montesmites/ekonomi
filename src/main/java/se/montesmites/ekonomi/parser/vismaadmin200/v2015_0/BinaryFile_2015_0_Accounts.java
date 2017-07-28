package se.montesmites.ekonomi.parser.vismaadmin200.v2015_0;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import se.montesmites.ekonomi.model.Account;
import se.montesmites.ekonomi.model.AccountId;
import se.montesmites.ekonomi.model.AccountStatus;
import se.montesmites.ekonomi.model.YearId;
import static se.montesmites.ekonomi.parser.vismaadmin200.DataType.STRING;
import se.montesmites.ekonomi.parser.vismaadmin200.Field;
import se.montesmites.ekonomi.parser.vismaadmin200.Record;

public class BinaryFile_2015_0_Accounts extends BinaryFile_2015_0<Account> {

    private final Field<String> YEARID = new Field("yearid", STRING, 1, 1);
    private final Field<String> ID = new Field("id", STRING, 2, 4);
    private final Field<String> DESCR = new Field("descr", STRING, 8, 59);
    private final Field<String> REMOVED = new Field("removed", STRING, 0, 1);
    private final Field<String> CLOSED = new Field("closed", STRING, 158, 1);

    public BinaryFile_2015_0_Accounts() {
        super("KONTO.DBF", 833, 177);
    }

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
}
