import React from 'react';
import { Spinner } from 'react-bootstrap';

const LoadingSpinner = ({ 
  size = 'md', 
  text = 'Cargando...', 
  center = true, 
  className = '' 
}) => {
  const spinnerSize = size === 'sm' ? 'sm' : undefined;

  const content = (
    <div className={`d-flex align-items-center ${className}`}>
      <Spinner
        animation="border"
        role="status"
        size={spinnerSize}
        className="me-2"
      />
      <span>{text}</span>
    </div>
  );

  if (center) {
    return (
      <div className="d-flex justify-content-center align-items-center py-4">
        {content}
      </div>
    );
  }

  return content;
};

export default LoadingSpinner;