package se.montesmites.ekonomi.parser.vismaadmin200.v2015_0;

enum BinaryFile_2015_0_Definition {
    YEARS("BOKFAAR.DBF");
    
    private final String fileName;

    private BinaryFile_2015_0_Definition(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }
}
