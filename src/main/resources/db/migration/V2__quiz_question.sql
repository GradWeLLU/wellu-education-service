-- Enable UUID generation
CREATE EXTENSION IF NOT EXISTS pgcrypto;

-- =========================
-- QUIZZES TABLE
-- =========================
CREATE TABLE quizzes (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    title TEXT NOT NULL,
    difficulty TEXT NOT NULL,

    time_limit INTEGER,
    is_daily BOOLEAN DEFAULT FALSE,
    total_points INTEGER
);

-- Difficulty constraint
ALTER TABLE quizzes
ADD CONSTRAINT chk_quiz_difficulty
CHECK (difficulty IN ('EASY', 'MEDIUM', 'HARD'));

-- Time limit must be positive
ALTER TABLE quizzes
ADD CONSTRAINT chk_time_limit_positive
CHECK (time_limit IS NULL OR time_limit > 0);


-- =========================
-- QUESTIONS TABLE
-- =========================
CREATE TABLE questions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    quiz_id UUID NOT NULL,

    content TEXT NOT NULL,
    correct_answer_index INTEGER NOT NULL,
    explanation TEXT,
    points INTEGER,

    CONSTRAINT fk_questions_quiz
        FOREIGN KEY (quiz_id)
        REFERENCES quizzes(id)
        ON DELETE CASCADE
);

-- Points must be non-negative
ALTER TABLE questions
ADD CONSTRAINT chk_points_positive
CHECK (points IS NULL OR points >= 0);


-- =========================
-- QUESTION_CHOICES TABLE
-- =========================
CREATE TABLE question_choices (
    id BIGSERIAL PRIMARY KEY,

    question_id UUID NOT NULL,
    choice_text TEXT NOT NULL,
    choice_order INTEGER NOT NULL,

    CONSTRAINT fk_choices_question
        FOREIGN KEY (question_id)
        REFERENCES questions(id)
        ON DELETE CASCADE
);


-- =========================
-- INDEXES
-- =========================
CREATE INDEX idx_questions_quiz_id ON questions(quiz_id);
CREATE INDEX idx_choices_question_id ON question_choices(question_id);