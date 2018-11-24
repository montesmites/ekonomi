package se.montesmites.ekonomi.parser.vismaadmin200.v2015_0;

import java.util.List;
import se.montesmites.ekonomi.parser.vismaadmin200.BinaryFile_VismaAdmin200;
import se.montesmites.ekonomi.parser.vismaadmin200.Field;
import se.montesmites.ekonomi.parser.vismaadmin200.RecordDefinition;

public abstract class BinaryFile_2015_0<T> implements BinaryFile_VismaAdmin200<T> {

  public static final BinaryFile_2015_0_Years YEARS = new BinaryFile_2015_0_Years();
  public static final BinaryFile_2015_0_Accounts ACCOUNTS = new BinaryFile_2015_0_Accounts();
  public static final BinaryFile_2015_0_Events EVENTS = new BinaryFile_2015_0_Events();
  public static final BinaryFile_2015_0_Entries ENTRIES = new BinaryFile_2015_0_Entries();
  public static final BinaryFile_2015_0_Balances BALANCES = new BinaryFile_2015_0_Balances();

  public static List<BinaryFile_2015_0<?>> values() {
    return List.of(ACCOUNTS, BALANCES, ENTRIES, EVENTS, YEARS);
  }

  private final String fileName;
  private final int start;
  private final int length;

  BinaryFile_2015_0(String fileName, int start, int length) {
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
