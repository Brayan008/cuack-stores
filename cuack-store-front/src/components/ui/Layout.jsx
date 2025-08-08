import React from 'react';
import { Navbar, Nav, Container } from 'react-bootstrap';
import { LinkContainer } from 'react-router-bootstrap';
import { useAuth0 } from '../../hooks/useAuth0';
import LoginButton from '../auth/LoginButton';
import LogoutButton from '../auth/LogoutButton';

const Layout = ({ children }) => {
  const { isAuthenticated, user } = useAuth0();

  return (
    <>
      <Navbar bg="dark" variant="dark" expand="lg" className="mb-4">
        <Container>
          <Navbar.Brand href="/">
            🦆 Cuack Stores - Cheyenes
          </Navbar.Brand>
          
          {isAuthenticated && (
            <>
              <Navbar.Toggle aria-controls="basic-navbar-nav" />
              <Navbar.Collapse id="basic-navbar-nav">
                <Nav className="me-auto">
                  <LinkContainer to="/">
                    <Nav.Link>Inicio</Nav.Link>
                  </LinkContainer>
                  <LinkContainer to="/products">
                    <Nav.Link>Productos</Nav.Link>
                  </LinkContainer>
                  <LinkContainer to="/create-order">
                    <Nav.Link>Crear Pedido</Nav.Link>
                  </LinkContainer>
                  <LinkContainer to="/orders">
                    <Nav.Link>Pedidos</Nav.Link>
                  </LinkContainer>
                </Nav>
                
                <Nav>
                  <Navbar.Text className="me-3">
                    Hola, {user?.name || user?.email}
                  </Navbar.Text>
                  <LogoutButton />
                </Nav>
              </Navbar.Collapse>
            </>
          )}
          
          {!isAuthenticated && (
            <Nav>
              <LoginButton />
            </Nav>
          )}
        </Container>
      </Navbar>

      <Container>
        {children}
      </Container>
    </>
  );
};

export default Layout;