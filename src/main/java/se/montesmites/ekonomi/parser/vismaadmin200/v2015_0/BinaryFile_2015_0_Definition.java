package se.montesmites.ekonomi.parser.vismaadmin200.v2015_0;

enum BinaryFile_2015_0_Definition {
    YEARS("BOKFAAR.DBF", 513, 89);

    private final String fileName;
    private final int start;
    private final int length;

    private BinaryFile_2015_0_Definition(String fileName, int start, int length) {
        this.fileName = fileName;
        this.start = start;
        this.length = length;
    }

    public String getFileName() {
        return fileName;
    }

    public int getStart() {
        return start;
    }

    public int getLength() {
        return length;
    }
}
