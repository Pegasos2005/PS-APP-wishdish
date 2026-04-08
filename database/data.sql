USE wishdish;

-- Limpieza de tablas (En orden inverso por las claves foráneas)
SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE order_items;
TRUNCATE TABLE orders;
TRUNCATE TABLE products;
TRUNCATE TABLE categories;
TRUNCATE TABLE dining_tables;
SET FOREIGN_KEY_CHECKS = 1;

/*
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
*/

/* PRUEBA PARA VER SI SALE LA INFO*/
-- 1. INSERTAR CATEGORÍAS (Modelo Category.java)
INSERT INTO categories (id, name, description) VALUES
                                                   (1, 'Entrantes', 'Para empezar con buen pie'),
                                                   (2, 'Hamburguesas', 'Nuestra especialidad'),
                                                   (3, 'Guarniciones', 'El acompañamiento perfecto'),
                                                   (4, 'Postres', 'El toque dulce'),
                                                   (5, 'Bebidas', 'Refrescos y aguas');

-- 2. INSERTAR MESAS (Modelo DiningTable.java)
-- Importante para que el botón "Send Order" no dé error al buscar la mesa 1
INSERT INTO dining_tables (id, table_number) VALUES
                                                 (1, 1), (2, 2), (3, 3), (4, 4), (5, 5);

-- 3. INSERTAR PRODUCTOS (Modelo Product.java)
-- Usamos 'picture' porque así se llama la variable en tu clase Java
INSERT INTO products (id, name, description, price, picture, category_id, available) VALUES
-- Entrantes
(101, 'Nachos con Queso', 'Nachos crujientes con mezcla de quesos y guacamole.', 8.90, 'assets/nachos.jpg', 1, true),
(102, 'Tequeños', 'Palitos de queso crujientes con salsa.', 7.90, 'assets/tequeños.jpg', 1, true),
(103, 'Aros de cebolla 10 uds', 'Crujientes aros de cebolla fritos, acompañados de nuestra salsa Montana', 8.90, 'assets/aros_cebolla.jpg', 1, true),
-- Hamburguesas
(104, 'Hamburguesa smash con queso y bacon', '180 g. de carne 100% vaca gallega, sin aditivos ni conservantes', 13.50, 'assets/hamburguesa_smash_bacon.jpg', 2, true),
(105, 'Burger del Mes', 'Pan Black Brioche, 200Gr de Angus Americano, Pulled-Pork Casero...', 16.50, 'assets/Burger_del_mes.jpg', 2, true),
(106, 'Crispy chicken toro hot', 'Nuestra hamburguesa 100% de pollo crispy', 12.90, 'assets/crispy_chicken_toro_hot.jpg', 2, true),
(107, 'Hamburguesa smash', '180 g. de carne 100% vaca gallega, picada a diario', 13.50, 'assets/hamburguesa_smash.jpg', 2, true),
-- Guarniciones
(108, 'Batatas Cajun', 'Batatas fritas cubiertas de especias Cajun.', 4.90, 'assets/batatas_cajun.jpg', 3, true),
(109, 'Batata frita', 'Boniato frito.', 4.90, 'assets/batata_frita.jpg', 3, true),
(110, 'Papas cajún', 'Papas fritas sazonadas con un mix de especias.', 3.50, 'assets/papas_cajun.jpg', 3, true),
(111, 'Papas clásicas', 'Patatas fritas clásicas', 3.60, 'assets/papas_clasicas.jpg', 3, true),
-- Postres
(112, 'Cheesecake de Pistacho', 'Deliciosa Cheesecake Casera con Topping de Pistacho.', 6.50, 'assets/cheescake_pistacho.jpg', 4, true),
(113, 'Cheesecake Lotus', 'Tarta casera de queso y galletas Lotus.', 5.85, 'assets/cheescake_lotus.jpg', 4, true),
(114, 'Cheesecake clásica', 'Deliciosa tarta de queso casera', 6.50, 'assets/cheescake.jpg', 4, true),
(115, 'Brownie De Chocolate', 'Con helado de Vainilla y salsa de chocolate.', 5.50, 'assets/brownie.jpg', 4, true),
-- Bebidas
(116, 'Nestea Limón', 'Lata 330ml.', 2.50, 'assets/nestea_te_negro.jpg', 5, true),
(117, 'Appletiser', 'Botella de zumo de manzana con gas.', 2.90, 'assets/appletiser.jpg', 5, true),
(118, 'Agua Con Gas (1.5L)', 'Botella grande', 3.00, 'assets/botella_agua_1.50.jpg', 5, true),
(119, 'Agua Con Gas (50cl)', 'Botella pequeña', 1.50, 'assets/botella_agua_0.50.jpg', 5, true);



-- 4. INSERTAR COMANDA DE PRUEBA
INSERT INTO orders (table_id, status) VALUES (1, 'in_kitchen');

-- Le ponemos unos Nachos (ID 101) y una Hamburguesa Smash (ID 104) a esa comanda
INSERT INTO order_items (order_id, product_id, quantity, status) VALUES
                                                                     (LAST_INSERT_ID(), 101, 1, 'in_kitchen'),
                                                                     (LAST_INSERT_ID(), 104, 1, 'in_kitchen');