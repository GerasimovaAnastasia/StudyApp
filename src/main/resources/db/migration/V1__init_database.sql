CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(255) UNIQUE NOT NULL,
    password TEXT NOT NULL,
    role VARCHAR(20) NOT NULL CHECK (role IN ('ROLE_STUDENT', 'ROLE_TEACHER')),
    first_name VARCHAR(100),
    last_name VARCHAR(100)
);
SELECT * FROM users;

CREATE TABLE teacher_profiles (
    user_id BIGINT PRIMARY KEY REFERENCES users(id) ON DELETE CASCADE,
    position VARCHAR(100)
);
SELECT * FROM teacher_profiles;

CREATE TABLE student_profiles (
    user_id BIGINT PRIMARY KEY REFERENCES users(id) ON DELETE CASCADE,
    group_name VARCHAR(50)
);
SELECT * FROM student_profiles;
CREATE TABLE courses (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) UNIQUE,
    description TEXT,
    teacher_id BIGINT REFERENCES teacher_profiles(user_id)
);
SELECT * FROM courses;

CREATE TABLE tasks (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255),
    task_content TEXT,
    course_id BIGINT REFERENCES courses(id) ON DELETE CASCADE
);
SELECT * FROM tasks;

CREATE TABLE literature (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) UNIQUE,
    author VARCHAR(255),
    link TEXT,
    course_id BIGINT REFERENCES courses(id) ON DELETE CASCADE
);
SELECT * FROM literature;

CREATE TABLE course_files (
    id BIGSERIAL PRIMARY KEY,
    file_name VARCHAR(255) UNIQUE,
    file_type VARCHAR(50),
    file_path TEXT,
    course_id BIGINT REFERENCES courses(id) ON DELETE CASCADE
);
SELECT * FROM course_files;

CREATE TABLE messages (
    id BIGSERIAL PRIMARY KEY,
    text TEXT NOT NULL,
    status VARCHAR(10) CHECK (status IN ('READ', 'UNREAD', 'UPDATE')),
    sent_date_time TIMESTAMP NOT NULL,
    sender_id BIGINT REFERENCES users(id) NOT NULL,
    recipient_id BIGINT REFERENCES users(id) NOT NULL
);
SELECT * FROM messages;

CREATE TABLE student_answers (
    id BIGSERIAL PRIMARY KEY,
    answer_text TEXT,
    grade INTEGER,
    student_id BIGINT REFERENCES student_profiles(user_id) ON DELETE CASCADE,
    task_id BIGINT REFERENCES tasks(id) ON DELETE CASCADE
);
SELECT * FROM student_answers;

CREATE TABLE student_courses (
    id BIGSERIAL PRIMARY KEY,
    student_id BIGINT NOT NULL,
    course_id BIGINT NOT NULL,
    start_date TIMESTAMP NOT NULL,
    status VARCHAR(20) CHECK (status IN ('ACTIVE', 'COMPLETED')),
    FOREIGN KEY (student_id) REFERENCES student_profiles(user_id) ON DELETE CASCADE,
    FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE CASCADE
);
SELECT * FROM student_courses;

ALTER TABLE tasks
ADD CONSTRAINT uk_title_tasks UNIQUE (title, course_id);
