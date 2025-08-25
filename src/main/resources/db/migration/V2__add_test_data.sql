INSERT INTO  users (id, username, password, role, first_name, last_name) 
VALUES
(1, 'student1', '"$2a$10$h5C1GYLFC.I6m1HwLMUZsuyzFgAhGSX2KnPbaK/0kcGLgnV2Ye6jK"', 'ROLE_STUDENT', 'Иван', 'Иванов'),
(2, 'student2', '$2a$10$fX7cysGm7LgpysbNboOVKu2AqP/s2yCL1R6slnA.aW.m3WYCD6RXa',  'ROLE_STUDENT', 'Петр', 'Петров'),
(3, 'teacher1', '$2a$10$pN/SxbTGEKFLUN4R7qPCne9bH.GRv0qYppihe9dvH2kRUNY7AOqU6', 'ROLE_TEACHER', 'Олег', 'Смирнов'),
(4, 'student3','$2a$10$oFQTGnWzVjsLhGXCU7h4Zua4O2mdj8trJtzuH/r1O2CwO/zquu6Aq','ROLE_STUDENT','Александр','Александров'),
(5, 'student4','$2a$10$JtQzKP3LihlyMpckEQjlruwT3cP5CoM4vwZ1JMrXzsK4wI/1QRHji','ROLE_STUDENT','"Василий"','"Васильев"');

SELECT * from users;

INSERT INTO  student_profiles (user_id, group_name) 
VALUES
(1, 'A'),
(2, 'B'),
(4, 'А'),
(5, 'А');

SELECT * from student_profiles;

INSERT INTO  teacher_profiles (user_id, position) 
VALUES
(3, 'teacher');

SELECT * from teacher_profiles;

INSERT INTO  courses (id, title, description, teacher_id)
VALUES
    (1, 'Math', 'test', 3),
    (2, 'Music', 'test', 3),
    (3, 'Physics', 'test', 3);

SELECT * from courses;

INSERT INTO  tasks (id, title, task_content, course_id)
VALUES
    (1, 'task1', 'content',  1),
    (2, 'task2', 'content',  1),
    (3, 'task3', 'content',  1),
    (4, 'task4', 'content',  1),
    (5, 'task1', 'content',  2);;

SELECT * from tasks;

INSERT INTO  course_files (id, file_name, file_type, file_path, course_id)
VALUES
(1, 'file', 'pdf', 'assignments/file.pdf', 1);

SELECT * from course_files;

INSERT INTO  literature (id, title, author, link, course_id)
VALUES
(1, 'file', 'pdf', 'literature/file.pdf', 1);

SELECT * from literature;

INSERT INTO  student_answers (id, answer_text, grade, student_id, task_id)
VALUES
    (1, 'text', 4, 1, 1),
    (2, 'text', 4, 1, 2),
    (3, 'text', 4, 1, 3),
    (4, 'text', 4, 1, 4),
    (5, 'text', 5, 2, 1),
    (6, 'text', 5, 2, 2),
    (7, 'text', 5, 2, 3),
    (8, 'text', 5, 2, 4),
    (9, 'text', 3.5, 4, 1),
    (10, 'text', 5, 4, 2);

SELECT * from student_answers;

INSERT INTO  student_courses (id, student_id, course_id, start_date, status)
VALUES
    (1, 1, 1, '2025-08-15 10:30:00', 'COMPLETED'),
    (2, 2, 1, '2025-08-15 10:30:00', 'COMPLETED'),
    (3, 4, 1, '2025-08-15 10:30:00', 'ACTIVE'),
    (4, 5, 2, '2025-08-15 10:30:00', 'ACTIVE');
SELECT * from student_courses;

INSERT INTO  messages (id, text, status, sent_date_time, sender_id, recipient_id)
VALUES
    (1, 'text', 'UNREAD', '2025-08-15 10:30:00', 1, 3),
    (2, 'text', 'UNREAD', '2025-08-15 10:35:00', 1, 3),
    (3, 'text', 'UNREAD', '2025-08-15 11:30:00', 2, 3),
    (4, 'text', 'UNREAD', '2025-08-15 10:35:00', 2, 3);

SELECT * from messages;