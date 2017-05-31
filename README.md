# FXClub
## Introducción
Proyecto creado por los alumnos de ingeniería informática de la UJI Alberto González ([@albertogonper](https://github.com/albertogonper)) y Daniel Chía ([@dnchia](https://github.com/dnchia)), para la asignatura EI1048 del grado.
## Requisitos previos
BBDD MySQL, versión 5.x, preferiblemente 5.7
1. Bien mediante una instalación local
2. O mediante docker
```
docker run -d -p 3306:3306 -e MYSQL_ROOT_PASSWORD=r29dt4 -e MYSQL_USER=videoclub -e MYSQL_PASSWORD=q1w2e3r4t5y6 -e MYSQL_DATABASE=agdc-videoclub --name=videoclub-mysql mysql:5.7
```
**Importante, el proyecto busca por defecto la BBDD en el hostname `mysql`, puerto `3306` debido a que el motor de CI ofrece el servicio de BBDD resolviendo ese hostname.**

Para solucionar esto tenemos dos opciones:
1. Añadir al final del fichero **hosts** del sistema la siguiente línea
 `127.0.0.1 mysql`
2. En el fichero **application.properties** localizado en `$PROJECT_ROOT/src/main/resources`, cambiar el hostname de *mysql* a *localhost*

## Tecnologías empleadas
* Spring Boot
* Spring Data JPA
* Hibernate ORM
* JavaFX