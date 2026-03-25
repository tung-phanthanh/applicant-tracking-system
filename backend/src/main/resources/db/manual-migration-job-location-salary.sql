-- Run manually when using spring.jpa.hibernate.ddl-auto=none (MySQL).
-- Adds location and salary columns for the jobs workflow.

ALTER TABLE jobs
    ADD COLUMN location VARCHAR(255) NULL AFTER description;

ALTER TABLE jobs
    ADD COLUMN salary VARCHAR(255) NULL AFTER location;
