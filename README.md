# Ссылки на задания
-------------------------------------------------------------------------------------------------------------------------------------
[1-dz](https://github.com/Starkliwok/YLAB_Homeworks/tree/1-dz)

[2-dz](https://github.com/Starkliwok/YLAB_Homeworks/tree/2-dz)

-------------------------------------------------------------------------------------------------------------------------------------
> [!IMPORTANT]
> Создано с помощью:
> - Java 17
> - Servlet
> - Maven
> - Junit 5
> - Mockito 5.12.0
> - AssertJ 3.26.0
> - Lombok 1.18.32
> - Testcontainers 1.19.8
> - AspectJ 1.9.22.1
> - Jackson 2.17.1
> - Mupstruct 1.5.5.Final
> - Lombok mupstruct binding 0.2.0

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
> - Все взаимодействие должно теперь осуществляться через отправку HTTP запросов
> - Сервлеты должны принимать JSON и отдавать также JSON
> - Для сериализации и десериализации использовать jackson
> - Использовать понятное именование эндпоинтов
> - Возвращать разные статус-коды
> - Добавить DTO (если ранее не было заложено в логике)
> - Для маппинга сущностей в дто использовать MapStruct
> - Реализовать валидацию входящих ДТО
> - Сделать Аудит действий пользователя через аспекты
> - Также реализовать на аспектах логирование выполнения любого метода (с замером времени выполнения)
> - Сервлеты должны быть покрыты тестами
> ------------------------------------------------------------------------------------------------------------------------------------

> [!TIP]
> 
> # Что было выполнено помимо ТЗ:
>
> - Реализация панели администратора
> - CRUD операции с пользователями с помощью панели администратора
> # Планы на дальнейшую реализацию:
> - Хранение зашифрованный паролей в БД
> - Реализация JWT
> - Переписать аспекты аудита
 
 # Сборка и запуск
 ------------------------------------------------------------------------------------------------------------------------------------
 - Java 17
 - Запустить docker.compose при открытом приложении Docker
 - С помощью Maven собрать проект mvn clean install
 - Для работы потребуется тестовый логин и тестовый пароль, который уже есть в системе.
 - (Администратор - root root)
 - Работа приложения проверена на Windows 10
-------------------------------------------------------------------------------------------------------------------------------------

# Доступные эндпоинты:
1. http://localhost:8080/registration - POST регистрация нового пользователя
```
{
   "username": "user1234",
   "password": "user1234"
}
```
-----------------------------
2. http://localhost:8080/login - POST аутентификация пользователя
```
{
   "username": "root",
   "password": "root"
}
```
-----------------------------
3. http://localhost:8080/logout - GET выход из аккаунта
-----------------------------
4. http://localhost:8080/reservation_place/{id} - GET получение места для бронирования по id, в случае если id не указан, возвращаются все места
-----------------------------
5. http://localhost:8080/reservation_place - POST создание нового места для бронирования (ADMIN)
```
{
   "typeId": 1,
   "name": "workplace2",
   "placeArea": 40.2,
   "costPerHour": 20.6,
   "numberOfSeats": 1
}
```
-----------------------------
6. http://localhost:8080/reservation_place - PUT обновление места для бронирования (ADMIN)
```
{
   "id": 1,
   "typeId": 1,
   "name": "workplace2",
   "placeArea": 40.2,
   "costPerHour": 20.6,
   "numberOfSeats": 1
}
```
-----------------------------
7. http://localhost:8080/reservation_place/{id} - DELETE удаление места для бронирования по id (ADMIN)
-----------------------------
8. http://localhost:8080/reservation_place/date_slot/{id} - GET получение всех доступных дат для бронирования места с id, в случае если id не указан, возвращаются все места
-----------------------------
9. http://localhost:8080/reservation_place/date_slot - POST получение доступных мест для бронирования на указанную дату
```
{
   "localDate": "10-07-2024"
}
```
-----------------------------
10. http://localhost:8080/reservation/time_slot - POST получение доступных временных промежутков для бронирования места
```
{
   "reservationPlaceId": 1,
   "localDate": "10-07-2024"
}
```
-----------------------------
11. http://localhost:8080/reservation/{id} - GET получение брони пользователя по указанному id, в случае если id не указан, возвращаются все брони пользователя
-----------------------------
12. http://localhost:8080/reservation - POST добавление новой брони
```
{
   "startDate": "10-07-2024 10",
   "endDate": "10-07-2024 12",
   "reservationPlaceId": 1
}
```
-----------------------------
13. http://localhost:8080/reservation - PUT обновление брони
```
{
   "id": 1,
   "startDate": "10-07-2024 10",
   "endDate": "10-07-2024 12",
   "reservationPlaceId": 1
}
```
-----------------------------
14. http://localhost:8080/reservation/{id} - DELETE удаление брони пользователя по id брони
-----------------------------
15. http://localhost:8080/admin/audit/{id} - GET получение аудита по id, в случае если id не указан, возвращаются все аудиты (ADMIN)
-----------------------------
16. http://localhost:8080/admin/reservation/{id} - GET получение брони по указанному id, в случае если id не указан, возвращаются все брони (ADMIN)
-----------------------------
17. http://localhost:8080/admin/reservation/{id} - DELETE удаление брони по id брони (ADMIN)
-----------------------------
18. http://localhost:8080/admin/reservation_place/sort - POST сортировка помещений по указанной сортировке в теле метода (ADMIN)
```
{
   "sortType": "type",
   "placeType": "Workplace"
}
```
-----------------------------
19. http://localhost:8080/admin/reservation/sort - POST сортировка броней по указанной сортировке в теле метода (ADMIN)
```
{
   "sortType": "user"
}
```
-----------------------------
20. http://localhost:8080/admin/user/{id} - GET получение пользователя по id, в случае если id не указан, возвращаются все пользователи (ADMIN)
-----------------------------
21. http://localhost:8080/admin/user/{id} - DELETE удаление пользователя по id (ADMIN)
-----------------------------
