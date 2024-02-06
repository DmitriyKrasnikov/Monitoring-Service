# Инструкция по развертыванию приложения

## Шаг 1: Скачайте проект из репозитория

1. Откройте терминал.
2. Перейдите в каталог, где хотите сохранить проект.
3. Введите следующую команду: `git clone <URL репозитория>`. Замените `<URL репозитория>` на URL репозитория.

## Шаг 2: Откройте проект в IntelliJ IDEA

1. Запустите IntelliJ IDEA.
2. Нажмите `File -> Open`.
3. Найдите и выберите папку проекта, затем нажмите `OK`.

## Шаг 3: Выполните сборку проекта

1. Нажмите `Build -> Build Project` для сборки проекта.

## Шаг 4: Запустите приложение

1. Нажмите `Run -> Main` для запуска приложения.

# Эндпоинты приложения

- `POST /meter/register` - в теле передаётся объект UserDto для регистрации пользователя.
- `POST /meter/login` - в теле передаётся объект UserDto. В ответе возвращается токен, который в дальнейшем необходимо 
- использовать в заголовке "Authorization" запросов. Значение заголовка представлено в виде: "Bearer <токен>"
- `GET /meter/logout` - выход из системы.
- `GET /meter/readings` - получение текущих показаний счётчика конкретного пользователя.
- `POST /meter/readings` - подача показаний пользователем.
- `GET /meter/readings?month=may` - получение показаний конкретного пользователя за конкретный месяц.
- `GET /meter/readings/history` - просмотр истории показаний пользователя.
- `GET /meter/readings/all` - получение текущих показаний всех пользователей. Функция доступна только администратору.
