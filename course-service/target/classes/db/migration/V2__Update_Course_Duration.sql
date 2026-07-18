ALTER TABLE courses DROP COLUMN IF EXISTS estimated_duration_hours;
ALTER TABLE courses ADD COLUMN estimated_duration_minutes INTEGER NOT NULL DEFAULT 0;
