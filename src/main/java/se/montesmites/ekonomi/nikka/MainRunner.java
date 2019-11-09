package se.montesmites.ekonomi.nikka;

import static java.util.stream.Collectors.toList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import se.montesmites.ekonomi.jpa.model.Bokfaar;
import se.montesmites.ekonomi.jpa.model.Konto;
import se.montesmites.ekonomi.jpa.model.Reing;
import se.montesmites.ekonomi.jpa.model.Ver;
import se.montesmites.ekonomi.jpa.model.Verrad;
import se.montesmites.ekonomi.jpa.repository.BokfaarRepository;
import se.montesmites.ekonomi.jpa.repository.KontoRepository;
import se.montesmites.ekonomi.jpa.repository.ReingRepository;
import se.montesmites.ekonomi.jpa.repository.VerRepository;
import se.montesmites.ekonomi.jpa.repository.VerradRepository;

@Component
@Profile("!test")
public class MainRunner implements ApplicationRunner {

  @Autowired
  private BokfaarRepository bokfaarRepository;
  @Autowired
  private KontoRepository kontoRepository;
  @Autowired
  private ReingRepository reingRepository;
  @Autowired
  private VerRepository verRepository;
  @Autowired
  private VerradRepository verradRepository;

  @Override
  public void run(ApplicationArguments applicationArguments) {
    // Main.main(new String[]{});
    System.out.println(bokfaarRepository.findAll().stream().map(Bokfaar::toYear).collect(toList()));
    System.out.println(kontoRepository.findAll().stream().map(Konto::toAccount).collect(toList()));
    System.out.println(
        reingRepository.findByKontoIdIsNotNullAndIngaendeBalansIsNotNull().stream()
            .map(Reing::toBalance)
            .collect(toList()));
    System.out.println(verRepository.findAll().stream().map(Ver::toEvent).collect(toList()));
    System.out.println(verradRepository.findAll().stream().map(Verrad::toEntry).collect(toList()));
  }
}
