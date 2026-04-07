-- Usamos tu base de datos
CREATE DATABASE IF NOT EXISTS wishdish;
USE wishdish;

-- 1. Categorías
CREATE TABLE categories (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT
);

-- 2. Productos
CREATE TABLE products (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    price DECIMAL(10, 2) NOT NULL,
    picture VARCHAR(255) NOT NULL,
    available BOOLEAN NOT NULL DEFAULT TRUE,
    category_id INT,
    FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE SET NULL
);

-- 3. Mesas
CREATE TABLE dining_tables (
    id INT AUTO_INCREMENT PRIMARY KEY,
    table_number INT NOT NULL UNIQUE
);

-- 4. Comandas (Cabecera)
CREATE TABLE orders (
    id INT AUTO_INCREMENT PRIMARY KEY,
    table_id INT NOT NULL,
    order_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    status ENUM('in_kitchen', 'served', 'paid') DEFAULT 'in_kitchen',
    general_notes TEXT, -- Aquí va un comentario del cliente sobre la comanda (Necesito 2 servilletas, nos falta un cenicero, etc...)
    FOREIGN KEY (table_id) REFERENCES dining_tables(id)
);

-- 5. Líneas de Comanda (Platos dentro de la comanda)
CREATE TABLE order_items (
    id INT AUTO_INCREMENT PRIMARY KEY,
    order_id INT NOT NULL,
    product_id INT NOT NULL,
    quantity INT DEFAULT 1,
    status ENUM('in_kitchen', 'prepared') DEFAULT 'in_kitchen',
    item_notes TEXT, -- Aquí van los "Sin pepinillo"
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(id)
);