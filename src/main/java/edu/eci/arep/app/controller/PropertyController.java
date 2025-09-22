package edu.eci.arep.app.controller;

import edu.eci.arep.app.dto.PropertyDTO;
import edu.eci.arep.app.model.Property;
import edu.eci.arep.app.service.PropertyService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/properties")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class PropertyController {

    private final PropertyService propertyService;

    public PropertyController(PropertyService propertyService) {
        this.propertyService = propertyService;
    }

    @GetMapping
    public List<PropertyDTO> getAll() {
        return propertyService.getAllProperties()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public PropertyDTO getById(@PathVariable("id") Long id) {
        Property property = propertyService.getPropertyById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Property not found"));
        return convertToDTO(property);
    }

    @PostMapping
    public PropertyDTO create(@RequestBody PropertyDTO dto) {
        if (dto.getPrice() == null || dto.getPrice() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Price must be greater than 0");
        }
        Property property = convertToEntity(dto);
        Property saved = propertyService.saveProperty(property);
        return convertToDTO(saved);
    }

    @PutMapping("/{id}")
    public PropertyDTO update(@PathVariable("id") Long id, @RequestBody PropertyDTO dto) {
        Property existing = propertyService.getPropertyById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Property not found"));

        // Validaciones extra
        if (dto.getPrice() == null || dto.getPrice() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Price must be greater than 0");
        }

        // Actualizar campos (full update)
        existing.setAddress(dto.getAddress());
        existing.setPrice(dto.getPrice());
        existing.setSize(dto.getSize());
        existing.setDescription(dto.getDescription());

        Property saved = propertyService.saveProperty(existing);
        return convertToDTO(saved);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") Long id) {
        if (propertyService.getPropertyById(id).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Property not found");
        }
        propertyService.deleteProperty(id);
    }

    private PropertyDTO convertToDTO(Property property) {
        return new PropertyDTO(
                property.getId(),
                property.getAddress(),
                property.getPrice(),
                property.getSize(),
                property.getDescription());
    }

    private Property convertToEntity(PropertyDTO dto) {
        Property property = new Property();
        property.setAddress(dto.getAddress());
        property.setPrice(dto.getPrice());
        property.setSize(dto.getSize());
        property.setDescription(dto.getDescription());
        return property;
    }
}
