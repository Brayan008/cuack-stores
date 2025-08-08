import React, { useState, useEffect } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { Formik, Form, Field, FieldArray } from 'formik';
import * as Yup from 'yup';
import { 
  Row, 
  Col, 
  Card, 
  Button, 
  Alert, 
  InputGroup,
  FormControl,
  FormGroup,
  FormLabel
} from 'react-bootstrap';
import { createOrder, clearCreateOrderSuccess } from '../../store/ordersSlice';
import { fetchProducts, checkProductAvailability } from '../../store/productsSlice';
import LoadingSpinner from '../ui/LoadingSpinner';

// Validation schema
const orderValidationSchema = Yup.object({
  storeId: Yup.string().required('ID de tienda es requerido'),
  sellerName: Yup.string().required('Nombre del vendedor es requerido'),
  customer: Yup.object({
    name: Yup.string().required('Nombre del cliente es requerido'),
    email: Yup.string().email('Email inválido').required('Email es requerido'),
    phone: Yup.string(),
    address: Yup.string(),
    document: Yup.string(),
    documentType: Yup.string()
  }),
  items: Yup.array().of(
    Yup.object({
      productHawa: Yup.string().required('HAWA del producto es requerido'),
      quantity: Yup.number().min(1, 'Cantidad debe ser mayor a 0').required('Cantidad es requerida')
    })
  ).min(1, 'Debe agregar al menos un producto')
});

