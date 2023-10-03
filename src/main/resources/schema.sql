DROP TABLE IF EXISTS mpa, films, users, likeIds, genre, film_genre, friendship CASCADE;
CREATE TABLE mpa (
mpa_id int PRIMARY KEY AUTO_INCREMENT NOT NULL,
mpa_name varchar(200) NOT NULL
);

CREATE TABLE genre (
genre_id int PRIMARY KEY AUTO_INCREMENT NOT NULL,
genre_name varchar(200)
);

CREATE TABLE films (
film_id int PRIMARY KEY AUTO_INCREMENT NOT NULL,
film_name varchar(200) NOT NULL,
description varchar(200),
releaseDate date,
duration int check (duration>0),
mpa int,
genres integer Array,
rate int
);

CREATE TABLE users (
user_id int PRIMARY KEY AUTO_INCREMENT NOT NULL,
email varchar(200) NOT NULL,
login varchar(200) NOT NULL,
user_name varchar(200),
birthday date check (birthday<now())
);

CREATE TABLE likeIds (
film_id int NOT NULL,
user_id int NOT NULL,
PRIMARY KEY (film_id, user_id),
foreign key (film_id) references films(film_id),
foreign key (user_id) references users(user_id)
);


CREATE TABLE film_genre (
film_id int NOT NULL,
genre_id int NOT NULL,
foreign key (film_id) references films(film_id),
foreign key (genre_id) references genre(genre_id)
);

CREATE TABLE friendship (
user_id int NOT NULL ,
friend_id int NOT NULL,
foreign key (user_id) references users(user_id),
foreign key (friend_id) references users(user_id),
PRIMARY KEY (user_id, friend_id),
status boolean
);
