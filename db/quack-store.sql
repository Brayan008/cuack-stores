USE quack_store;
GO

IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='products' AND xtype='U')
BEGIN
    CREATE TABLE products (
        id BIGINT IDENTITY(1,1) PRIMARY KEY,
        hawa NVARCHAR(50) NOT NULL UNIQUE,
        name NVARCHAR(200) NOT NULL,
        description NVARCHAR(500),
        list_price DECIMAL(10,2) NOT NULL CHECK (list_price > 0),
        discount DECIMAL(5,2) DEFAULT 0.00 CHECK (discount >= 0),
        stock INT NOT NULL DEFAULT 0 CHECK (stock >= 0),
        available BIT DEFAULT 1,
        created_at DATETIME2 DEFAULT GETDATE(),
        updated_at DATETIME2,
        created_by NVARCHAR(100),

        INDEX IX_products_hawa (hawa),
        INDEX IX_products_available (available),
        INDEX IX_products_stock (stock),
        INDEX IX_products_created_at (created_at)
    );

    PRINT 'Tabla products creada exitosamente';
END
ELSE
    PRINT 'La tabla products ya existe';
GO

IF NOT EXISTS (SELECT 1 FROM products WHERE hawa = 'HAWA001')
BEGIN
    INSERT INTO products (hawa, name, description, list_price, discount, stock, available, created_by)
    VALUES
    ('HAWA001', 'Camioneta Ford F-150', 'Camioneta pickup Ford F-150 2024, cabina doble', 850000.00, 5.00, 15, 1, 'system'),
    ('HAWA002', 'Camioneta Chevrolet Silverado', 'Camioneta pickup Chevrolet Silverado 2024, cabina regular', 780000.00, 3.00, 8, 1, 'system'),
    ('HAWA003', 'Chevy', 'Chevy Monza 20019, humilde pero poderoso', 920000.00, 7.00, 12, 1, 'system'),
    ('HAWA004', 'Tsuru tuneado', 'Tsuru, tuneado con buen sonido, 4x4 8 cilindros, le gano a un bmw', 650000.00, 2.00, 20, 1, 'system'),
    ('HAWA005', 'Voch', 'Vocho 1998, con llantas 25', 580000.00, 4.00, 6, 1, 'system'),
    ('HAWA006', 'Camioneta GMC Sierra', 'Camioneta pickup GMC Sierra 2024, AT4', 890000.00, 6.00, 0, 0, 'system');

    PRINT 'Datos de prueba insertados exitosamente';
END
ELSE
    PRINT 'Los datos de prueba ya existen';
GO


-- Crear la tabla orders
IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='orders' AND xtype='U')
BEGIN
    CREATE TABLE orders (
        id BIGINT IDENTITY(1,1) PRIMARY KEY,
        
        -- Datos de auditoría
        created_at DATETIME2 DEFAULT GETDATE() NOT NULL,
        updated_at DATETIME2,
        user_ip NVARCHAR(45),
        
        -- Datos del pedido
        store_id NVARCHAR(50) NOT NULL,
        seller_name NVARCHAR(200) NOT NULL,
        status NVARCHAR(20) DEFAULT 'PENDIENTE' CHECK (status IN ('PENDIENTE', 'ENTREGADO', 'CANCELADO')),
        comments NVARCHAR(1000),
        
        -- Datos del cliente (embedded)
        customer_name NVARCHAR(200) NOT NULL,
        customer_email NVARCHAR(200),
        customer_phone NVARCHAR(20),
        customer_address NVARCHAR(500),
        customer_document NVARCHAR(50),
        customer_document_type NVARCHAR(20),
        
        -- Totales del pedido
        subtotal DECIMAL(12,2) DEFAULT 0.00,
        total_discount DECIMAL(12,2) DEFAULT 0.00,
        total DECIMAL(12,2) DEFAULT 0.00,
        
        -- Índices
        INDEX IX_orders_created_at (created_at DESC),
        INDEX IX_orders_status (status),
        INDEX IX_orders_store_id (store_id),
        INDEX IX_orders_seller_name (seller_name),
        INDEX IX_orders_customer_name (customer_name),
        INDEX IX_orders_customer_email (customer_email),
        INDEX IX_orders_status_created_at (status, created_at DESC)
    );
    
    PRINT 'Tabla orders creada exitosamente';
END
ELSE
    PRINT 'La tabla orders ya existe';
GO

