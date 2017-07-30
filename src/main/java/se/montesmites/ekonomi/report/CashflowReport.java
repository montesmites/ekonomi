package se.montesmites.ekonomi.report;

import java.util.List;

public class CashflowReport {
    private final List<Row> rows;

    public CashflowReport(List<Row> rows) {
        this.rows = rows;
    }
    
    public List<Row> getRows() {
        return rows;
    }
    
    public int getRowCount() {
        return rows.size();
    }
}
