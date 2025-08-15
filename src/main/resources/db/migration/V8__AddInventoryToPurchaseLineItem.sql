-- Add inventory_id column to purchase_line_item table for direct inventory tracking
ALTER TABLE purchase_line_item 
ADD COLUMN inventory_id bigint DEFAULT NULL,
ADD CONSTRAINT fk_purchase_line_item_inventory 
FOREIGN KEY (inventory_id) REFERENCES inventory (id);

-- Add active field to inventory table for basic filtering
ALTER TABLE inventory 
ADD COLUMN active bit(1) DEFAULT 1 NOT NULL;
