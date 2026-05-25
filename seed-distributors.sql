-- seed-distributors.sql
--
-- Normalize distributor usernames to VW0001-style IDs and assign six-digit numeric passwords.
-- Run this against your PostgreSQL database after reviewing and backing up current data.

BEGIN;

-- Ensure distributor usernames follow the VW0001 pattern.
UPDATE Users
SET username = 'VW' || lpad(id::text, 4, '0')
WHERE type_id = (
    SELECT id
    FROM user_types
    WHERE type = 'Distributor'
);

-- Assign a random six-digit numeric password to all distributor users.
UPDATE Users
SET password = lpad((floor(random() * 1000000)::int)::text, 6, '0')
WHERE type_id = (
    SELECT id
    FROM user_types
    WHERE type = 'Distributor'
);

COMMIT;
