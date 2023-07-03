INSERT INTO books (number_of_copies, `year`, created_at, authors, isbn, publisher, synopsis, title) VALUES(10, 1999, '2023-06-22 12:14', 'J.K.Rowling', '1234567890987', 'Salani', '', 'Harry Potter e la pietra filosofale');
INSERT INTO books (number_of_copies, `year`, created_at, authors, isbn, publisher, synopsis, title) VALUES(13, 1960, '2023-06-22 11:00', 'Frank Herbert', '9685231456987', 'Einaudi', '', 'Dune');
INSERT INTO borrowings (book_id, borrowing_date, expiry_date, return_date, note) VALUES(1, '2023-06-10', '2023-07-10', '2023-06-28', 'some pages are spoiled');
INSERT INTO borrowings (book_id, borrowing_date, expiry_date, return_date, note) VALUES(1, '2023-05-20', '2023-06-20', null, null);
INSERT INTO borrowings (book_id, borrowing_date, expiry_date, return_date, note) VALUES(2, '2023-03-10', '2023-04-10', '2023-03-28', null);
INSERT INTO categories (name, description) VALUES('fiction', 'fiction');
INSERT INTO categories (name, description) VALUES('teenagers', 'books for age 11 to 19');
INSERT INTO categories (name, description) VALUES('non-fiction', 'books non fiction');
INSERT INTO book_category (book_id, category_id) VALUES(1,1);
INSERT INTO book_category (book_id, category_id) VALUES(1,2);
INSERT INTO book_category (book_id, category_id) VALUES(1,3);
INSERT INTO book_category (book_id, category_id) VALUES(2,2);

INSERT INTO roles (id, name) VALUES(1,'ADMIN');
INSERT INTO roles (id, name) VALUES(2,'USER');
INSERT INTO users (id, email, first_name, last_name, password) VALUES(1, 'john@email.com', 'John', 'Doe', '{noop}john');
INSERT INTO users (id, email, first_name, last_name, password) VALUES(2, 'jane@email.com', 'Jane', 'Doe', '{noop}jane');
INSERT INTO users_roles (roles_id, user_id) VALUES(1, 1);
INSERT INTO users_roles (roles_id, user_id) VALUES(2, 2);



