CREATE DATABASE library_db;
USE library_db;

CREATE TABLE users (
	user_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(50) NOT NULL,
    role VARCHAR(10) NOT NULL
);
SELECT * FROM users;
ALTER TABLE users MODIFY password VARCHAR(100);
-- SELECT user_id, username, role FROM users WHERE role = 'user' ORDER BY user_id ;

CREATE TABLE books (
	book_id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(50) UNIQUE NOT NULL,
    author VARCHAR(50),
    is_issued BOOLEAN DEFAULT FALSE
);

ALTER TABLE books ADD COLUMN book_count INT DEFAULT 1;
SELECT * FROM books;
-- INSERT INTO books(title,author) VALUES ('Math', 'm');

CREATE TABLE issue_history (
	id INT AUTO_INCREMENT PRIMARY KEY,
    user VARCHAR(50),
    book_id INT,
    action VARCHAR(10),
    action_time DATETIME DEFAULT current_timestamp,
    FOREIGN KEY (book_id) REFERENCES books(book_id)
);

