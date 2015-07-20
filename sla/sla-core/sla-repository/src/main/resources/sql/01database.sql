-- Default database and user initialization
CREATE DATABASE sc_sla;
CREATE USER atossla@'%' IDENTIFIED BY '_atossla_';
GRANT ALL PRIVILEGES ON sc_sla.* TO atossla@'%';
