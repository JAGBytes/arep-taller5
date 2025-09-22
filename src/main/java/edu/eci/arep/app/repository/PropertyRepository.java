package edu.eci.arep.app.repository;

import edu.eci.arep.app.model.Property;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PropertyRepository extends JpaRepository<Property, Long> {
    // Aquí puedes definir queries personalizadas si las necesitas
}