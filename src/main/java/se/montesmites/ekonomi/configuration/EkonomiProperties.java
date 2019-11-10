package se.montesmites.ekonomi.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import se.montesmites.ekonomi.datasource.DatasourceType;

@Component
@ConfigurationProperties("ekonomi")
public class EkonomiProperties {

  private DatasourceProperties datasource;
  private ReportProperties report;

  public static class DatasourceProperties {

    private DatasourceType type;
    private String sieInputPath;
    private String spcsInputDir;

    public DatasourceType getType() {
      return type;
    }

    public void setType(DatasourceType type) {
      this.type = type;
    }

    public String getSieInputPath() {
      return sieInputPath;
    }

    public void setSieInputPath(String sieInputPath) {
      this.sieInputPath = sieInputPath;
    }

    public String getSpcsInputDir() {
      return spcsInputDir;
    }

    public void setSpcsInputDir(String spcsInputDir) {
      this.spcsInputDir = spcsInputDir;
    }
  }

  public static class ReportProperties {

    private String title;
    private String templatePath;
    private Integer fiscalYear;
    private String outputDir;

    public String getTitle() {
      return title;
    }

    public void setTitle(String title) {
      this.title = title;
    }

    public String getTemplatePath() {
      return templatePath;
    }

    public void setTemplatePath(String templatePath) {
      this.templatePath = templatePath;
    }

    public Integer getFiscalYear() {
      return fiscalYear;
    }

    public void setFiscalYear(Integer fiscalYear) {
      this.fiscalYear = fiscalYear;
    }

    public String getOutputDir() {
      return outputDir;
    }

    public void setOutputDir(String outputDir) {
      this.outputDir = outputDir;
    }
  }

  public DatasourceProperties getDatasource() {
    return datasource;
  }

  public void setDatasource(
      DatasourceProperties datasource) {
    this.datasource = datasource;
  }

  public ReportProperties getReport() {
    return report;
  }

  public void setReport(ReportProperties report) {
    this.report = report;
  }
}
