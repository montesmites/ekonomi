package se.montesmites.ekonomi.configuration;

import java.nio.file.Path;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("ekonomi")
@Getter
@Setter
public class EkonomiProperties {

  private BackupProperties backup;
  private DatasourceProperties datasource;
  private ReportProperties report;

  @Getter
  @Setter
  public static class BackupProperties {

    private String postgresPath;
    private String destinationPath;
  }

  @Getter
  @Setter
  public static class DatasourceProperties {

    private DatasourceType type;
    private String sieInputPath;
    private String spcsInputDir;
  }

  @Getter
  @Setter
  public static class ReportProperties {

    private String title;
    private TemplateProperties template;
    private Integer fiscalYear;
    private String outputDir;

    @Getter
    @Setter
    public static class TemplateProperties {

      private TemplateType type;
      private String path;

      public Path asPath() {
        return type.asPath(path);
      }
    }
  }
}
