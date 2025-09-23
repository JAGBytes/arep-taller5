package edu.eci.arep.app.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.eci.arep.app.dto.PropertyDTO;
import edu.eci.arep.app.model.Property;
import edu.eci.arep.app.service.PropertyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class PropertyControllerTest {

    @Mock
    private PropertyService propertyService;

    @InjectMocks
    private PropertyController propertyController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(propertyController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void getAllProperties_ShouldReturnListOfProperties() throws Exception {
        // Arrange
        Property property1 = createTestProperty(1L, "Calle 123", 100000.0, 50.0, "Casa bonita");
        Property property2 = createTestProperty(2L, "Carrera 456", 200000.0, 80.0, "Apartamento moderno");
        List<Property> properties = Arrays.asList(property1, property2);

        when(propertyService.getAllProperties()).thenReturn(properties);

        // Act & Assert
        mockMvc.perform(get("/api/properties"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].address").value("Calle 123"))
                .andExpect(jsonPath("$[0].price").value(100000.0))
                .andExpect(jsonPath("$[0].size").value(50.0))
                .andExpect(jsonPath("$[0].description").value("Casa bonita"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].address").value("Carrera 456"))
                .andExpect(jsonPath("$[1].price").value(200000.0))
                .andExpect(jsonPath("$[1].size").value(80.0))
                .andExpect(jsonPath("$[1].description").value("Apartamento moderno"));

        verify(propertyService, times(1)).getAllProperties();
    }

    @Test
    void getPropertyById_WhenPropertyExists_ShouldReturnProperty() throws Exception {
        // Arrange
        Property property = createTestProperty(1L, "Calle 123", 100000.0, 50.0, "Casa bonita");
        when(propertyService.getPropertyById(1L)).thenReturn(Optional.of(property));

        // Act & Assert
        mockMvc.perform(get("/api/properties/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.address").value("Calle 123"))
                .andExpect(jsonPath("$.price").value(100000.0))
                .andExpect(jsonPath("$.size").value(50.0))
                .andExpect(jsonPath("$.description").value("Casa bonita"));

        verify(propertyService, times(1)).getPropertyById(1L);
    }

    @Test
    void getPropertyById_WhenPropertyDoesNotExist_ShouldReturnNotFound() throws Exception {
        // Arrange
        when(propertyService.getPropertyById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/api/properties/999"))
                .andExpect(status().isNotFound());

        verify(propertyService, times(1)).getPropertyById(999L);
    }

    @Test
    void createProperty_WithValidData_ShouldReturnCreatedProperty() throws Exception {
        // Arrange
        PropertyDTO inputDTO = new PropertyDTO(null, "Calle 123", 100000.0, 50.0, "Casa bonita");
        Property savedProperty = createTestProperty(1L, "Calle 123", 100000.0, 50.0, "Casa bonita");

        when(propertyService.saveProperty(any(Property.class))).thenReturn(savedProperty);

        // Act & Assert
        mockMvc.perform(post("/api/properties")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inputDTO)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.address").value("Calle 123"))
                .andExpect(jsonPath("$.price").value(100000.0))
                .andExpect(jsonPath("$.size").value(50.0))
                .andExpect(jsonPath("$.description").value("Casa bonita"));

        verify(propertyService, times(1)).saveProperty(any(Property.class));
    }

    @Test
    void createProperty_WithInvalidPrice_ShouldReturnBadRequest() throws Exception {
        // Arrange
        PropertyDTO inputDTO = new PropertyDTO(null, "Calle 123", -100.0, 50.0, "Casa bonita");

        // Act & Assert
        mockMvc.perform(post("/api/properties")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inputDTO)))
                .andExpect(status().isBadRequest());

        verify(propertyService, never()).saveProperty(any(Property.class));
    }

    @Test
    void createProperty_WithNullPrice_ShouldReturnBadRequest() throws Exception {
        // Arrange
        PropertyDTO inputDTO = new PropertyDTO(null, "Calle 123", null, 50.0, "Casa bonita");

        // Act & Assert
        mockMvc.perform(post("/api/properties")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inputDTO)))
                .andExpect(status().isBadRequest());

        verify(propertyService, never()).saveProperty(any(Property.class));
    }

    @Test
    void createProperty_WithZeroPrice_ShouldReturnBadRequest() throws Exception {
        // Arrange
        PropertyDTO inputDTO = new PropertyDTO(null, "Calle 123", 0.0, 50.0, "Casa bonita");

        // Act & Assert
        mockMvc.perform(post("/api/properties")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inputDTO)))
                .andExpect(status().isBadRequest());

        verify(propertyService, never()).saveProperty(any(Property.class));
    }

    @Test
    void updateProperty_WhenPropertyExists_ShouldReturnUpdatedProperty() throws Exception {
        // Arrange
        Property existingProperty = createTestProperty(1L, "Calle 123", 100000.0, 50.0, "Casa bonita");
        PropertyDTO updateDTO = new PropertyDTO(1L, "Calle 456", 150000.0, 75.0, "Casa renovada");
        Property updatedProperty = createTestProperty(1L, "Calle 456", 150000.0, 75.0, "Casa renovada");

        when(propertyService.getPropertyById(1L)).thenReturn(Optional.of(existingProperty));
        when(propertyService.saveProperty(any(Property.class))).thenReturn(updatedProperty);

        // Act & Assert
        mockMvc.perform(put("/api/properties/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.address").value("Calle 456"))
                .andExpect(jsonPath("$.price").value(150000.0))
                .andExpect(jsonPath("$.size").value(75.0))
                .andExpect(jsonPath("$.description").value("Casa renovada"));

        verify(propertyService, times(1)).getPropertyById(1L);
        verify(propertyService, times(1)).saveProperty(any(Property.class));
    }

    @Test
    void updateProperty_WhenPropertyDoesNotExist_ShouldReturnNotFound() throws Exception {
        // Arrange
        PropertyDTO updateDTO = new PropertyDTO(999L, "Calle 456", 150000.0, 75.0, "Casa renovada");
        when(propertyService.getPropertyById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(put("/api/properties/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isNotFound());

        verify(propertyService, times(1)).getPropertyById(999L);
        verify(propertyService, never()).saveProperty(any(Property.class));
    }

    @Test
    void updateProperty_WithInvalidPrice_ShouldReturnBadRequest() throws Exception {
        // Arrange
        Property existingProperty = createTestProperty(1L, "Calle 123", 100000.0, 50.0, "Casa bonita");
        PropertyDTO updateDTO = new PropertyDTO(1L, "Calle 456", -150000.0, 75.0, "Casa renovada");

        when(propertyService.getPropertyById(1L)).thenReturn(Optional.of(existingProperty));

        // Act & Assert
        mockMvc.perform(put("/api/properties/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isBadRequest());

        verify(propertyService, times(1)).getPropertyById(1L);
        verify(propertyService, never()).saveProperty(any(Property.class));
    }

    @Test
    void deleteProperty_WhenPropertyExists_ShouldReturnNoContent() throws Exception {
        // Arrange
        Property existingProperty = createTestProperty(1L, "Calle 123", 100000.0, 50.0, "Casa bonita");
        when(propertyService.getPropertyById(1L)).thenReturn(Optional.of(existingProperty));
        doNothing().when(propertyService).deleteProperty(1L);

        // Act & Assert
        mockMvc.perform(delete("/api/properties/1"))
                .andExpect(status().isOk());

        verify(propertyService, times(1)).getPropertyById(1L);
        verify(propertyService, times(1)).deleteProperty(1L);
    }

    @Test
    void deleteProperty_WhenPropertyDoesNotExist_ShouldReturnNotFound() throws Exception {
        // Arrange
        when(propertyService.getPropertyById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(delete("/api/properties/999"))
                .andExpect(status().isNotFound());

        verify(propertyService, times(1)).getPropertyById(999L);
        verify(propertyService, never()).deleteProperty(anyLong());
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
