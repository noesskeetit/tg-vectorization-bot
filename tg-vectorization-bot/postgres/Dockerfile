FROM postgres:11.7

# Копируем init.sql в контейнер
COPY init.sql /docker-entrypoint-initdb.d/

# Устанавливаем переменные окружения
ENV POSTGRES_USER postgres
ENV POSTGRES_PASSWORD Manager1
ENV POSTGRES_DB postgres
ENV PGPORT 5555

# Копируем файлы конфигурации
COPY postgresql.conf /etc/postgresql/postgresql.conf
COPY pg_hba.conf /etc/postgresql/pg_hba.conf

# Переключаемся на пользователя postgres
USER postgres

EXPOSE 5555

# Запускаем PostgreSQL при старте контейнера
CMD ["postgres"]


