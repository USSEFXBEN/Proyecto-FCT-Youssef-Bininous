CREATE TABLE users (
    id_usuario SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    rol VARCHAR(50) NOT NULL
);

CREATE TABLE routines (
    id_rutina SERIAL PRIMARY KEY,
    id_usuario INT NOT NULL REFERENCES users(id_usuario),
    nombre VARCHAR(100) NOT NULL,
    descripcion TEXT,
    horario TIME,
    dias_activos VARCHAR(100),
    completado BOOLEAN DEFAULT FALSE
);

CREATE TABLE habits (
    id_habito SERIAL PRIMARY KEY,
    id_usuario INT NOT NULL REFERENCES users(id_usuario),
    tipo VARCHAR(50),
    fecha DATE NOT NULL,
    completado BOOLEAN DEFAULT FALSE
);

CREATE TABLE reminders (
    id_recordatorio SERIAL PRIMARY KEY,
    id_usuario INT NOT NULL REFERENCES users(id_usuario),
    id_rutina INT NOT NULL REFERENCES routines(id_rutina),
    hora TIME,
    mensaje TEXT
);

INSERT INTO users (nombre, email, rol) 
VALUES ('Youssef Bininous', 'admin@example.com', 'admin');

INSERT INTO routines (id_usuario, nombre, descripcion, horario, dias_activos, completado)
VALUES (1, 'Revisión del sistema', 'Ver estadísticas y usuarios', '09:00', 'Lunes,Miércoles,Viernes', FALSE);

INSERT INTO habits (id_usuario, tipo, fecha, completado)
VALUES (1, 'Supervisión', '2025-10-24', FALSE);

INSERT INTO reminders (id_usuario, id_rutina, hora, mensaje)
VALUES (1, 1, '09:00', '¡Revisar usuarios y estadísticas!');
