import { Button } from 'react-bootstrap';
import { useAuth0 } from '../../hooks/useAuth0';

const LoginButton = () => {
  const { authenticate, isLoading } = useAuth0();

  const handleLogin = async () => {
    try {
      await authenticate();
    } catch (error) {
      console.error('Error during login:', error);
      alert('Error durante el login: ' + error.message);
    }
  };

  return (
    <Button 
      variant="primary" 
      onClick={handleLogin}
      disabled={isLoading}
    >
      {isLoading ? 'Iniciando sesión...' : 'Iniciar Sesión'}
    </Button>
  );
};

export default LoginButton;