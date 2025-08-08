import { Button } from 'react-bootstrap';
import { useAuth0 } from '../../hooks/useAuth0';

const LogoutButton = () => {
  const { logout } = useAuth0();

  const handleLogout = () => {
    logout();
  };

  return (
    <Button 
      variant="outline-light" 
      onClick={handleLogout}
      size="sm"
    >
      Cerrar Sesión
    </Button>
  );
};

export default LogoutButton;