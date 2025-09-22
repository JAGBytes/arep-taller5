// Configuration
const API_BASE_URL = 'http://localhost:8080/api/properties';

// Global state
let properties = [];
let currentPropertyId = null;
let isEditMode = false;

// DOM elements
const propertyListSection = document.getElementById('propertyList');
const propertyFormSection = document.getElementById('propertyForm');
const propertiesContainer = document.getElementById('propertiesContainer');
const loadingMessage = document.getElementById('loadingMessage');
const emptyMessage = document.getElementById('emptyMessage');
const propertyFormElement = document.getElementById('propertyFormElement');
const formTitle = document.getElementById('formTitle');
const addPropertyBtn = document.getElementById('addPropertyBtn');
const cancelBtn = document.getElementById('cancelBtn');
const propertyModal = document.getElementById('propertyModal');
const modalBody = document.getElementById('modalBody');
const closeModal = document.getElementById('closeModal');
const editPropertyBtn = document.getElementById('editPropertyBtn');
const deletePropertyBtn = document.getElementById('deletePropertyBtn');
const toastContainer = document.getElementById('toastContainer');

// Initialize the application
document.addEventListener('DOMContentLoaded', function() {
    initializeEventListeners();
    loadProperties();
});

// Event Listeners
function initializeEventListeners() {
    // Form submission
    if (propertyFormElement) {
        propertyFormElement.addEventListener('submit', handleFormSubmit);
    }
    
    // Button clicks
    if (addPropertyBtn) addPropertyBtn.addEventListener('click', showAddForm);
    if (cancelBtn) cancelBtn.addEventListener('click', hideForm);
    if (closeModal) closeModal.addEventListener('click', hideModal);
    if (editPropertyBtn) editPropertyBtn.addEventListener('click', editCurrentProperty);
    if (deletePropertyBtn) deletePropertyBtn.addEventListener('click', deleteCurrentProperty);
    
    // Modal click outside to close
    if (propertyModal) {
        propertyModal.addEventListener('click', function(e) {
            if (e.target === propertyModal) {
                hideModal();
            }
        });
    }
    
    // Form validation on input
    if (propertyFormElement) {
        const formInputs = propertyFormElement.querySelectorAll('input, textarea');
        formInputs.forEach(input => {
            input.addEventListener('blur', (e) => validateField(e.target));
            input.addEventListener('input', (e) => clearFieldError(e.target.id));
        });
    }
}

// API Functions
async function apiRequest(url, options = {}) {
    try {
        const response = await fetch(url, {
            headers: {
                'Content-Type': 'application/json',
                ...options.headers
            },
            ...options
        });
        
        if (!response.ok) {
            const errorData = await response.json().catch(() => ({}));
            throw new Error(errorData.message || `HTTP error! status: ${response.status}`);
        }
        
        return await response.json();
    } catch (error) {
        console.error('API Error:', error);
        showToast('Error en la comunicación con el servidor', 'error');
        throw error;
    }
}

async function loadProperties() {
    try {
        showLoading(true);
        properties = await apiRequest(API_BASE_URL);
        renderProperties();
    } catch (error) {
        console.error('Error loading properties:', error);
        showToast('Error al cargar las propiedades', 'error');
        properties = []; // Reset to empty array on error
        renderProperties();
    } finally {
        showLoading(false);
    }
}

async function createProperty(propertyData) {
    try {
        const newProperty = await apiRequest(API_BASE_URL, {
            method: 'POST',
            body: JSON.stringify(propertyData)
        });
        properties.push(newProperty);
        renderProperties();
        showToast('Propiedad creada exitosamente', 'success');
        hideForm();
    } catch (error) {
        console.error('Error creating property:', error);
        showToast('Error al crear la propiedad', 'error');
    }
}

async function updateProperty(id, propertyData) {
    try {
        const numericId = parseInt(id);
        const updatedProperty = await apiRequest(`${API_BASE_URL}/${numericId}`, {
            method: 'PUT',
            body: JSON.stringify(propertyData)
        });
        
        // Find the property to update (handle both string and number comparisons)
        const index = properties.findIndex(p => {
            const propertyId = typeof p.id === 'string' ? parseInt(p.id) : p.id;
            return propertyId === numericId;
        });
        
        if (index !== -1) {
            properties[index] = updatedProperty;
        }
        renderProperties();
        showToast('Propiedad actualizada exitosamente', 'success');
        hideForm();
    } catch (error) {
        console.error('Error updating property:', error);
        showToast('Error al actualizar la propiedad', 'error');
    }
}

