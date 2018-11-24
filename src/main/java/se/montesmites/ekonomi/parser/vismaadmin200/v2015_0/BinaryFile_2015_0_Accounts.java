package se.montesmites.ekonomi.parser.vismaadmin200.v2015_0;

import se.montesmites.ekonomi.model.Account;
import se.montesmites.ekonomi.model.AccountId;
import se.montesmites.ekonomi.model.AccountStatus;
import se.montesmites.ekonomi.model.YearId;
import se.montesmites.ekonomi.parser.vismaadmin200.Field;
import se.montesmites.ekonomi.parser.vismaadmin200.Record;

import java.util.List;
import java.util.Optional;

import static se.montesmites.ekonomi.parser.vismaadmin200.DataType.STRING;

public class BinaryFile_2015_0_Accounts extends BinaryFile_2015_0<Account> {

    private final Field<String> YEARID = new Field<>("yearid", STRING, 1, 1);
    private final Field<String> ID = new Field<>("id", STRING, 2, 4);
    private final Field<String> DESCR = new Field<>("descr", STRING, 8, 59);
    private final Field<String> REMOVED = new Field<>("removed", STRING, 0, 1);
    private final Field<String> CLOSED = new Field<>("closed", STRING, 158, 1);

    BinaryFile_2015_0_Accounts() {
        super("KONTO.DBF", 833, 177);
    }

    @Override
    List<Field<?>> getFields() {
        return List.of(YEARID, ID, DESCR, REMOVED, CLOSED);
    }

    @Override
    public boolean filter(Record record) {
        boolean superFilter = super.filter(record);
        if (superFilter) {
            var status = accountStatus(record);
            var open = AccountStatus.OPEN;
            var closed = AccountStatus.CLOSED;
            return status
                    .filter(accountStatus -> accountStatus == open || accountStatus == closed)
                    .isPresent();
        } else {
            return false;
        }
    }

    @Override
    public Account modelize(Record record) {
        var yearId = new YearId(YEARID.extract(record));
        return new Account(
                new AccountId(yearId, ID.extract(record)),
                DESCR.extract(record).trim(),
                accountStatus(record).get());
    }

    private Optional<AccountStatus> accountStatus(Record record) {
        var removed = REMOVED.extract(record);
        var closed = CLOSED.extract(record);
        return AccountStatus.parse(removed, closed);
    }
}
