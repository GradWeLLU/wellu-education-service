CREATE TABLE quiz_attempt (
    attempt_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL,
    score DOUBLE PRECISION,
    completed_at TIMESTAMP,
    quiz_id UUID NOT NULL,

    CONSTRAINT fk_quiz_attempt_quiz
        FOREIGN KEY (quiz_id)
        REFERENCES quizzes(id)
        ON DELETE CASCADE
);

CREATE TABLE attempt_answers (
    quiz_attempt_attempt_id UUID NOT NULL,
    question_id UUID NOT NULL,
    selected_answer INTEGER,

    CONSTRAINT fk_attempt_answers_attempt
        FOREIGN KEY (quiz_attempt_attempt_id)
        REFERENCES quiz_attempt(attempt_id)
        ON DELETE CASCADE
);

CREATE INDEX idx_quiz_attempt_quiz_id ON quiz_attempt(quiz_id);
CREATE INDEX idx_quiz_attempt_user_id ON quiz_attempt(user_id);
CREATE INDEX idx_attempt_answers_attempt_id ON attempt_answers(quiz_attempt_attempt_id);
