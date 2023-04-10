package se.montesmites.ekonomi.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import se.montesmites.ekonomi.jpa.model.Bokfaar;

@Repository
public interface BokfaarRepository extends JpaRepository<Bokfaar, String> {}
