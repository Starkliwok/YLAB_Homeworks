# Ссылки на задания
-------------------------------------------------------------------------------------------------------------------------------------
[1-dz](https://github.com/Starkliwok/YLAB_Homeworks/tree/1-dz)

[2-dz](https://github.com/Starkliwok/YLAB_Homeworks/tree/2-dz)

[3-dz](https://github.com/Starkliwok/YLAB_Homeworks/tree/3-dz)

-------------------------------------------------------------------------------------------------------------------------------------
> [!IMPORTANT]
> Создано с помощью:
> - Java 17
> - Maven
> - Servlet 6.0.0
> - Spring Framework 6.1.10
> - Spring doc OpenAPI starter 2.6.0
> - Swagger
> - Lombok 1.18.32
> - AspectJ 1.9.22.1
> - Jackson 2.17.1
> - Mupstruct 1.5.5.Final
> - Lombok mupstruct binding 0.2.0
> - Junit 5
> - Mockito 5.12.0
> - MockMVC
> - Testcontainers 1.19.8
> - AssertJ 3.26.0

> [!NOTE]
> 
> ТЗ
> -------------------------------------------------------------------------------------------------------------------------------------
> # Coworking-Service
> Разработайте приложение для управления коворкинг-пространством. Приложение должно позволять пользователям бронировать рабочие места, конференц-залы, а также управлять
> бронированиями и просматривать доступность ресурсов.
>
> # Функциональные требования:
> - регистрация и авторизация пользователя;
> - просмотр списка всех доступных рабочих мест и конференц-залов;
> - просмотр доступных слотов для бронирования на конкретную дату;
> - бронирование рабочего места или конференц-зала на определённое время и дату;
> - отмена бронирования;
> - добавление новых рабочих мест и конференц-залов, а также управление существующими;
> - просмотр всех бронирований и их фильтрация по дате, пользователю или ресурсу.
> 
> # Технические требования:
> - Необходимо обновить сервис согласно следующим требованиям и ограничениям.
> - Java-конфигурация приложения
> - Кастомные конфигурационные файлы заменить на application.yml
> - Удалить сервлеты, реализовать Rest-контроллеры (Spring MVC)
> - Swagger + Swagger UI
> - Аспекты переписать на Spring AOP
> - Внедрение зависимостей ТОЛЬКО через конструктор
> - Удалить всю логику создания сервисов, репозиториев и тд. Приложение должно полностью управляться спрингом
> - Добавить тесты на контроллеры (WebMVC)
> ------------------------------------------------------------------------------------------------------------------------------------

> [!TIP]
> 
> # Что было выполнено помимо ТЗ:
>
> - Реализация панели администратора
> - CRUD операции с пользователями с помощью панели администратора
> # Планы на дальнейшую реализацию:
> - Хранение зашифрованный паролей в БД
 
 # Сборка и запуск
 ------------------------------------------------------------------------------------------------------------------------------------
 - Java 17
 - Запустить docker.compose при открытом приложении Docker
 - С помощью Maven собрать проект mvn clean install
 - Для работы потребуется тестовый логин и тестовый пароль, который будет в БД после выполнения миграций.
 - (Администратор - root root)
 - Работа приложения проверена на Windows 10
-------------------------------------------------------------------------------------------------------------------------------------

# Эндпоинт документации Swagger:
------------------------------------------------------------------------------------------------------------------------------------
http://localhost:8080/swagger-ui/index.html
------------------------------------------------------------------------------------------------------------------------------------
