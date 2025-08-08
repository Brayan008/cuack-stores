import ProductList from '../components/products/ProductList';
import { Container } from 'react-bootstrap';

const ProductsPage = () => {
  return (
    <Container fluid>
      <ProductList />
    </Container>
  );
};

export default ProductsPage;