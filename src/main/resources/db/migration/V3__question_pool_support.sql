ALTER TABLE questions
ALTER COLUMN quiz_id DROP NOT NULL;

ALTER TABLE questions
ADD COLUMN difficulty TEXT;

ALTER TABLE questions
ADD CONSTRAINT chk_question_difficulty
CHECK (difficulty IS NULL OR difficulty IN ('EASY', 'MEDIUM', 'HARD'));
