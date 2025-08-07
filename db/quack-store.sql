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
    ('HAWA003', 'Camioneta RAM 1500', 'Camioneta pickup RAM 1500 2024, cabina crew cab', 920000.00, 7.00, 12, 1, 'system'),
    ('HAWA004', 'Camioneta Toyota Tacoma', 'Camioneta pickup Toyota Tacoma 2024, 4x4', 650000.00, 2.00, 20, 1, 'system'),
    ('HAWA005', 'Camioneta Nissan Frontier', 'Camioneta pickup Nissan Frontier 2024, cabina doble', 580000.00, 4.00, 6, 1, 'system'),
    ('HAWA006', 'Camioneta GMC Sierra', 'Camioneta pickup GMC Sierra 2024, AT4', 890000.00, 6.00, 0, 0, 'system');

    PRINT 'Datos de prueba insertados exitosamente';
END
ELSE
    PRINT 'Los datos de prueba ya existen';
GO