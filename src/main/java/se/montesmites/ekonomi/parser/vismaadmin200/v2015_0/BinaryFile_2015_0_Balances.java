package se.montesmites.ekonomi.parser.vismaadmin200.v2015_0;

import java.util.Arrays;
import java.util.List;
import se.montesmites.ekonomi.model.AccountId;
import se.montesmites.ekonomi.model.Balance;
import se.montesmites.ekonomi.model.Currency;
import se.montesmites.ekonomi.model.YearId;
import static se.montesmites.ekonomi.parser.vismaadmin200.DataType.CURRENCY;
import static se.montesmites.ekonomi.parser.vismaadmin200.DataType.STRING;
import se.montesmites.ekonomi.parser.vismaadmin200.Field;
import se.montesmites.ekonomi.parser.vismaadmin200.Record;

public class BinaryFile_2015_0_Balances extends BinaryFile_2015_0<Balance> {

    public BinaryFile_2015_0_Balances() {
        super("REING.DBF", 321, 78);
    }

    private final Field<String> YEARID = new Field("yearid", STRING, 1, 1);
    private final Field<String> ACCOUNT = new Field("account", STRING, 10, 4);
    private final Field<Currency> AMOUNT = new Field("mnt", CURRENCY, 15, 14);
    private final Field<String> ASTERISK = new Field("asterisk", STRING, 0, 1) {
        @Override
        public boolean filter(Record record) {
            return super.filter(record) && !"*".equals(ASTERISK.extract(record));
        }
    };

    @Override
    List<Field<?>> getFields() {
        return Arrays.asList(ASTERISK, YEARID, ACCOUNT, AMOUNT);
    }

    @Override
    public Balance modelize(Record record) {
        YearId yearid = new YearId(YEARID.extract(record));
        AccountId accountid = new AccountId(yearid, ACCOUNT.extract(record));
        Currency amount = AMOUNT.extract(record);
        return new Balance(accountid, amount);
    }
}
