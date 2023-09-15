# java-filmorate
SQL trainer http://sqlfiddle.com/#!17/9b6a67/1
Template repository for Filmorate project.
![Screenshot of a scheme filmorate.](https://raw.githubusercontent.com/Ksenni888/java-filmorate/main/scheme-filmorate.jpg)

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
