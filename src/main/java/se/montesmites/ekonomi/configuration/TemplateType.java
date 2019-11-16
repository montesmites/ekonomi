package se.montesmites.ekonomi.configuration;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

public enum TemplateType {
  FILE {
    @Override
    Path asPath(String path) {
      return Paths.get(path);
    }
  },
  RESOURCE {
    @Override
    Path asPath(String path) {
      try {
        return Paths.get(getClass().getResource(path).toURI());
      } catch (URISyntaxException e) {
        throw new RuntimeException(e);
      }
    }
  };

  abstract Path asPath(String path);
}