// Reemplaza la función deleteProperty existente con esta versión corregida
async function deleteProperty(id) {
    try {
        const numericId = parseInt(id);
        
        const response = await fetch(`${API_BASE_URL}/${numericId}`, {
            method: 'DELETE'
        });
        
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        
        // No intentar parsear JSON si no hay contenido
        // Solo filtrar la propiedad del array local
        properties = properties.filter(p => {
            const propertyId = typeof p.id === 'string' ? parseInt(p.id) : p.id;
            return propertyId !== numericId;
        });
        
        renderProperties();
        showToast('Propiedad eliminada exitosamente', 'success');
        hideModal();
        
    } catch (error) {
        console.error('Error deleting property:', error);
        showToast('Error al eliminar la propiedad', 'error');
    }
}

async function getPropertyById(id) {
    try {
        const numericId = parseInt(id);
        return await apiRequest(`${API_BASE_URL}/${numericId}`);
    } catch (error) {
        console.error('Error getting property:', error);
        showToast('Error al cargar los detalles de la propiedad', 'error');
        throw error;
    }
}

// UI Functions
function showLoading(show) {
    if (loadingMessage) loadingMessage.style.display = show ? 'block' : 'none';
    if (propertiesContainer) propertiesContainer.style.display = show ? 'none' : 'grid';
    if (emptyMessage) emptyMessage.style.display = 'none';
}

function renderProperties() {
    if (!propertiesContainer) return;
    
    if (properties.length === 0) {
        propertiesContainer.style.display = 'none';
        if (emptyMessage) emptyMessage.style.display = 'block';
        return;
    }
    
    propertiesContainer.style.display = 'grid';
    if (emptyMessage) emptyMessage.style.display = 'none';
    
    propertiesContainer.innerHTML = properties.map(property => `
        <div class="property-card" onclick="showPropertyDetails(${property.id})">
            <h3>
                <i class="fas fa-home"></i>
                Propiedad #${property.id}
            </h3>
            <div class="address">
                <i class="fas fa-map-marker-alt"></i>
                ${escapeHtml(property.address || 'Sin dirección')}
            </div>
            <div class="property-details">
                <div class="property-detail">
                    <span class="label">Precio</span>
                    <span class="value price">$${formatNumber(property.price || 0)}</span>
                </div>
                <div class="property-detail">
                    <span class="label">Tamaño</span>
                    <span class="value size">${formatNumber(property.size || 0)} m²</span>
                </div>
            </div>
            ${property.description ? `
                <div class="property-description">
                    ${escapeHtml(property.description)}
                </div>
            ` : ''}
            <div class="property-actions">
                <button class="btn btn-primary" onclick="event.stopPropagation(); showPropertyDetails(${property.id})" title="Ver detalles">
                    <i class="fas fa-eye"></i> Ver
                </button>
                <button class="btn btn-success" onclick="event.stopPropagation(); editProperty(${property.id})" title="Editar propiedad">
                    <i class="fas fa-edit"></i> Editar
                </button>
                <button class="btn btn-danger" onclick="event.stopPropagation(); confirmDelete(${property.id})" title="Eliminar propiedad">
                    <i class="fas fa-trash"></i> Eliminar
                </button>
            </div>
        </div>
    `).join('');
}

function showAddForm() {
    isEditMode = false;
    currentPropertyId = null;
    if (formTitle) formTitle.textContent = 'Nueva Propiedad';
    if (propertyFormElement) propertyFormElement.reset();
    clearAllErrors();
    if (propertyListSection) propertyListSection.style.display = 'none';
    if (propertyFormSection) propertyFormSection.style.display = 'block';
    
    // Focus on first field
    const addressField = document.getElementById('address');
    if (addressField) {
        setTimeout(() => addressField.focus(), 100);
    }
}

function editProperty(id) {
    const property = properties.find(p => p.id === id);
    if (!property) {
        showToast('Propiedad no encontrada', 'error');
        return;
    }
    
    isEditMode = true;
    currentPropertyId = id;
    if (formTitle) formTitle.textContent = 'Editar Propiedad';
    
    // Fill form with property data
    const fields = {
        address: property.address || '',
        price: property.price || '',
        size: property.size || '',
        description: property.description || ''
    };
    
    Object.entries(fields).forEach(([fieldName, value]) => {
        const field = document.getElementById(fieldName);
        if (field) field.value = value;
    });
    
    clearAllErrors();
    if (propertyListSection) propertyListSection.style.display = 'none';
    if (propertyFormSection) propertyFormSection.style.display = 'block';
    
    // Focus on first field
    const addressField = document.getElementById('address');
    if (addressField) {
        setTimeout(() => addressField.focus(), 100);
    }
}

function hideForm() {
    if (propertyFormSection) propertyFormSection.style.display = 'none';
    if (propertyListSection) propertyListSection.style.display = 'block';
    if (propertyFormElement) propertyFormElement.reset();
    clearAllErrors();
}

