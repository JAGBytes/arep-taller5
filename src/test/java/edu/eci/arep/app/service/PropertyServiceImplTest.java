package edu.eci.arep.app.service;

import edu.eci.arep.app.model.Property;
import edu.eci.arep.app.repository.PropertyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PropertyServiceImplTest {

    @Mock
    private PropertyRepository propertyRepository;

    @InjectMocks
    private PropertyServiceImpl propertyService;

    private Property testProperty;

    @BeforeEach
    void setUp() {
        testProperty = createTestProperty(1L, "Calle 123", 100000.0, 50.0, "Casa bonita");
    }

    @Test
    void getAllProperties_ShouldReturnAllProperties() {
        // Arrange
        Property property1 = createTestProperty(1L, "Calle 123", 100000.0, 50.0, "Casa bonita");
        Property property2 = createTestProperty(2L, "Carrera 456", 200000.0, 80.0, "Apartamento moderno");
        List<Property> expectedProperties = Arrays.asList(property1, property2);

        when(propertyRepository.findAll()).thenReturn(expectedProperties);

        // Act
        List<Property> result = propertyService.getAllProperties();

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        assertThat(result).containsExactlyInAnyOrder(property1, property2);
        verify(propertyRepository, times(1)).findAll();
    }

    @Test
    void getAllProperties_WhenNoPropertiesExist_ShouldReturnEmptyList() {
        // Arrange
        when(propertyRepository.findAll()).thenReturn(Arrays.asList());

        // Act
        List<Property> result = propertyService.getAllProperties();

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();
        verify(propertyRepository, times(1)).findAll();
    }

    @Test
    void getPropertyById_WhenPropertyExists_ShouldReturnProperty() {
        // Arrange
        when(propertyRepository.findById(1L)).thenReturn(Optional.of(testProperty));

        // Act
        Optional<Property> result = propertyService.getPropertyById(1L);

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(testProperty);
        verify(propertyRepository, times(1)).findById(1L);
    }

    @Test
    void getPropertyById_WhenPropertyDoesNotExist_ShouldReturnEmpty() {
        // Arrange
        when(propertyRepository.findById(999L)).thenReturn(Optional.empty());

        // Act
        Optional<Property> result = propertyService.getPropertyById(999L);

        // Assert
        assertThat(result).isEmpty();
        verify(propertyRepository, times(1)).findById(999L);
    }

    @Test
    void saveProperty_WithValidProperty_ShouldReturnSavedProperty() {
        // Arrange
        Property propertyToSave = createTestProperty(null, "Calle 123", 100000.0, 50.0, "Casa bonita");
        Property savedProperty = createTestProperty(1L, "Calle 123", 100000.0, 50.0, "Casa bonita");

        when(propertyRepository.save(propertyToSave)).thenReturn(savedProperty);

        // Act
        Property result = propertyService.saveProperty(propertyToSave);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getAddress()).isEqualTo("Calle 123");
        assertThat(result.getPrice()).isEqualTo(100000.0);
        assertThat(result.getSize()).isEqualTo(50.0);
        assertThat(result.getDescription()).isEqualTo("Casa bonita");
        verify(propertyRepository, times(1)).save(propertyToSave);
    }

    @Test
    void saveProperty_WithUpdatedProperty_ShouldReturnUpdatedProperty() {
        // Arrange
        Property existingProperty = createTestProperty(1L, "Calle 123", 100000.0, 50.0, "Casa bonita");
        Property updatedProperty = createTestProperty(1L, "Calle 456", 150000.0, 75.0, "Casa renovada");

        when(propertyRepository.save(existingProperty)).thenReturn(updatedProperty);

        // Act
        Property result = propertyService.saveProperty(existingProperty);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getAddress()).isEqualTo("Calle 456");
        assertThat(result.getPrice()).isEqualTo(150000.0);
        assertThat(result.getSize()).isEqualTo(75.0);
        assertThat(result.getDescription()).isEqualTo("Casa renovada");
        verify(propertyRepository, times(1)).save(existingProperty);
    }

    @Test
    void deleteProperty_WhenPropertyExists_ShouldDeleteSuccessfully() {
        // Arrange
        when(propertyRepository.existsById(1L)).thenReturn(true);
        doNothing().when(propertyRepository).deleteById(1L);

        // Act
        propertyService.deleteProperty(1L);

        // Assert
        verify(propertyRepository, times(1)).existsById(1L);
        verify(propertyRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteProperty_WhenPropertyDoesNotExist_ShouldThrowException() {
        // Arrange
        when(propertyRepository.existsById(999L)).thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> propertyService.deleteProperty(999L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Property not found with id 999");

        verify(propertyRepository, times(1)).existsById(999L);
        verify(propertyRepository, never()).deleteById(anyLong());
    }

    @Test
    void deleteProperty_WithNullId_ShouldThrowException() {
        // Act & Assert
        assertThatThrownBy(() -> propertyService.deleteProperty(null))
                .isInstanceOf(Exception.class);
    }

    @Test
    void saveProperty_WithMinimumValidData_ShouldReturnSavedProperty() {
        // Arrange
        Property propertyToSave = createTestProperty(null, "Test Address", 1.0, 1.0, null);
        Property savedProperty = createTestProperty(1L, "Test Address", 1.0, 1.0, null);

        when(propertyRepository.save(propertyToSave)).thenReturn(savedProperty);

        // Act
        Property result = propertyService.saveProperty(propertyToSave);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getAddress()).isEqualTo("Test Address");
        assertThat(result.getPrice()).isEqualTo(1.0);
        assertThat(result.getSize()).isEqualTo(1.0);
        assertThat(result.getDescription()).isNull();
        verify(propertyRepository, times(1)).save(propertyToSave);
    }

    @Test
    void saveProperty_WithMaximumValidData_ShouldReturnSavedProperty() {
        // Arrange
        String longDescription = "A".repeat(1000); // Maximum length description
        Property propertyToSave = createTestProperty(null, "Test Address", Double.MAX_VALUE, Double.MAX_VALUE,
                longDescription);
        Property savedProperty = createTestProperty(1L, "Test Address", Double.MAX_VALUE, Double.MAX_VALUE,
                longDescription);

        when(propertyRepository.save(propertyToSave)).thenReturn(savedProperty);

        // Act
        Property result = propertyService.saveProperty(propertyToSave);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getAddress()).isEqualTo("Test Address");
        assertThat(result.getPrice()).isEqualTo(Double.MAX_VALUE);
        assertThat(result.getSize()).isEqualTo(Double.MAX_VALUE);
        assertThat(result.getDescription()).isEqualTo(longDescription);
        verify(propertyRepository, times(1)).save(propertyToSave);
    }

    private Property createTestProperty(Long id, String address, Double price, Double size, String description) {
        Property property = new Property();
        property.setId(id);
        property.setAddress(address);
        property.setPrice(price);
        property.setSize(size);
        property.setDescription(description);
        return property;
    }
}