-- Crear la tabla order_items
IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='order_items' AND xtype='U')
BEGIN
    CREATE TABLE order_items (
        id BIGINT IDENTITY(1,1) PRIMARY KEY,
        order_id BIGINT NOT NULL,
        
        -- Datos del producto
        product_hawa NVARCHAR(50) NOT NULL,
        product_name NVARCHAR(200) NOT NULL,
        quantity INT NOT NULL CHECK (quantity > 0),
        unit_price DECIMAL(10,2) NOT NULL CHECK (unit_price >= 0),
        discount_percentage DECIMAL(5,2) DEFAULT 0.00 CHECK (discount_percentage >= 0),
        discount_amount DECIMAL(10,2) DEFAULT 0.00 CHECK (discount_amount >= 0),
        subtotal DECIMAL(12,2) DEFAULT 0.00 CHECK (subtotal >= 0),
        
        -- Foreign Key
        FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
        
        -- Índices
        INDEX IX_order_items_order_id (order_id),
        INDEX IX_order_items_product_hawa (product_hawa),
        INDEX IX_order_items_product_name (product_name)
    );
    
    PRINT 'Tabla order_items creada exitosamente';
END
ELSE
    PRINT 'La tabla order_items ya existe';
GO

-- Trigger para actualizar updated_at en orders
IF NOT EXISTS (SELECT * FROM sys.triggers WHERE name = 'TR_orders_update_timestamp')
BEGIN
    EXEC('
    CREATE TRIGGER TR_orders_update_timestamp
    ON orders
    AFTER UPDATE
    AS
    BEGIN
        SET NOCOUNT ON;
        UPDATE orders
        SET updated_at = GETDATE()
        FROM orders o
        INNER JOIN inserted i ON o.id = i.id;
    END');
    
    PRINT 'Trigger TR_orders_update_timestamp creado exitosamente';
END
ELSE
    PRINT 'El trigger TR_orders_update_timestamp ya existe';
GO

-- Insertar datos de prueba
IF NOT EXISTS (SELECT 1 FROM orders WHERE id = 1)
BEGIN
    -- Pedido 1: Pendiente (reciente)
    INSERT INTO orders (
        store_id, seller_name, status, customer_name, customer_email, 
        customer_phone, customer_address, customer_document, customer_document_type,
        comments, user_ip
    ) VALUES (
        'STORE001', 'Juan Pérez', 'PENDIENTE', 'María González', 'maria.gonzalez@email.com',
        '555-0101', 'Av. Principal 123, Querétaro', '12345678', 'RFC',
        'Cliente frecuente, solicita entrega rápida', '192.168.1.100'
    );
    
    -- Pedido 2: Entregado
    INSERT INTO orders (
        store_id, seller_name, status, customer_name, customer_email, 
        customer_phone, customer_address, customer_document, customer_document_type,
        created_at, updated_at, comments, user_ip
    ) VALUES (
        'STORE001', 'Ana López', 'ENTREGADO', 'Carlos Rodríguez', 'carlos.rodriguez@email.com',
        '555-0102', 'Calle Reforma 456, Querétaro', '87654321', 'RFC',
        DATEADD(DAY, -2, GETDATE()), DATEADD(DAY, -1, GETDATE()),
        'Pedido entregado sin problemas', '192.168.1.101'
    );
    
    -- Pedido 3: Cancelado (más de 10 minutos)
    INSERT INTO orders (
        store_id, seller_name, status, customer_name, customer_email, 
        customer_phone, customer_address, customer_document, customer_document_type,
        created_at, updated_at, comments, user_ip
    ) VALUES (
        'STORE002', 'Roberto Silva', 'CANCELADO', 'Laura Martínez', 'laura.martinez@email.com',
        '555-0103', 'Blvd. Constitución 789, Querétaro', '11223344', 'RFC',
        DATEADD(HOUR, -1, GETDATE()), DATEADD(MINUTE, -30, GETDATE()),
        'Cliente canceló por cambio de planes', '192.168.1.102'
    );
    
    -- Items para el Pedido 1
    INSERT INTO order_items (
        order_id, product_hawa, product_name, quantity, 
        unit_price, discount_percentage, discount_amount, subtotal
    ) VALUES 
    (1, 'HAWA001', 'Camioneta Ford F-150', 1, 850000.00, 5.00, 42500.00, 807500.00),
    (1, 'HAWA004', 'Camioneta Toyota Tacoma', 1, 650000.00, 2.00, 13000.00, 637000.00);
    
    -- Items para el Pedido 2  
    INSERT INTO order_items (
        order_id, product_hawa, product_name, quantity, 
        unit_price, discount_percentage, discount_amount, subtotal
    ) VALUES 
    (2, 'HAWA003', 'Camioneta RAM 1500', 2, 920000.00, 7.00, 128800.00, 1711200.00);
    
    -- Items para el Pedido 3
    INSERT INTO order_items (
        order_id, product_hawa, product_name, quantity, 
        unit_price, discount_percentage, discount_amount, subtotal
    ) VALUES 
    (3, 'HAWA002', 'Camioneta Chevrolet Silverado', 1, 780000.00, 3.00, 23400.00, 756600.00);
    
    PRINT 'Datos de prueba insertados exitosamente';
END
ELSE
    PRINT 'Los datos de prueba ya existen';
GO