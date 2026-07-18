CREATE TABLE sections (
    id UUID PRIMARY KEY,
    course_id UUID NOT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    display_order INTEGER NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    UNIQUE(course_id, display_order)
);

CREATE TABLE lessons (
    id UUID PRIMARY KEY,
    section_id UUID NOT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    content_type VARCHAR(50) NOT NULL,
    video_url VARCHAR(500),
    pdf_url VARCHAR(500),
    article_content TEXT,
    duration_minutes INTEGER NOT NULL,
    is_preview BOOLEAN NOT NULL DEFAULT FALSE,
    display_order INTEGER NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    CONSTRAINT fk_lesson_section FOREIGN KEY (section_id) REFERENCES sections(id) ON DELETE CASCADE,
    UNIQUE(section_id, display_order)
);

CREATE TABLE resources (
    id UUID PRIMARY KEY,
    lesson_id UUID NOT NULL,
    title VARCHAR(255) NOT NULL,
    resource_type VARCHAR(50),
    file_url VARCHAR(500) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_resource_lesson FOREIGN KEY (lesson_id) REFERENCES lessons(id) ON DELETE CASCADE
);
