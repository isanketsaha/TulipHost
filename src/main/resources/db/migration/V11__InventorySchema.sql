ALTER TABLE inventory
    CHANGE unit_price purchase_price DOUBLE;

ALTER TABLE inventory
    ADD COLUMN mrp DOUBLE NOT NULL AFTER purchase_price;
