CREATE TABLE enrollments (
    id UUID PRIMARY KEY,
    student_id UUID NOT NULL,
    course_id UUID NOT NULL,
    status VARCHAR(50) NOT NULL,
    progress_percentage INTEGER NOT NULL DEFAULT 0,
    completed_lessons INTEGER NOT NULL DEFAULT 0,
    total_lessons INTEGER NOT NULL,
    last_accessed_lesson_id UUID,
    enrolled_at TIMESTAMP NOT NULL,
    completed_at TIMESTAMP,
    last_accessed_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    UNIQUE(student_id, course_id)
);

CREATE TABLE lesson_progress (
    id UUID PRIMARY KEY,
    enrollment_id UUID NOT NULL,
    lesson_id UUID NOT NULL,
    completed BOOLEAN NOT NULL DEFAULT FALSE,
    completed_at TIMESTAMP,
    watch_time_minutes INTEGER NOT NULL DEFAULT 0,
    last_position_seconds INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    CONSTRAINT fk_lp_enrollment FOREIGN KEY (enrollment_id) REFERENCES enrollments(id) ON DELETE CASCADE,
    UNIQUE(enrollment_id, lesson_id)
);
