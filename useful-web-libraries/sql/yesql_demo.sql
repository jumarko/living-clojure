-- You will need to create a person table in "test" schema:
--CREATE TABLE person
--(
--    first_name VARCHAR(64),
--    last_name VARCHAR(64),
--    id INT(11) PRIMARY KEY NOT NULL
--);


-- name: get-users
-- read all users from database
SELECT *
FROM test.person


-- name: get-user-by-first-name
-- read single user data
SELECT *
FROM test.person
WHERE first_name = :first_name