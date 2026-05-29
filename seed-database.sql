-- seed-database.sql
--
-- Full reset-and-seed script for the Zerroo database.
-- Run this against the development PostgreSQL database after backing up any data you want to keep.

BEGIN;

DO $$
DECLARE
    truncate_sql text;
BEGIN
    SELECT 'TRUNCATE TABLE ' || string_agg(format('%I.%I', schemaname, tablename), ', ')
           || ' CASCADE'
    INTO truncate_sql
    FROM pg_tables
    WHERE schemaname = 'public';

    IF truncate_sql IS NOT NULL THEN
        EXECUTE truncate_sql;
    END IF;
END $$;

INSERT INTO user_types (id, type) VALUES
    (1, 'Distributor'),
    (2, 'Organisation User');

INSERT INTO roles (id, role) VALUES
    (1, 'System User'),
    (2, 'Admin'),
    (3, 'Employee'),
    (4, 'Distributor');

INSERT INTO ranks (id, rank, "index", min_income) VALUES
    (1, 'Influencer', 1, 1000),
    (2, 'Pro Influencer', 2, 5000),
    (3, 'Bronze Executive', 3, 10000),
    (4, 'Star Executive', 4, 25000),
    (5, 'Pearl Executive', 5, 50000),
    (6, 'Business Development Executive', 6, 100000),
    (7, 'Senior Business Development Executive', 7, 300000),
    (8, 'Chief Marketing Officer', 8, 500000),
    (9, 'Business Administrator', 9, 1000000),
    (10, 'Diplomat', 10, 1500000),
    (11, 'Silver Diplomat', 11, 2500000),
    (12, 'Gold Diplomat', 12, 5000000),
    (13, 'Platinum Ambassador', 13, 10000000),
    (14, 'Diamond Ambassador', 14, 20000000),
    (15, 'Brand Ambassador', 15, 30000000);

INSERT INTO otp_types (id, type) VALUES
    (1, 'SMS'),
    (2, 'Email');

INSERT INTO bank_verification_statuses (id, status) VALUES
    (1, 'Pending'),
    (2, 'Verified'),
    (3, 'Rejected');

INSERT INTO cutoff_statuses (id, status) VALUES
    (1, 'Pending'),
    (2, 'Initiated');

INSERT INTO forward_shipment_statuses (id, status) VALUES
    (1, 'Pending'),
    (2, 'Processing'),
    (3, 'Picked Up'),
    (4, 'Dispatched'),
    (5, 'Delivered'),
    (6, 'RTO Pending'),
    (7, 'RTO Returned'),
    (8, 'Lost'),
    (9, 'Exception'),
    (10, 'Error');

INSERT INTO income_wallet_transaction_types (id, type) VALUES
    (1, 'Pair Match Income'),
    (2, 'Company'),
    (3, 'Payout'),
    (4, 'Opening Balance'),
    (5, 'Sp Income');

INSERT INTO kyc_verification_statuses (id, status) VALUES
    (1, 'Pending'),
    (2, 'Verified'),
    (3, 'Rejected');

INSERT INTO payout_statuses (id, status) VALUES
    (1, 'Pending'),
    (2, 'Approved');

INSERT INTO purchase_wallet_request_statuses (id, status) VALUES
    (1, 'Pending'),
    (2, 'Approved'),
    (3, 'Rejected');

INSERT INTO purchase_wallet_transaction_types (id, type) VALUES
    (1, 'Purchase'),
    (2, 'Wallet Request'),
    (3, 'From Company'),
    (4, 'To Company'),
    (5, 'Company'),
    (6, 'Opening Balance');

INSERT INTO sale_order_item_statuses (id, status) VALUES
    (1, 'Un Shipped'),
    (2, 'Shipped'),
    (3, 'Shipment Processing'),
    (4, 'Dispatched'),
    (5, 'Delivered'),
    (6, 'Fulfilled'),
    (7, 'Refund Requested'),
    (8, 'Return Initiated'),
    (9, 'Return Received'),
    (10, 'QC Processing'),
    (11, 'Refunded'),
    (12, 'Refund Cancelled'),
    (13, 'Exchange Requested');

INSERT INTO sale_order_shipping_statuses (id, status) VALUES
    (1, 'Un Shipped'),
    (2, 'Shipped');

INSERT INTO stock_ledger_types (id, type) VALUES
    (1, 'Inward'),
    (2, 'Adjustment'),
    (3, 'Transfer To'),
    (4, 'Transfer From'),
    (5, 'Sales');

