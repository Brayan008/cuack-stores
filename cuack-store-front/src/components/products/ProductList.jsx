import React, { useEffect, useState } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { 
  Row, 
  Col, 
  Card, 
  Button, 
  Alert, 
  Form,
  InputGroup,
  Badge
} from 'react-bootstrap';
import { fetchProducts, checkProductAvailability } from '../../store/productsSlice';
import LoadingSpinner from '../ui/LoadingSpinner';

const ProductCard = ({ product, onCheckAvailability, availability }) => {
  const formatCurrency = (amount) => {
    return new Intl.NumberFormat('es-MX', {
      style: 'currency',
      currency: 'MXN'
    }).format(amount);
  };

  const getStockVariant = (stock) => {
    if (stock <= 0) return 'danger';
    if (stock <= 5) return 'warning';
    return 'success';
  };

  return (
    <Card className="h-100">
      <Card.Header className="d-flex justify-content-between align-items-center">
        <strong>{product.hawa}</strong>
        <Badge 
          variant={product.available ? 'success' : 'secondary'}
        >
          {product.available ? 'Disponible' : 'No disponible'}
        </Badge>
      </Card.Header>
      <Card.Body>
        <Card.Title>{product.name}</Card.Title>
        <Card.Text>{product.description}</Card.Text>
        
        <div className="mb-2">
          <strong>Precio:</strong> {formatCurrency(product.listPrice)}
          {product.discount > 0 && (
            <>
              <br />
              <span className="text-muted">
                Descuento: {product.discount}%
              </span>
              <br />
              <strong className="text-success">
                Precio final: {formatCurrency(product.finalPrice)}
              </strong>
            </>
          )}
        </div>

        <div className="mb-3">
          <Badge variant={getStockVariant(product.stock)}>
            Stock: {product.stock} unidades
          </Badge>
        </div>

        {availability && (
          <Alert 
            variant={availability.available ? 'success' : 'warning'}
            className="py-2"
          >
            {availability.message}
            {availability.available && (
              <span> - Stock disponible: {availability.stock}</span>
            )}
          </Alert>
        )}
      </Card.Body>
      <Card.Footer>
        <Button
          variant="outline-primary"
          size="sm"
          onClick={() => onCheckAvailability(product.hawa)}
          className="w-100"
        >
          Verificar Disponibilidad
        </Button>
      </Card.Footer>
    </Card>
  );
};

const ProductList = () => {
  const dispatch = useDispatch();
  const { 
    products, 
    availabilityCheck, 
    loading, 
    error 
  } = useSelector(state => state.products);

  const [searchTerm, setSearchTerm] = useState('');
  const [filteredProducts, setFilteredProducts] = useState([]);

  useEffect(() => {
    dispatch(fetchProducts());
  }, [dispatch]);

  useEffect(() => {
    const filtered = products.filter(product =>
      product.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
      product.hawa.toLowerCase().includes(searchTerm.toLowerCase()) ||
      product.description.toLowerCase().includes(searchTerm.toLowerCase())
    );
    setFilteredProducts(filtered);
  }, [products, searchTerm]);

  const handleCheckAvailability = async (hawa) => {
    try {
      await dispatch(checkProductAvailability(hawa)).unwrap();
    } catch (error) {
      console.error('Error checking availability:', error);
    }
  };

  if (loading) {
    return <LoadingSpinner text="Cargando productos..." />;
  }

  return (
    <>
      <Card className="mb-4">
        <Card.Header className="d-flex justify-content-between align-items-center">
          <h4>Catálogo de Productos</h4>
          <Button 
            variant="outline-primary" 
            onClick={() => dispatch(fetchProducts())}
          >
            Actualizar
          </Button>
        </Card.Header>
        <Card.Body>
          {error && (
            <Alert variant="danger">
              {error}
            </Alert>
          )}

          {/* Search */}
          <Form.Group className="mb-4">
            <InputGroup>
              <InputGroup.Text>🔍</InputGroup.Text>
              <Form.Control
                type="text"
                placeholder="Buscar por nombre, HAWA o descripción..."
                value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)}
              />
            </InputGroup>
          </Form.Group>

          {filteredProducts.length === 0 ? (
            <Alert variant="info">
              {searchTerm ? 'No se encontraron productos que coincidan con la búsqueda.' : 'No hay productos disponibles.'}
            </Alert>
          ) : (
            <>
              <div className="mb-3">
                <small className="text-muted">
                  Mostrando {filteredProducts.length} de {products.length} productos
                </small>
              </div>
              
              <Row>
                {filteredProducts.map(product => (
                  <Col key={product.id} md={6} lg={4} className="mb-4">
                    <ProductCard
                      product={product}
                      onCheckAvailability={handleCheckAvailability}
                      availability={availabilityCheck[product.hawa]}
                    />
                  </Col>
                ))}
              </Row>
            </>
          )}
        </Card.Body>
      </Card>
    </>
  );
};

export default ProductList;