async function showPropertyDetails(id) {
    try {
        const property = await getPropertyById(id);
        currentPropertyId = id;
        
        if (modalBody) {
            modalBody.innerHTML = `
                <div class="property-details-full">
                    <div class="detail-group">
                        <h4><i class="fas fa-map-marker-alt"></i> Dirección</h4>
                        <p>${escapeHtml(property.address || 'Sin dirección')}</p>
                    </div>
                    <div class="detail-group">
                        <h4><i class="fas fa-dollar-sign"></i> Precio</h4>
                        <p class="price">$${formatNumber(property.price || 0)}</p>
                    </div>
                    <div class="detail-group">
                        <h4><i class="fas fa-expand-arrows-alt"></i> Tamaño</h4>
                        <p>${formatNumber(property.size || 0)} metros cuadrados</p>
                    </div>
                    ${property.description ? `
                        <div class="detail-group">
                            <h4><i class="fas fa-align-left"></i> Descripción</h4>
                            <p>${escapeHtml(property.description)}</p>
                        </div>
                    ` : ''}
                </div>
            `;
        }
        
        if (propertyModal) propertyModal.style.display = 'flex';
    } catch (error) {
        console.error('Error showing property details:', error);
    }
}

function hideModal() {
    if (propertyModal) propertyModal.style.display = 'none';
    currentPropertyId = null;
}

function editCurrentProperty() {
    if (currentPropertyId) {
        editProperty(currentPropertyId);
        hideModal();
    }
}

function deleteCurrentProperty() {
    console.log('deleteCurrentProperty called, currentPropertyId:', currentPropertyId, 'Type:', typeof currentPropertyId);
    if (currentPropertyId) {
        if (confirm('¿Estás seguro de que deseas eliminar esta propiedad? Esta acción no se puede deshacer.')) {
            deleteProperty(currentPropertyId);
        }
    } else {
        console.error('No currentPropertyId set');
        showToast('Error: No se ha seleccionado una propiedad para eliminar', 'error');
    }
}

function confirmDelete(id) {
    if (confirm('¿Estás seguro de que deseas eliminar esta propiedad? Esta acción no se puede deshacer.')) {
        deleteProperty(id);
    }
}

// Form Handling
function handleFormSubmit(e) {
    e.preventDefault();
    
    if (!validateForm()) {
        showToast('Por favor corrige los errores en el formulario', 'error');
        return;
    }
    
    const formData = new FormData(propertyFormElement);
    const priceValue = formData.get('price');
    const sizeValue = formData.get('size');
    
    const propertyData = {
        address: formData.get('address')?.trim() || '',
        price: priceValue ? parseFloat(priceValue) : null,
        size: sizeValue ? parseFloat(sizeValue) : null,
        description: formData.get('description')?.trim() || ''
    };
    
    if (isEditMode && currentPropertyId) {
        updateProperty(currentPropertyId, propertyData);
    } else {
        createProperty(propertyData);
    }
}

// Validation Functions
function validateForm() {
    let isValid = true;
    const fields = ['address', 'price', 'size'];
    
    fields.forEach(fieldName => {
        const field = document.getElementById(fieldName);
        if (field && !validateField(field)) {
            isValid = false;
        }
    });
    
    return isValid;
}

function validateField(field) {
    if (!field) return true;
    
    const fieldName = field.id;
    const value = field.value.trim();
    const errorElement = document.getElementById(fieldName + 'Error');
    let isValid = true;
    let errorMessage = '';
    
    // Required field validation
    if (!value && ['address', 'price', 'size'].includes(fieldName)) {
        isValid = false;
        errorMessage = 'Este campo es obligatorio';
    } else if (value) {
        // Specific validations
        switch (fieldName) {
            case 'address':
                if (value.length < 5) {
                    isValid = false;
                    errorMessage = 'La dirección debe tener al menos 5 caracteres';
                } else if (value.length > 255) {
                    isValid = false;
                    errorMessage = 'La dirección no puede exceder 255 caracteres';
                }
                break;
            case 'price':
                const price = parseFloat(value);
                if (isNaN(price) || price <= 0) {
                    isValid = false;
                    errorMessage = 'El precio debe ser un número mayor a 0';
                } else if (price > 999999999) {
                    isValid = false;
                    errorMessage = 'El precio es demasiado alto';
                }
                break;
            case 'size':
                const size = parseFloat(value);
                if (isNaN(size) || size <= 0) {
                    isValid = false;
                    errorMessage = 'El tamaño debe ser un número mayor a 0';
                } else if (size > 999999) {
                    isValid = false;
                    errorMessage = 'El tamaño es demasiado alto';
                }
                break;
            case 'description':
                if (value.length > 1000) {
                    isValid = false;
                    errorMessage = 'La descripción no puede exceder 1000 caracteres';
                }
                break;
        }
    }
    
    // Update UI
    if (errorElement) {
        if (isValid) {
            field.classList.remove('error');
            errorElement.classList.remove('show');
        } else {
            field.classList.add('error');
            errorElement.textContent = errorMessage;
            errorElement.classList.add('show');
        }
    }
    
    return isValid;
}