const OrderForm = () => {
  const dispatch = useDispatch();
  const { 
    createOrderLoading, 
    createOrderSuccess, 
    error: orderError 
  } = useSelector(state => state.orders);
  
  const { 
    products, 
    availabilityCheck, 
    loading: productsLoading 
  } = useSelector(state => state.products);

  const [availabilityErrors, setAvailabilityErrors] = useState({});

  useEffect(() => {
    dispatch(fetchProducts());
  }, [dispatch]);

  useEffect(() => {
    if (createOrderSuccess) {
      alert('¡Pedido creado exitosamente!');
      dispatch(clearCreateOrderSuccess());
    }
  }, [createOrderSuccess, dispatch]);

  const initialValues = {
    storeId: 'STORE-001',
    sellerName: '',
    customer: {
      name: '',
      email: '',
      phone: '',
      address: '',
      document: '',
      documentType: 'DNI'
    },
    items: [
      {
        productHawa: '',
        quantity: 1
      }
    ],
    comments: ''
  };

  const handleSubmit = async (values, { setSubmitting, resetForm }) => {
    try {
      // Check availability for all items
      for (const item of values.items) {
        await dispatch(checkProductAvailability(item.productHawa)).unwrap();
      }

      // Create order
      await dispatch(createOrder(values)).unwrap();
      resetForm();
    } catch (error) {
      console.error('Error creating order:', error);
    } finally {
      setSubmitting(false);
    }
  };

  const checkItemAvailability = async (hawa) => {
    if (hawa) {
      try {
        const result = await dispatch(checkProductAvailability(hawa)).unwrap();
        
        if (!result.available) {
          setAvailabilityErrors(prev => ({
            ...prev,
            [hawa]: result.message
          }));
        } else {
          setAvailabilityErrors(prev => {
            const newErrors = { ...prev };
            delete newErrors[hawa];
            return newErrors;
          });
        }
      } catch (error) {
        setAvailabilityErrors(prev => ({
          ...prev,
          [hawa]: 'Error verificando disponibilidad'
        }));
      }
    }
  };

  if (productsLoading) {
    return <LoadingSpinner text="Cargando productos..." />;
  }

  return (
    <Card>
      <Card.Header>
        <h4>Crear Nuevo Pedido</h4>
      </Card.Header>
      <Card.Body>
        {orderError && (
          <Alert variant="danger">
            {orderError}
          </Alert>
        )}

        <Formik
          initialValues={initialValues}
          validationSchema={orderValidationSchema}
          onSubmit={handleSubmit}
        >
          {({ values, errors, touched, setFieldValue, isSubmitting }) => (
            <Form>
              {/* Store and Seller Info */}
              <Row>
                <Col md={6}>
                  <FormGroup className="mb-3">
                    <FormLabel>ID de Tienda</FormLabel>
                    <Field
                      name="storeId"
                      as={FormControl}
                      isInvalid={touched.storeId && errors.storeId}
                    />
                    {touched.storeId && errors.storeId && (
                      <div className="invalid-feedback d-block">{errors.storeId}</div>
                    )}
                  </FormGroup>
                </Col>
                <Col md={6}>
                  <FormGroup className="mb-3">
                    <FormLabel>Nombre del Vendedor</FormLabel>
                    <Field
                      name="sellerName"
                      as={FormControl}
                      isInvalid={touched.sellerName && errors.sellerName}
                    />
                    {touched.sellerName && errors.sellerName && (
                      <div className="invalid-feedback d-block">{errors.sellerName}</div>
                    )}
                  </FormGroup>
                </Col>
              </Row>

              {/* Customer Info */}
              <Card className="mb-4">
                <Card.Header>Datos del Cliente</Card.Header>
                <Card.Body>
                  <Row>
                    <Col md={6}>
                      <FormGroup className="mb-3">
                        <FormLabel>Nombre del Cliente</FormLabel>
                        <Field
                          name="customer.name"
                          as={FormControl}
                          isInvalid={touched.customer?.name && errors.customer?.name}
                        />
                        {touched.customer?.name && errors.customer?.name && (
                          <div className="invalid-feedback d-block">{errors.customer.name}</div>
                        )}
                      </FormGroup>
                    </Col>
                    <Col md={6}>
                      <FormGroup className="mb-3">
                        <FormLabel>Email</FormLabel>
                        <Field
                          name="customer.email"
                          type="email"
                          as={FormControl}
                          isInvalid={touched.customer?.email && errors.customer?.email}
                        />
                        {touched.customer?.email && errors.customer?.email && (
                          <div className="invalid-feedback d-block">{errors.customer.email}</div>
                        )}
                      </FormGroup>
                    </Col>
                  </Row>
                  <Row>
                    <Col md={6}>
                      <FormGroup className="mb-3">
                        <FormLabel>Teléfono</FormLabel>
                        <Field
                          name="customer.phone"
                          as={FormControl}
                        />
                      </FormGroup>
                    </Col>
                    <Col md={6}>
                      <FormGroup className="mb-3">
                        <FormLabel>Dirección</FormLabel>
                        <Field
                          name="customer.address"
                          as={FormControl}
                        />
                      </FormGroup>
                    </Col>
                  </Row>
                </Card.Body>
              </Card>

              {/* Order Items */}
              <Card className="mb-4">
                <Card.Header>Productos del Pedido</Card.Header>
                <Card.Body>
                  <FieldArray name="items">
                    {({ push, remove }) => (
                      <>
                        {values.items.map((item, index) => (
                          <Row key={index} className="mb-3 align-items-end">
                            <Col md={6}>
                              <FormGroup>
                                <FormLabel>HAWA del Producto</FormLabel>
                                <Field
                                  name={`items.${index}.productHawa`}
                                  as="select"
                                  className="form-control"
                                  onChange={(e) => {
                                    setFieldValue(`items.${index}.productHawa`, e.target.value);
                                    checkItemAvailability(e.target.value);
                                  }}
                                >
                                  <option value="">Seleccionar producto...</option>
                                  {products.map(product => (
                                    <option key={product.hawa} value={product.hawa}>
                                      {product.hawa} - {product.name}
                                    </option>
                                  ))}
                                </Field>
                                {availabilityErrors[item.productHawa] && (
                                  <Alert variant="warning" className="mt-2">
                                    {availabilityErrors[item.productHawa]}
                                  </Alert>
                                )}
                              </FormGroup>
                            </Col>
                            <Col md={4}>
                              <FormGroup>
                                <FormLabel>Cantidad</FormLabel>
                                <Field
                                  name={`items.${index}.quantity`}
                                  type="number"
                                  min="1"
                                  as={FormControl}
                                />
                              </FormGroup>
                            </Col>
                            <Col md={2}>
                              <Button
                                variant="danger"
                                size="sm"
                                onClick={() => remove(index)}
                                disabled={values.items.length === 1}
                              >
                                Eliminar
                              </Button>
                            </Col>
                          </Row>
                        ))}
                        
                        <Button
                          variant="secondary"
                          onClick={() => push({ productHawa: '', quantity: 1 })}
                        >
                          Agregar Producto
                        </Button>
                      </>
                    )}
                  </FieldArray>
                </Card.Body>
              </Card>

              <FormGroup className="mb-4">
                <FormLabel>Comentarios (opcional)</FormLabel>
                <Field
                  name="comments"
                  as="textarea"
                  rows={3}
                  className="form-control"
                />
              </FormGroup>

              <div className="d-grid">
                <Button
                  type="submit"
                  variant="primary"
                  size="lg"
                  disabled={isSubmitting || createOrderLoading}
                >
                  {createOrderLoading ? (
                    <>
                      <LoadingSpinner size="sm" text="Creando pedido..." center={false} />
                    </>
                  ) : (
                    'Crear Pedido'
                  )}
                </Button>
              </div>
            </Form>
          )}
        </Formik>
      </Card.Body>
    </Card>
  );
};

export default OrderForm;