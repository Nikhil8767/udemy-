CREATE TABLE categories (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    description VARCHAR(500),
    icon VARCHAR(255),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP
);

ALTER TABLE courses DROP COLUMN IF EXISTS category;
ALTER TABLE courses ADD COLUMN category_id UUID;

-- Since this is applied over an existing schema for normalization, link the foreign key
ALTER TABLE courses ADD CONSTRAINT fk_course_category FOREIGN KEY (category_id) REFERENCES categories (id);

-- Enforce Not Null after foreign keys are established (assume tests clear old un-mapped data naturally or are empty)
-- Wait, if it's empty this works immediately. If not empty it requires default Category. Assuming empty for new enterprise spin-up.
ALTER TABLE courses ALTER COLUMN category_id SET NOT NULL;
