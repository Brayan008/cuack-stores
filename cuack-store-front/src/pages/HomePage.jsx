
import { Container, Row, Col, Card, Button } from 'react-bootstrap';
import { LinkContainer } from 'react-router-bootstrap';
import { useAuth0 } from '../hooks/useAuth0';

const HomePage = () => {
  const { isAuthenticated, user } = useAuth0();

  if (!isAuthenticated) {
    return (
      <Container className="mt-5">
        <Row className="justify-content-center">
          <Col md={8} lg={6}>
            <Card className="text-center">
              <Card.Body className="py-5">
                <div className="mb-4">
                  <h1 className="display-4">🦆</h1>
                  <h2>Tiendas Patito</h2>
                  <p className="lead text-muted">
                    Sistema de Gestión de Pedidos de Camionetas
                  </p>
                </div>
                
                <Card className="mb-4">
                  <Card.Body>
                    <h5>Bienvenido al Sistema</h5>
                    <p className="mb-0">
                      Inicie sesión para acceder al sistema de gestión de inventario y pedidos.
                    </p>
                  </Card.Body>
                </Card>

                <div className="d-grid gap-2">
                  <p className="text-muted">
                    Utilice sus credenciales para acceder al sistema
                  </p>
                </div>
              </Card.Body>
            </Card>
          </Col>
        </Row>
      </Container>
    );
  }

  return (
    <Container>
      <Row>
        <Col>
          <div className="text-center mb-5">
            <h1>🦆 Bienvenido, {user?.name || user?.email}</h1>
            <p className="lead">Sistema de Gestión de Pedidos - Tiendas Patito</p>
          </div>
        </Col>
      </Row>

      <Row>
        <Col md={6} className="mb-4">
          <Card className="h-100">
            <Card.Body className="text-center">
              <div className="mb-3">
                <h2>📦</h2>
              </div>
              <Card.Title>Gestión de Productos</Card.Title>
              <Card.Text>
                Consulte el catálogo de camionetas disponibles, verifique stock y disponibilidad.
              </Card.Text>
              <LinkContainer to="/products">
                <Button variant="primary">Ver Productos</Button>
              </LinkContainer>
            </Card.Body>
          </Card>
        </Col>

        <Col md={6} className="mb-4">
          <Card className="h-100">
            <Card.Body className="text-center">
              <div className="mb-3">
                <h2>🛒</h2>
              </div>
              <Card.Title>Crear Pedido</Card.Title>
              <Card.Text>
                Registre un nuevo pedido de camionetas con validación de inventario automática.
              </Card.Text>
              <LinkContainer to="/create-order">
                <Button variant="success">Crear Pedido</Button>
              </LinkContainer>
            </Card.Body>
          </Card>
        </Col>
      </Row>

      <Row>
        <Col md={12} className="mb-4">
          <Card className="h-100">
            <Card.Body className="text-center">
              <div className="mb-3">
                <h2>📋</h2>
              </div>
              <Card.Title>Lista de Pedidos</Card.Title>
              <Card.Text>
                Consulte y gestione todos los pedidos. Cambie el estatus de pendiente a entregado o cancelado.
              </Card.Text>
              <LinkContainer to="/orders">
                <Button variant="info">Ver Pedidos</Button>
              </LinkContainer>
            </Card.Body>
          </Card>
        </Col>
      </Row>
    </Container>
  );
};

export default HomePage;