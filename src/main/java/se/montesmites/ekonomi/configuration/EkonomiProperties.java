package se.montesmites.ekonomi.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import se.montesmites.ekonomi.datasource.DatasourceType;

@Configuration
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "ekonomi.report")
public class EkonomiProperties {

  private DatasourceType datasourceType;
  private String title;
  private String templatePath;
  private Integer fiscalYear;
  private String outputDir;

  public DatasourceType getDatasourceType() {
    return datasourceType;
  }

  public void setDatasourceType(DatasourceType datasourceType) {
    this.datasourceType = datasourceType;
  }

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