function clearFieldError(fieldName) {
    const field = document.getElementById(fieldName);
    const errorElement = document.getElementById(fieldName + 'Error');
    if (field) field.classList.remove('error');
    if (errorElement) errorElement.classList.remove('show');
}

function clearAllErrors() {
    const errorElements = document.querySelectorAll('.error-message');
    const inputElements = document.querySelectorAll('input, textarea');
    
    errorElements.forEach(element => {
        element.classList.remove('show');
    });
    
    inputElements.forEach(element => {
        element.classList.remove('error');
    });
}

// Utility Functions
function formatNumber(number) {
    if (number == null || isNaN(number)) return '0';
    return new Intl.NumberFormat('es-CO').format(number);
}

function escapeHtml(text) {
    if (!text) return '';
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}

function showToast(message, type = 'info') {
    if (!toastContainer) return;
    
    const toast = document.createElement('div');
    toast.className = `toast ${type}`;
    
    const icon = type === 'success' ? 'check-circle' : 
                 type === 'error' ? 'exclamation-circle' : 'info-circle';
    
    toast.innerHTML = `
        <i class="fas fa-${icon}"></i>
        <span>${escapeHtml(message)}</span>
    `;
    
    toastContainer.appendChild(toast);
    
    // Auto remove after 5 seconds
    setTimeout(() => {
        if (toast.parentNode) {
            toast.parentNode.removeChild(toast);
        }
    }, 5000);
    
    // Add click to dismiss
    toast.addEventListener('click', () => {
        if (toast.parentNode) {
            toast.parentNode.removeChild(toast);
        }
    });
}

// Additional styles for property details modal (complementing styles.css)
const additionalStyles = `
    .property-details-full {
        display: flex;
        flex-direction: column;
        gap: 25px;
    }
    
    .detail-group {
        padding: 15px 20px;
        background: rgba(52, 152, 219, 0.05);
        border-radius: 10px;
        border-left: 4px solid #3498db;
    }
    
    .detail-group h4 {
        color: #2c3e50;
        margin-bottom: 10px;
        font-size: 1.1rem;
        display: flex;
        align-items: center;
        gap: 10px;
        font-weight: 600;
    }
    
    .detail-group h4 i {
        color: #3498db;
        width: 20px;
        text-align: center;
    }
    
    .detail-group p {
        color: #34495e;
        font-size: 1rem;
        line-height: 1.5;
        margin: 0;
        word-wrap: break-word;
        font-weight: 500;
    }
    
    .detail-group .price {
        color: #27ae60 !important;
        font-weight: 700;
        font-size: 1.3rem;
        text-shadow: 0 1px 2px rgba(39, 174, 96, 0.2);
    }
    
    .toast {
        cursor: pointer;
        transition: all 0.3s ease;
        position: relative;
        overflow: hidden;
    }
    
    .toast:hover {
        transform: translateX(-5px);
        box-shadow: 0 6px 20px rgba(0,0,0,0.25);
    }
    
    .toast::after {
        content: '×';
        position: absolute;
        top: 5px;
        right: 10px;
        color: #7f8c8d;
        font-size: 14px;
        opacity: 0;
        transition: opacity 0.3s ease;
    }
    
    .toast:hover::after {
        opacity: 1;
    }
    
    /* Enhanced modal animations */
    .modal {
        animation: modalFadeIn 0.3s ease;
    }
    
    @keyframes modalFadeIn {
        from {
            opacity: 0;
            backdrop-filter: blur(0px);
        }
        to {
            opacity: 1;
            backdrop-filter: blur(5px);
        }
    }
    
    /* Loading state improvements */
    .loading {
        background: rgba(255,255,255,0.9);
        border-radius: 12px;
        margin: 20px;
        box-shadow: 0 4px 15px rgba(0,0,0,0.1);
    }
    
    .loading i {
        background: linear-gradient(45deg, #3498db, #2980b9);
        -webkit-background-clip: text;
        -webkit-text-fill-color: transparent;
        background-clip: text;
    }
`;

// Inject additional styles
if (!document.getElementById('dynamic-styles')) {
    const styleSheet = document.createElement('style');
    styleSheet.id = 'dynamic-styles';
    styleSheet.textContent = additionalStyles;
    document.head.appendChild(styleSheet);
}

// Global functions for onclick handlers
window.showPropertyDetails = showPropertyDetails;
window.editProperty = editProperty;
window.confirmDelete = confirmDelete;