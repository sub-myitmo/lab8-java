create table my_user (
id     SERIAL PRIMARY KEY,
username   TEXT   NOT NULL,
password   TEXT   NOT NULL
);

create table model (
id     SERIAL PRIMARY KEY,
name   TEXT   NOT NULL,
coor_x   INT   NOT NULL,
coor_y   DOUBLE PRECISION   NOT NULL,
creation_date   date  NOT NULL,
students_count BIGINT NOT NULL,
expelled_students BIGINT NOT NULL,
transferred_students INT NOT NULL,
semester TEXT NOT NULL,
admin_name TEXT NOT NULL,
birthday timestamp NOT NULL,
weight DOUBLE PRECISION   NOT NULL,
loc_x float NOT NULL,
loc_y INT NOT NULL,
loc_z DOUBLE PRECISION NOT NULL,
user_id INT REFERENCES my_user
);