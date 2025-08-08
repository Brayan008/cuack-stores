import { useState, useEffect } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import jwtDecode from 'jwt-decode';
import { AUTH0_CONFIG } from '../utils/constants';
import { setUser, setToken, logout as logoutAction } from '../store/authSlice';

export const useAuth0 = () => {
  const dispatch = useDispatch();
  const { user, token, isAuthenticated } = useSelector(state => state.auth);
  const [isLoading, setIsLoading] = useState(false);

  useEffect(() => {
    const fragment = window.location.hash.substring(1);
    const params = new URLSearchParams(fragment);
    const idToken = params.get('id_token');

    if (idToken) {
      handleAuthCallback(idToken);
    }
  }, []);

  const buildAuthUrl = () => {
    const params = new URLSearchParams({
      client_id: AUTH0_CONFIG.clientId,
      redirect_uri: AUTH0_CONFIG.redirectUri,
      response_type: 'id_token',
      scope: AUTH0_CONFIG.scopes.join(' '),
      nonce: Math.random().toString(36).substring(2, 15),
      prompt: 'login'
    });

    return `${AUTH0_CONFIG.authorizationEndpoint}?${params.toString()}`;
  };

  const authenticate = async () => {
    setIsLoading(true);
    try {
      window.location.href = buildAuthUrl();
    } catch (error) {
      setIsLoading(false);
      throw new Error('Error durante la autenticación: ' + error.message);
    }
  };

  const handleAuthCallback = (idToken) => {
    try {
      const decoded = jwtDecode(idToken);

      const currentTime = Date.now() / 1000;
      if (decoded.exp < currentTime) {
        throw new Error('Token expirado');
      }

      dispatch(setUser({
        id: decoded.sub,
        email: decoded.email,
        name: decoded.name || decoded.email,
        picture: decoded.picture
      }));

      dispatch(setToken(idToken));

      window.history.replaceState({}, document.title, window.location.pathname);

    } catch (error) {
      console.error('Error procesando callback:', error);
      logout();
    } finally {
      setIsLoading(false);
    }
  };

  const logout = () => {
    dispatch(logoutAction());
    const logoutUrl = `${AUTH0_CONFIG.domain}/v2/logout?client_id=${AUTH0_CONFIG.clientId}&returnTo=${encodeURIComponent(window.location.origin)}`;
    window.location.href = logoutUrl;
  };

  return {
    user,
    token,
    isAuthenticated,
    isLoading,
    authenticate,
    logout
  };
};