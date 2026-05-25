BEGIN;

UPDATE users
SET username = 'VW0001'
WHERE id = 2
  AND type_id = (
    SELECT id FROM user_types WHERE type = 'Distributor'
  );

DELETE FROM user_histories
WHERE owner_id IN (
    SELECT id FROM users
    WHERE type_id = (
        SELECT id FROM user_types WHERE type = 'Distributor'
    )
    AND id <> 2
)
   OR user_id IN (
    SELECT id FROM users
    WHERE type_id = (
        SELECT id FROM user_types WHERE type = 'Distributor'
    )
    AND id <> 2
);

DELETE FROM user_roles
WHERE user_id IN (
    SELECT id FROM users
    WHERE type_id = (
        SELECT id FROM user_types WHERE type = 'Distributor'
    )
    AND id <> 2
);

DELETE FROM logins
WHERE user_id IN (
    SELECT id FROM users
    WHERE type_id = (
        SELECT id FROM user_types WHERE type = 'Distributor'
    )
    AND id <> 2
);

DELETE FROM reset_passwords
WHERE user_id IN (
    SELECT id FROM users
    WHERE type_id = (
        SELECT id FROM user_types WHERE type = 'Distributor'
    )
    AND id <> 2
);

DELETE FROM carts
WHERE user_id IN (
    SELECT id FROM users
    WHERE type_id = (
        SELECT id FROM user_types WHERE type = 'Distributor'
    )
    AND id <> 2
);

DELETE FROM invoices
WHERE user_id IN (
    SELECT id FROM users
    WHERE type_id = (
        SELECT id FROM user_types WHERE type = 'Distributor'
    )
    AND id <> 2
);

DELETE FROM sale_orders
WHERE user_id IN (
    SELECT id FROM users
    WHERE type_id = (
        SELECT id FROM user_types WHERE type = 'Distributor'
    )
    AND id <> 2
);

DELETE FROM forward_shipments
WHERE user_id IN (
    SELECT id FROM users
    WHERE type_id = (
        SELECT id FROM user_types WHERE type = 'Distributor'
    )
    AND id <> 2
);

DELETE FROM item_histories
WHERE owner_id IN (
    SELECT id FROM users
    WHERE type_id = (
        SELECT id FROM user_types WHERE type = 'Distributor'
    )
    AND id <> 2
);

DELETE FROM bank_verifications
WHERE action_by IN (
    SELECT id FROM users
    WHERE type_id = (
        SELECT id FROM user_types WHERE type = 'Distributor'
    )
    AND id <> 2
);

DELETE FROM kyc_verifications
WHERE action_by IN (
    SELECT id FROM users
    WHERE type_id = (
        SELECT id FROM user_types WHERE type = 'Distributor'
    )
    AND id <> 2
);

DELETE FROM purchase_wallet_requests
WHERE action_by IN (
    SELECT id FROM users
    WHERE type_id = (
        SELECT id FROM user_types WHERE type = 'Distributor'
    )
    AND id <> 2
)
   OR distributor_id IN (
    SELECT id FROM users
    WHERE type_id = (
        SELECT id FROM user_types WHERE type = 'Distributor'
    )
    AND id <> 2
);

DELETE FROM payouts
WHERE approved_by IN (
    SELECT id FROM users
    WHERE type_id = (
        SELECT id FROM user_types WHERE type = 'Distributor'
    )
    AND id <> 2
)
   OR created_by IN (
    SELECT id FROM users
    WHERE type_id = (
        SELECT id FROM user_types WHERE type = 'Distributor'
    )
    AND id <> 2
);

DELETE FROM items
WHERE created_by IN (
    SELECT id FROM users
    WHERE type_id = (
        SELECT id FROM user_types WHERE type = 'Distributor'
    )
    AND id <> 2
)
   OR last_modified_by IN (
    SELECT id FROM users
    WHERE type_id = (
        SELECT id FROM user_types WHERE type = 'Distributor'
    )
    AND id <> 2
);

DELETE FROM price_lists
WHERE created_by IN (
    SELECT id FROM users
    WHERE type_id = (
        SELECT id FROM user_types WHERE type = 'Distributor'
    )
    AND id <> 2
)
   OR last_modified_by IN (
    SELECT id FROM users
    WHERE type_id = (
        SELECT id FROM user_types WHERE type = 'Distributor'
    )
    AND id <> 2
);

DELETE FROM sizes
WHERE created_by IN (
    SELECT id FROM users
    WHERE type_id = (
        SELECT id FROM user_types WHERE type = 'Distributor'
    )
    AND id <> 2
)
   OR modified_by IN (
    SELECT id FROM users
    WHERE type_id = (
        SELECT id FROM user_types WHERE type = 'Distributor'
    )
    AND id <> 2
);

DELETE FROM stock_adjustments
WHERE owner_id IN (
    SELECT id FROM users
    WHERE type_id = (
        SELECT id FROM user_types WHERE type = 'Distributor'
    )
    AND id <> 2
);

DELETE FROM stock_inwards
WHERE owner_id IN (
    SELECT id FROM users
    WHERE type_id = (
        SELECT id FROM user_types WHERE type = 'Distributor'
    )
    AND id <> 2
);

DELETE FROM stock_ledgers
WHERE owner_id IN (
    SELECT id FROM users
    WHERE type_id = (
        SELECT id FROM user_types WHERE type = 'Distributor'
    )
    AND id <> 2
);

DELETE FROM stock_transfers
WHERE owner_id IN (
    SELECT id FROM users
    WHERE type_id = (
        SELECT id FROM user_types WHERE type = 'Distributor'
    )
    AND id <> 2
);

DELETE FROM cutoffs
WHERE initiated_by IN (
    SELECT id FROM users
    WHERE type_id = (
        SELECT id FROM user_types WHERE type = 'Distributor'
    )
    AND id <> 2
);

DELETE FROM users
WHERE id IN (
    SELECT id FROM users
    WHERE type_id = (
        SELECT id FROM user_types WHERE type = 'Distributor'
    )
    AND id <> 2
);

COMMIT;
