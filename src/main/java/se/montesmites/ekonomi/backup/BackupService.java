package se.montesmites.ekonomi.backup;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import se.montesmites.ekonomi.configuration.EkonomiProperties;

@Service
@RequiredArgsConstructor
@Slf4j
public class BackupService {

  private final EkonomiProperties ekonomiProperties;

  @PersistenceContext private EntityManager entityManager;

  @Transactional
  public void backupDatabaseToJsonFiles() {
    var template =
        """
              copy (
                  select json_agg(row_to_json(data)) :::: text
                  from public.%2$s data
              ) to '%1$s';
        """;
    try {
      var timestamp = DateTimeFormatter.ofPattern("yyyyMMddHHmmss").format(LocalDateTime.now());
      var postgresPath = Paths.get(ekonomiProperties.getBackup().getPostgresPath());
      var destinationPath = Paths.get(ekonomiProperties.getBackup().getDestinationPath());
      var postgresDir = Files.createDirectories(postgresPath.resolve(timestamp));
      var destinationDir = Files.createDirectories(destinationPath.resolve(timestamp));
      var tables = List.of("fiscal_year", "account", "balance", "event", "entry");
      for (var table : tables) {
        var fileNameFormat = "%s-%s.json";
        var postgresFile = postgresDir.resolve(fileNameFormat.formatted(timestamp, table));
        var destinationFile = destinationDir.resolve(fileNameFormat.formatted(timestamp, table));
        var script = template.formatted(postgresFile, table);
        var result = entityManager.createNativeQuery(script).executeUpdate();
        Files.copy(postgresFile, destinationFile);
        Files.delete(postgresFile);
        log.info(
            "Saved backup of table {} via {} to {} with result {}.",
            table,
            postgresFile,
            destinationFile,
            result);
      }
      Files.delete(postgresDir);
    } catch (Exception e) {
      log.error("Could not backup database to json files: ", e);
      throw new RuntimeException(e);
    }
  }
}
