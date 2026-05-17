-- zerroo database cleanup script
--
-- This script is intended to clear business data while preserving
-- core authentication/authorization tables and known lookup/status tables.
-- Review and adjust before executing.
--
-- NOTE: No explicit "Mantroy" tables were found in the backend code.
-- If your database contains additional Mantroy-specific tables, do not
-- truncate them unless you are sure they are safe to remove.

BEGIN;

-- Preserve auth/user tables and their direct role mappings
-- Users, Roles, User_Types, User_Roles, UserHistory, Login, ResetPassword, Otp, OtpType, Oauth

-- Preserve distributor/finance/user-extension lookup and status tables
-- Keep core auth tables: Users, Roles, User_Types, User_Roles, UserHistory, Login,
-- ResetPassword, Otp, OtpType, Oauth
-- Keep domain reference tables: Ranks, Kyc_Verification_Statuses, Bank_Verification_Statuses,
-- Income_Wallet_Transaction_Types, Purchase_Wallet_Transaction_Types,
-- Purchase_Wallet_Request_Statuses, Payout_Statuses, Sale_Order_Shipping_Statuses,
-- Sale_Order_Item_Statuses, Forward_Shipment_Statuses, Cutoff_Statuses,
-- Specification_Types

-- Cleanup candidate tables (catalog, transactional, and temporary business data)
TRUNCATE TABLE
    addresses,
    adjustment_items,
    backups,
    bank_verifications,
    branches,
    brands,
    carts,
    categories,
    colors,
    combo_group_items,
    combo_group_mappings,
    combo_groups,
    combos,
    couriers,
    cutoff_entries,
    cutoffs,
    distributor_histories,
    distributors,
    forward_shipment_histories,
    forward_shipment_items,
    forward_shipments,
    image_lists,
    images,
    income_wallet_transactions,
    inventories,
    invoice_items,
    invoices,
    inward_items,
    item_groups,
    item_histories,
    item_reviews,
    items,
    kyc_verifications,
    payout_entries,
    payouts,
    price_lists,
    purchase_wallet_requests,
    purchase_wallet_transactions,
    sale_order_items,
    sale_orders,
    serviceable_postcodes,
    sizes,
    specification_groups,
    specification_lists,
    specifications,
    stock_adjustments,
    stock_inwards,
    stock_ledgers,
    stock_transfers,
    stocks,
    transfer_items,
    transporter_courier_mappings,
    transporters
CASCADE;

COMMIT;