INSERT INTO branches (id, branch, source_of_supply, phone, email, address_1, address_2, postcode, landmark, city, state, country, gstin) VALUES
    (1, 'Victory World Chennai', 'Tamil Nadu', '9000000000', 'admin@victoryworld.in', 'Victory World Headquarters', NULL, '600001', 'Marina Beach', 'Chennai', 'Tamil Nadu', 'India', '33AADFZ7502M1ZX');

INSERT INTO inventories (id, inventory, contact_name, phone, address_1, address_2, postcode, landmark, city, state, branch_id) VALUES
    (1, 'Victory World Chennai', 'Victory World Support', '9000000000', 'Victory World Warehouse', NULL, '600001', 'Marina Beach', 'Chennai', 'Tamil Nadu', 1);

INSERT INTO transporters (id, transporter, inventory_id) VALUES
    (1, 'Victory World', 1);

INSERT INTO users (id, firstname, lastname, username, phone, email, password, type_id, created_time) VALUES
    (10001, 'Victory', 'Admin', 'admin', '9000000000', 'admin@victoryworld.in', 'password', 2, (extract(epoch from now()) * 1000)::bigint),
    (1, 'Shaara', 'Distributor', 'VC00001', '9000000001', 'distributor@victoryworld.in', '123456', 1, (extract(epoch from now()) * 1000)::bigint);

INSERT INTO user_roles (user_id, role_id) VALUES
    (10001, 2);

INSERT INTO distributors (
    id,
    placement,
    rank_id,
    kyc_status_id,
    bank_status_id,
    referer_id,
    parent_id
) VALUES
    (1, 1, 1, 1, 1, NULL, NULL);

INSERT INTO categories (id, category, parent, display) VALUES
    (1, 'Personal Care', NULL, TRUE),
    (2, 'Sanitary Napkin', 1, TRUE);

INSERT INTO brands (id, brand) VALUES
    (1, 'Shaara');

INSERT INTO item_groups (id, name, category_id, brand_id, specification_id) VALUES
    (1, 'Shaara Sanitary Pads', 2, 1, NULL);

INSERT INTO price_lists (
    id,
    name,
    description,
    mrp,
    price,
    cost,
    gst_percent,
    pv,
    created_time,
    created_by
) VALUES
    (1, 'Main', 'Shaara Sanitary Pads', 4000, 4000, 4000, 0, 80, (extract(epoch from now()) * 1000)::bigint, 10001);

INSERT INTO items (
    id,
    group_id,
    sku,
    title,
    description,
    hsn,
    price_id,
    online_status,
    featured_status,
    created_by,
    created_time
) VALUES
    (1, 1, 'SHAARA-SANITARY-PADS', 'Shaara Sanitary Pads', 'Initial seed product for Victory World.', '96190010', 1, TRUE, TRUE, 10001, (extract(epoch from now()) * 1000)::bigint);

INSERT INTO stock_inwards (id, description, ref_id, owner_id, time) VALUES
    (1, 'Initial seed stock', 'SEED-001', 10001, (extract(epoch from now()) * 1000)::bigint);

INSERT INTO inward_items (id, item_id, inward_id, quantity, inventory_id) VALUES
    (1, 1, 1, 100, 1);

INSERT INTO stock_ledgers (
    id,
    item_id,
    inventory_id,
    quantity,
    opening_quantity,
    closing_quantity,
    type_id,
    time,
    owner_id
) VALUES
    (1, 1, 1, 100, 0, 100, 1, (extract(epoch from now()) * 1000)::bigint, 10001);

INSERT INTO stocks (id, item_id, inventory_id, quantity, location) VALUES
    (1, 1, 1, 100, NULL);

DO $$
DECLARE
    seq_name text;
    tbl_name text;
    max_id bigint;
BEGIN
    FOR tbl_name IN
        SELECT t.tablename
        FROM pg_tables t
        WHERE t.schemaname = 'public'
          AND EXISTS (
              SELECT 1
              FROM information_schema.columns
              WHERE table_schema = 'public'
                AND table_name = t.tablename
                AND column_name = 'id'
          )
    LOOP
        seq_name := pg_get_serial_sequence(format('%I.%I', 'public', tbl_name), 'id');
        IF seq_name IS NOT NULL THEN
            EXECUTE format('SELECT max(id) FROM %I.%I', 'public', tbl_name) INTO max_id;
            IF max_id IS NULL THEN
                EXECUTE format('SELECT setval(%L, 1, false)', seq_name);
            ELSE
                EXECUTE format('SELECT setval(%L, %s, true)', seq_name, max_id);
            END IF;
        END IF;
    END LOOP;
END $$;

COMMIT;
