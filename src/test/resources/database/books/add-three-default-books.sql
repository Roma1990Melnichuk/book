
DELETE FROM books_categories WHERE book_id IN (SELECT id FROM books);
DELETE FROM books;
INSERT INTO books (id, price, title, author, isbn) VALUES
                                                       (1, 10.99, 'Kobzar', 'Shevchenko', '9460303332081'),
                                                       (2, 15.49, '1984', 'Orwell', '9780451524935'),
                                                       (3, 7.99, 'The Catcher in the Rye', 'Salinger', '9780316769488');