#!/bin/bash

set -e

mysql -uroot -p"$MYSQL_ROOT_PASSWORD" <<SQL

CREATE DATABASE IF NOT EXISTS bd_auth;
CREATE DATABASE IF NOT EXISTS bd_unidad;
CREATE DATABASE IF NOT EXISTS bd_asignacion;
CREATE DATABASE IF NOT EXISTS bd_evaluacion;

GRANT ALL PRIVILEGES ON \`bd\\_auth\`.* TO '${MYSQL_USER}'@'%';
GRANT ALL PRIVILEGES ON \`bd\\_unidad\`.* TO '${MYSQL_USER}'@'%';
GRANT ALL PRIVILEGES ON \`bd\\_asignacion\`.* TO '${MYSQL_USER}'@'%';
GRANT ALL PRIVILEGES ON \`bd\\_evaluacion\`.* TO '${MYSQL_USER}'@'%';

FLUSH PRIVILEGES;

SQL