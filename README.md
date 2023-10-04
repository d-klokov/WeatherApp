# Проект “Погода”

Веб-приложение для просмотра текущей погоды. Пользователь может зарегистрироваться и добавить в коллекцию один или несколько локаций (городов, сёл, других пунктов), после чего главная страница приложения начинает отображать список локаций с их текущей погодой.

![alt-text](https://github.com/d-klokov/WeatherApp/blob/master/weather.png)

Подробное описание задания: https://zhukovsd.github.io/java-backend-learning-course/Projects/WeatherViewer/

Чтобы запустить приложение локально docker и docker-compose должны быть установлены. 
1) Создать файл application.properties в папке src/main/resources.
2) В этом файле определить переменные HOST, POSTGRES_DATABASE, POSTGRES_USERNAME, POSTGRES_PASSWORD для подключения к БД. 
3) Запустить докер контейнер PostgreSQL: ``` docker start postgres_db ```
5) Запустить проект из IDE.
