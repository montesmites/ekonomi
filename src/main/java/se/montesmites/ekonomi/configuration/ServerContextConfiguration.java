package se.montesmites.ekonomi.configuration;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import se.montesmites.ekonomi.backup.BackupService;

@Configuration
@RequiredArgsConstructor
@Profile("!test")
public class ServerContextConfiguration {

  private final BackupService backupService;

  @Bean
  public ServletListenerRegistrationBean<ServletContextListener> servletListener() {
    return new ServletListenerRegistrationBean<>(
        new ServletContextListener() {
          @Override
          public void contextDestroyed(ServletContextEvent sce) {
            backupService.backupDatabaseToJsonFiles();
          }
        });
  }
}
