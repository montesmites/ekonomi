package se.montesmites.ekonomi.nikka;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class MainRunner implements ApplicationRunner {

  @Override
  public void run(ApplicationArguments applicationArguments) {
    Main.main(new String[]{});
  }
}
