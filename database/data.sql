USE wishdish;

-- Insertar Categorías
INSERT INTO categories (name) VALUES ('Entrantes'), ('Hamburguesas'), ('Bebidas');

-- Insertar Mesas
INSERT INTO dining_tables (table_number) VALUES (1), (2), (3), (4), (5);

-- Insertar Productos
INSERT INTO products (name, description, price, category_id) VALUES
('Nachos con Queso', 'Nachos crujientes con mezcla de quesos y guacamole', 8.50, 1),
('Hamburguesa Clásica', 'Doble carne, queso cheddar, lechuga y tomate', 12.00, 2),
('Hamburguesa Trufada', 'Carne de vaca madurada, mayonesa de trufa y setas', 14.50, 2),
('Coca-Cola', 'Lata 33cl', 2.50, 3),
('Agua Mineral', 'Botella 50cl', 1.50, 3);

-- Insertar una comanda de prueba (Para la Mesa 1, pidiendo servilletas)
INSERT INTO orders (table_id, status, general_notes) VALUES
(1, 'pending', 'Por favor, traer 2 servilletas extra y un cenicero');

-- Insertar los platos de esa comanda (La hamburguesa clásica va sin pepinillo)
-- Nota: LAST_INSERT_ID() coge el ID de la comanda que acabamos de crear arriba.
INSERT INTO order_items (order_id, product_id, quantity, item_notes) VALUES
(LAST_INSERT_ID(), 2, 1, 'Sin pepinillos, por favor'),
(LAST_INSERT_ID(), 4, 2, NULL);