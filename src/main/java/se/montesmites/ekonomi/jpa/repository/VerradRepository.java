package se.montesmites.ekonomi.jpa.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import se.montesmites.ekonomi.jpa.model.Verrad;
import se.montesmites.ekonomi.jpa.model.VerradId;

@Repository
public interface VerradRepository extends JpaRepository<Verrad, VerradId> {

  List<Verrad> findByDefinitivAndStruken(boolean definitiv, boolean struken);
}
