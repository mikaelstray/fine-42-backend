ALTER TABLE fines
    ADD COLUMN household_id BIGINT;

ALTER TABLE fines
    ADD CONSTRAINT fk_fines_on_household FOREIGN KEY (household_id) REFERENCES households (id);