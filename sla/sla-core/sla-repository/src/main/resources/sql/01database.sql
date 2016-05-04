-- Default database and user initialization
CREATE DATABASE IF NOT EXISTS sc_sla;
GRANT ALL PRIVILEGES ON sc_sla.* TO atossla@'%' IDENTIFIED BY '_atossla_';
GRANT ALL PRIVILEGES ON sc_sla.* TO atossla@'localhost' IDENTIFIED BY '_atossla_';
