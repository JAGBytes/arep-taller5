package edu.eci.arep.app.service;

import edu.eci.arep.app.model.Property;

import java.util.List;
import java.util.Optional;

public interface PropertyService {

    List<Property> getAllProperties();

    Optional<Property> getPropertyById(Long id);

    Property saveProperty(Property property);

    void deleteProperty(Long id);
}