CREATE TABLE roles (
    role_id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE users (
    user_id BIGSERIAL PRIMARY KEY,
    full_name VARCHAR(150) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role_id BIGINT NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_users_role FOREIGN KEY (role_id) REFERENCES roles (role_id)
);

CREATE TABLE products (
    product_id BIGSERIAL PRIMARY KEY,
    sku VARCHAR(100) NOT NULL UNIQUE,
    name VARCHAR(150) NOT NULL,
    description TEXT,
    tags VARCHAR(255),
    unit_price NUMERIC(12,2),
    current_stock INTEGER NOT NULL DEFAULT 0,
    reorder_level INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT chk_products_current_stock_non_negative CHECK (current_stock >= 0),
    CONSTRAINT chk_products_reorder_level_non_negative CHECK (reorder_level >= 0)
);

CREATE TABLE suppliers (
    supplier_id BIGSERIAL PRIMARY KEY,
    supplier_name VARCHAR(150) NOT NULL,
    contact_person VARCHAR(150),
    phone VARCHAR(50),
    email VARCHAR(150),
    address TEXT,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE TABLE customers (
    customer_id BIGSERIAL PRIMARY KEY,
    customer_name VARCHAR(150) NOT NULL,
    contact_person VARCHAR(150),
    phone VARCHAR(50),
    email VARCHAR(150),
    address TEXT,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE TABLE inbound_transactions (
    inbound_id BIGSERIAL PRIMARY KEY,
    product_id BIGINT NOT NULL,
    supplier_id BIGINT NOT NULL,
    quantity INTEGER NOT NULL,
    received_date TIMESTAMP NOT NULL,
    reference_no VARCHAR(100),
    remarks TEXT,
    created_by BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_inbound_product FOREIGN KEY (product_id) REFERENCES products (product_id),
    CONSTRAINT fk_inbound_supplier FOREIGN KEY (supplier_id) REFERENCES suppliers (supplier_id),
    CONSTRAINT fk_inbound_created_by FOREIGN KEY (created_by) REFERENCES users (user_id),
    CONSTRAINT chk_inbound_quantity_positive CHECK (quantity > 0)
);

CREATE TABLE outbound_transactions (
    outbound_id BIGSERIAL PRIMARY KEY,
    product_id BIGINT NOT NULL,
    customer_id BIGINT NOT NULL,
    quantity INTEGER NOT NULL,
    shipped_date TIMESTAMP NOT NULL,
    reference_no VARCHAR(100),
    remarks TEXT,
    created_by BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_outbound_product FOREIGN KEY (product_id) REFERENCES products (product_id),
    CONSTRAINT fk_outbound_customer FOREIGN KEY (customer_id) REFERENCES customers (customer_id),
    CONSTRAINT fk_outbound_created_by FOREIGN KEY (created_by) REFERENCES users (user_id),
    CONSTRAINT chk_outbound_quantity_positive CHECK (quantity > 0)
);

CREATE INDEX idx_users_role_id ON users (role_id);
CREATE INDEX idx_products_sku ON products (sku);
CREATE INDEX idx_suppliers_name ON suppliers (supplier_name);
CREATE INDEX idx_customers_name ON customers (customer_name);
CREATE INDEX idx_inbound_product_id ON inbound_transactions (product_id);
CREATE INDEX idx_inbound_supplier_id ON inbound_transactions (supplier_id);
CREATE INDEX idx_outbound_product_id ON outbound_transactions (product_id);
CREATE INDEX idx_outbound_customer_id ON outbound_transactions (customer_id);
