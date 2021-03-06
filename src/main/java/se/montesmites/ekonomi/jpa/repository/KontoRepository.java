package se.montesmites.ekonomi.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import se.montesmites.ekonomi.jpa.model.Konto;
import se.montesmites.ekonomi.jpa.model.KontoId;

@Repository
public interface KontoRepository extends JpaRepository<Konto, KontoId> {

}
