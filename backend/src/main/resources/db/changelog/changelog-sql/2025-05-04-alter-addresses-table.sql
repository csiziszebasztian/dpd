-- changeset cline:20250504-1
-- preconditions onFail:MARK_RAN
-- precondition-sql-check expectedResult:1 SELECT COUNT(*) FROM information_schema.columns WHERE table_name = 'addresses' AND column_name = 'address'
ALTER TABLE addresses
    ADD COLUMN postal_code VARCHAR(10),
    ADD COLUMN city VARCHAR(255),
    ADD COLUMN street VARCHAR(255),
    ADD COLUMN house_number VARCHAR(50),
    ADD COLUMN other_info VARCHAR(255);

-- changeset cline:20250504-2
-- preconditions onFail:MARK_RAN
-- precondition-sql-check expectedResult:1 SELECT COUNT(*) FROM information_schema.columns WHERE table_name = 'addresses' AND column_name = 'address'
ALTER TABLE addresses
    DROP COLUMN address;
