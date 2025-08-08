import React, { useEffect, useState } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { 
  Table, 
  Badge, 
  Button, 
  Modal, 
  Form,
  Alert,
  Card,
  Pagination
} from 'react-bootstrap';
import { fetchOrders, updateOrderStatus } from '../../store/ordersSlice';
import { ORDER_STATUS, ORDER_STATUS_LABELS, ORDER_STATUS_VARIANTS } from '../../utils/constants';
import LoadingSpinner from '../ui/LoadingSpinner';

const OrderList = () => {
  const dispatch = useDispatch();
  const { 
    orders, 
    loading, 
    error, 
    totalPages, 
    currentPage 
  } = useSelector(state => state.orders);

  const [showStatusModal, setShowStatusModal] = useState(false);
  const [selectedOrder, setSelectedOrder] = useState(null);
  const [newStatus, setNewStatus] = useState('');
  const [statusReason, setStatusReason] = useState('');

  useEffect(() => {
    dispatch(fetchOrders({ page: 0, size: 20 }));
  }, [dispatch]);

  const handleStatusChange = (order) => {
    setSelectedOrder(order);
    setNewStatus('');
    setStatusReason('');
    setShowStatusModal(true);
  };

  const handleConfirmStatusChange = async () => {
    if (!selectedOrder || !newStatus) return;

    try {
      await dispatch(updateOrderStatus({
        orderId: selectedOrder.id,
        status: newStatus,
        reason: statusReason
      })).unwrap();

      setShowStatusModal(false);
      alert('Estatus actualizado exitosamente');
    } catch (error) {
      alert('Error actualizando estatus: ' + error);
    }
  };

  const handlePageChange = (page) => {
    dispatch(fetchOrders({ page, size: 20 }));
  };

  const formatDate = (dateString) => {
    return new Date(dateString).toLocaleString('es-ES');
  };

  const formatCurrency = (amount) => {
    return new Intl.NumberFormat('es-MX', {
      style: 'currency',
      currency: 'MXN'
    }).format(amount);
  };

  const canChangeStatus = (order) => {
    return order.status === ORDER_STATUS.PENDIENTE;
  };

  const getAvailableStatuses = (currentStatus) => {
    if (currentStatus === ORDER_STATUS.PENDIENTE) {
      return [ORDER_STATUS.ENTREGADO, ORDER_STATUS.CANCELADO];
    }
    return [];
  };

  if (loading) {
    return <LoadingSpinner text="Cargando pedidos..." />;
  }

  return (
    <>
      <Card>
        <Card.Header className="d-flex justify-content-between align-items-center">
          <h4>Lista de Pedidos</h4>
          <Button 
            variant="outline-primary" 
            onClick={() => dispatch(fetchOrders({ page: currentPage, size: 20 }))}
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

          {orders.length === 0 ? (
            <Alert variant="info">
              No hay pedidos para mostrar.
            </Alert>
          ) : (
            <>
              <Table responsive striped hover>
                <thead>
                  <tr>
                    <th>ID</th>
                    <th>Fecha</th>
                    <th>Cliente</th>
                    <th>Vendedor</th>
                    <th>Estatus</th>
                    <th>Total</th>
                    <th>Items</th>
                    <th>Acciones</th>
                  </tr>
                </thead>
                <tbody>
                  {orders.map(order => (
                    <tr key={order.id}>
                      <td>#{order.id}</td>
                      <td>{formatDate(order.createdAt)}</td>
                      <td>
                        <div>
                          <strong>{order.customerName}</strong>
                          <br />
                          <small className="text-muted">{order.customerEmail}</small>
                        </div>
                      </td>
                      <td>{order.sellerName}</td>
                      <td>
                        <Badge variant={ORDER_STATUS_VARIANTS[order.status]}>
                          {ORDER_STATUS_LABELS[order.status]}
                        </Badge>
                        {order.status === ORDER_STATUS.PENDIENTE && !order.canBeCancelled && (
                          <small className="d-block text-muted">
                            No se puede cancelar
                          </small>
                        )}
                      </td>
                      <td>{formatCurrency(order.total)}</td>
                      <td>
                        {order.totalItems} productos 
                        <br />
                        <small className="text-muted">
                          {order.totalQuantity} unidades
                        </small>
                      </td>
                      <td>
                        {canChangeStatus(order) && (
                          <Button
                            variant="outline-primary"
                            size="sm"
                            onClick={() => handleStatusChange(order)}
                          >
                            Cambiar Estatus
                          </Button>
                        )}
                      </td>
                    </tr>
                  ))}
                </tbody>
              </Table>

              {/* Pagination */}
              {totalPages > 1 && (
                <div className="d-flex justify-content-center mt-3">
                  <Pagination>
                    <Pagination.Prev 
                      disabled={currentPage === 0}
                      onClick={() => handlePageChange(currentPage - 1)}
                    />
                    
                    {[...Array(totalPages)].map((_, index) => (
                      <Pagination.Item
                        key={index}
                        active={index === currentPage}
                        onClick={() => handlePageChange(index)}
                      >
                        {index + 1}
                      </Pagination.Item>
                    ))}
                    
                    <Pagination.Next 
                      disabled={currentPage === totalPages - 1}
                      onClick={() => handlePageChange(currentPage + 1)}
                    />
                  </Pagination>
                </div>
              )}
            </>
          )}
        </Card.Body>
      </Card>

      {/* Status Change Modal */}
      <Modal show={showStatusModal} onHide={() => setShowStatusModal(false)}>
        <Modal.Header closeButton>
          <Modal.Title>Cambiar Estatus del Pedido</Modal.Title>
        </Modal.Header>
        <Modal.Body>
          {selectedOrder && (
            <>
              <p>
                <strong>Pedido:</strong> #{selectedOrder.id}
                <br />
                <strong>Cliente:</strong> {selectedOrder.customerName}
                <br />
                <strong>Estatus actual:</strong>{' '}
                <Badge variant={ORDER_STATUS_VARIANTS[selectedOrder.status]}>
                  {ORDER_STATUS_LABELS[selectedOrder.status]}
                </Badge>
              </p>

              <Form.Group className="mb-3">
                <Form.Label>Nuevo Estatus</Form.Label>
                <Form.Select
                  value={newStatus}
                  onChange={(e) => setNewStatus(e.target.value)}
                >
                  <option value="">Seleccionar estatus...</option>
                  {getAvailableStatuses(selectedOrder.status).map(status => (
                    <option key={status} value={status}>
                      {ORDER_STATUS_LABELS[status]}
                    </option>
                  ))}
                </Form.Select>
              </Form.Group>

              {newStatus === ORDER_STATUS.CANCELADO && !selectedOrder.canBeCancelled && (
                <Alert variant="warning">
                  Este pedido no se puede cancelar porque han pasado más de 10 minutos desde su creación.
                </Alert>
              )}

              <Form.Group className="mb-3">
                <Form.Label>Razón del cambio (opcional)</Form.Label>
                <Form.Control
                  as="textarea"
                  rows={3}
                  value={statusReason}
                  onChange={(e) => setStatusReason(e.target.value)}
                  placeholder="Escriba la razón del cambio de estatus..."
                />
              </Form.Group>
            </>
          )}
        </Modal.Body>
        <Modal.Footer>
          <Button variant="secondary" onClick={() => setShowStatusModal(false)}>
            Cancelar
          </Button>
          <Button
            variant="primary"
            onClick={handleConfirmStatusChange}
            disabled={
              !newStatus || 
              (newStatus === ORDER_STATUS.CANCELADO && !selectedOrder?.canBeCancelled)
            }
          >
            Confirmar Cambio
          </Button>
        </Modal.Footer>
      </Modal>
    </>
  );
};

export default OrderList;