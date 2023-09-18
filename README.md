# java-filmorate
MIRO: https://miro.com/app/board/uXjVMlUbaOM=/?share_link_id=291461665058
SQL trainer: http://sqlfiddle.com/#!17/9b6a67/1
Template repository for Filmorate project.
![Screenshot of a scheme filmorate.](https://github.com/Ksenni888/java-filmorate/blob/main/scheme.jpg)

Examples of request:

SELECT *
FROM film;

SELECT *
FROM users;

SELECT *
FROM genre;

SELECT *
FROM friendship;

SELECT *
FROM likeIds;

SELECT 
film_id,
COUNT(user_id)
FROM likeIds
GROUP BY film_id;

SELECT 
film.name,
genre_name
FROM film
LEFT OUTER JOIN genre ON genre.film_id = film.film_id
GROUP BY genre.genre_name, film.name
ORDER BY film.name;

SELECT
users.user_id,
friendship.friend_id,
friendship.status
FROM users
LEFT JOIN friendship ON users.user_id = friendship.user_id;
