-- Create the sales table with store_location and country
DROP TABLE IF EXISTS sales;

CREATE TABLE sales
(
    sale_id        SERIAL PRIMARY KEY,
    product_id     INT            NOT NULL,
    customer_id    INT            NOT NULL,
    sale_date      DATE           NOT NULL,
    sale_amount    NUMERIC(10, 2) NOT NULL,
    store_location VARCHAR(100)   NOT NULL,
    country        VARCHAR(100)   NOT NULL,
    processed      BOOLEAN        NULL
);

-- Boolean false as default
ALTER TABLE sales
    ALTER COLUMN processed
        SET DEFAULT FALSE;