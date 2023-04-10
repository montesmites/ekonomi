package se.montesmites.ekonomi.configuration;

import java.nio.file.Path;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("ekonomi")
public class EkonomiProperties {

  private DatasourceProperties datasource;
  private ReportProperties report;

  public DatasourceProperties getDatasource() {
    return datasource;
  }

  public void setDatasource(DatasourceProperties datasource) {
    this.datasource = datasource;
  }

  public ReportProperties getReport() {
    return report;
  }

  public void setReport(ReportProperties report) {
    this.report = report;
  }

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
    private TemplateProperties template;
    private Integer fiscalYear;
    private String outputDir;

    public String getTitle() {
      return title;
    }

    public void setTitle(String title) {
      this.title = title;
    }

    public TemplateProperties getTemplate() {
      return template;
    }

    public void setTemplate(TemplateProperties template) {
      this.template = template;
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

    public static class TemplateProperties {

      private TemplateType type;
      private String path;

      public TemplateType getType() {
        return type;
      }

      public void setType(TemplateType type) {
        this.type = type;
      }

      public String getPath() {
        return path;
      }

      public void setPath(String path) {
        this.path = path;
      }

      public Path asPath() {
        return type.asPath(path);
      }
    }
  }
}
