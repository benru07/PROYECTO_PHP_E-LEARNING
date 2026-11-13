-- =====================================================================
--  LEARNOVA - Plataforma e-learning
--  Copia de seguridad: creación de la base de datos, tablas, relaciones
--  y datos (MySQL 8 / MariaDB 10.5+)
--
--  Relaciones (todas 1:N):
--     categorias (1) ---< (N) cursos (1) ---< (N) modulos (1) ---< (N) lecciones
--
--  Importar:  mysql -u root -p < backup.sql
--  o desde phpMyAdmin: pestaña "Importar".
-- =====================================================================

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

DROP DATABASE IF EXISTS learnova;
CREATE DATABASE learnova CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE learnova;

-- ---------------------------------------------------------------------
-- categorias: áreas de conocimiento
-- ---------------------------------------------------------------------
CREATE TABLE categorias (
    id              INT          NOT NULL AUTO_INCREMENT,
    nombre          VARCHAR(60)  NOT NULL,                  -- nombre de la temática
    descripcion     VARCHAR(255) NULL,
    orden           INT          NOT NULL DEFAULT 0,        -- orden de aparición en los menús
    activa          TINYINT(1)   NOT NULL DEFAULT 1,
    fecha_creacion  DATE         NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uq_categorias_nombre (nombre)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- cursos: información general. Cada curso pertenece a UNA categoría.
-- ---------------------------------------------------------------------
CREATE TABLE cursos (
    id                INT           NOT NULL AUTO_INCREMENT,
    categoria_id      INT           NOT NULL,
    titulo            VARCHAR(150)  NOT NULL,
    descripcion       TEXT          NULL,
    nivel             ENUM('basico','intermedio','avanzado') NOT NULL DEFAULT 'basico',  -- nivel de dificultad
    duracion_horas    INT           NOT NULL,
    precio            DECIMAL(8,2)  NOT NULL DEFAULT 0.00,
    publicado         TINYINT(1)    NOT NULL DEFAULT 0,
    fecha_publicacion DATE          NOT NULL,
    PRIMARY KEY (id),
    KEY idx_cursos_categoria (categoria_id),
    CONSTRAINT fk_cursos_categoria FOREIGN KEY (categoria_id)
        REFERENCES categorias (id) ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT chk_cursos_precio CHECK (precio >= 0),
    CONSTRAINT chk_cursos_duracion CHECK (duracion_horas > 0)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- modulos: unidades didácticas de un curso. Cada módulo pertenece a UN curso.
-- ---------------------------------------------------------------------
CREATE TABLE modulos (
    id              INT          NOT NULL AUTO_INCREMENT,
    curso_id        INT          NOT NULL,
    titulo          VARCHAR(150) NOT NULL,                  -- título del bloque
    descripcion     VARCHAR(255) NULL,
    orden           INT          NOT NULL,                  -- posición dentro del curso
    obligatorio     TINYINT(1)   NOT NULL DEFAULT 1,
    fecha_creacion  DATE         NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uq_modulo_orden (curso_id, orden),           -- no se repite el orden dentro de un curso
    CONSTRAINT fk_modulos_curso FOREIGN KEY (curso_id)
        REFERENCES cursos (id) ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT chk_modulos_orden CHECK (orden > 0)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- lecciones: material formativo de un módulo. Cada lección pertenece a UN módulo.
-- ---------------------------------------------------------------------
CREATE TABLE lecciones (
    id                INT          NOT NULL AUTO_INCREMENT,
    modulo_id         INT          NOT NULL,
    titulo            VARCHAR(150) NOT NULL,                -- título del contenido
    tipo_recurso      ENUM('video','pdf','audio','imagen','texto') NOT NULL DEFAULT 'video',
    recurso_url       VARCHAR(255) NULL,                    -- recurso multimedia
    duracion_min      INT          NOT NULL DEFAULT 0,      -- duración en minutos
    orden             INT          NOT NULL,                -- posición dentro del módulo
    gratuita          TINYINT(1)   NOT NULL DEFAULT 0,
    fecha_publicacion DATE         NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uq_leccion_orden (modulo_id, orden),         -- lecciones ordenadas dentro del módulo
    CONSTRAINT fk_lecciones_modulo FOREIGN KEY (modulo_id)
        REFERENCES modulos (id) ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT chk_lecciones_duracion CHECK (duracion_min >= 0),
    CONSTRAINT chk_lecciones_orden CHECK (orden > 0)
) ENGINE=InnoDB;

-- =====================================================================
--  DATOS
-- =====================================================================


INSERT INTO categorias (nombre, descripcion, orden, activa, fecha_creacion) VALUES
  ('Programación', 'Lenguajes, bases de datos y desarrollo de software', 1, 1, '2025-09-01'),
  ('Diseño', 'Interfaces, experiencia de usuario y diseño gráfico', 2, 1, '2025-09-01'),
  ('Idiomas', 'Inglés, preparación de exámenes oficiales y más', 3, 1, '2025-09-01'),
  ('Marketing', 'Marketing digital, redes sociales y SEO', 4, 1, '2025-09-02'),
  ('Negocios', 'Gestión empresarial, emprendimiento y finanzas', 5, 1, '2025-09-02'),
  ('Otros', 'Cursos que no encajan en el resto de temáticas', 6, 1, '2025-09-03'),
  ('Matemáticas', 'Álgebra, cálculo y estadística', 7, 1, '2025-09-10'),
  ('Física', 'Mecánica, termodinámica y física cuántica', 8, 1, '2025-09-10'),
  ('Ciencias', 'Ciencias naturales y experimentales', 9, 1, '2025-09-15'),
  ('Historia', 'Historia universal y de España', 10, 0, '2025-09-15');

INSERT INTO cursos (categoria_id, titulo, descripcion, nivel, duracion_horas, precio, publicado, fecha_publicacion) VALUES
  (1, 'JAVA con Fabio', 'Java desde cero: sintaxis, POO y colecciones.', 'intermedio', 10, 99.99, 1, '2025-10-01'),
  (3, 'Curso de Cambridge con Fabio', 'Preparación del examen de Cambridge, nivel B2.', 'intermedio', 8, 129.99, 1, '2025-10-05'),
  (7, 'Matemáticas con Fabio', 'Álgebra y cálculo para bachillerato y universidad.', 'intermedio', 12, 79.0, 1, '2025-10-10'),
  (8, 'Física cuántica', 'Introducción a la física cuántica con ejercicios resueltos.', 'avanzado', 16, 149.99, 1, '2025-10-15'),
  (9, 'Ciencias experimentales', 'Método científico y prácticas de laboratorio.', 'avanzado', 12, 89.0, 1, '2025-10-20'),
  (10, 'Historia de España', 'Un recorrido por la historia de España en 3 horas.', 'basico', 3, 0.0, 1, '2025-11-01'),
  (1, 'Acceso a Datos', 'JDBC, ORM y gestión de bases de datos desde Java.', 'avanzado', 30, 199.99, 1, '2025-11-10'),
  (2, 'Desarrollo de Interfaces', 'Diseño y desarrollo de interfaces gráficas de usuario.', 'intermedio', 20, 149.99, 1, '2025-11-15'),
  (1, 'Programación de dispositivos móviles', 'Apps móviles híbridas y nativas.', 'intermedio', 25, 179.0, 1, '2025-12-01'),
  (4, 'Marketing digital desde cero', 'SEO, redes sociales y campañas de pago.', 'basico', 15, 59.0, 1, '2025-12-10'),
  (5, 'Sistemas de Gestión Empresarial', 'ERP y CRM: instalación y configuración.', 'intermedio', 18, 129.0, 0, '2026-01-10'),
  (1, 'Introducción a PHP y MySQL', 'Tu primera aplicación web con PHP y MySQL.', 'basico', 14, 49.99, 1, '2026-01-20');

INSERT INTO modulos (curso_id, titulo, descripcion, orden, obligatorio, fecha_creacion) VALUES
  (1, 'Fundamentos del lenguaje', 'Módulo 1 del curso «JAVA con Fabio».', 1, 1, '2025-10-02'),
  (1, 'Programación orientada a objetos', 'Módulo 2 del curso «JAVA con Fabio».', 2, 1, '2025-10-06'),
  (1, 'Colecciones y excepciones', 'Módulo 3 del curso «JAVA con Fabio».', 3, 1, '2025-10-10'),
  (2, 'Reading and Use of English', 'Módulo 1 del curso «Curso de Cambridge con Fabio».', 1, 1, '2025-10-06'),
  (2, 'Listening and Speaking', 'Módulo 2 del curso «Curso de Cambridge con Fabio».', 2, 1, '2025-10-10'),
  (2, 'Writing y simulacros', 'Módulo 3 del curso «Curso de Cambridge con Fabio».', 3, 1, '2025-10-14'),
  (3, 'Álgebra', 'Módulo 1 del curso «Matemáticas con Fabio».', 1, 1, '2025-10-11'),
  (3, 'Funciones y límites', 'Módulo 2 del curso «Matemáticas con Fabio».', 2, 1, '2025-10-15'),
  (3, 'Derivadas e integrales', 'Módulo 3 del curso «Matemáticas con Fabio».', 3, 1, '2025-10-19'),
  (4, 'Orígenes de la teoría cuántica', 'Módulo 1 del curso «Física cuántica».', 1, 1, '2025-10-16'),
  (4, 'La ecuación de Schrödinger', 'Módulo 2 del curso «Física cuántica».', 2, 1, '2025-10-20'),
  (4, 'Átomos y partículas', 'Módulo 3 del curso «Física cuántica».', 3, 0, '2025-10-24'),
  (5, 'El método científico', 'Módulo 1 del curso «Ciencias experimentales».', 1, 1, '2025-10-21'),
  (5, 'Prácticas de laboratorio', 'Módulo 2 del curso «Ciencias experimentales».', 2, 1, '2025-10-25'),
  (5, 'Análisis de resultados', 'Módulo 3 del curso «Ciencias experimentales».', 3, 1, '2025-10-29'),
  (6, 'Edad Antigua y Media', 'Módulo 1 del curso «Historia de España».', 1, 1, '2025-11-02'),
  (6, 'Edad Moderna', 'Módulo 2 del curso «Historia de España».', 2, 1, '2025-11-06'),
  (6, 'Edad Contemporánea', 'Módulo 3 del curso «Historia de España».', 3, 1, '2025-11-10'),
  (7, 'Ficheros y JDBC', 'Módulo 1 del curso «Acceso a Datos».', 1, 1, '2025-11-11'),
  (7, 'Mapeo objeto-relacional', 'Módulo 2 del curso «Acceso a Datos».', 2, 1, '2025-11-15'),
  (7, 'Bases de datos NoSQL', 'Módulo 3 del curso «Acceso a Datos».', 3, 1, '2025-11-19'),
  (8, 'Diseño de interfaces', 'Módulo 1 del curso «Desarrollo de Interfaces».', 1, 1, '2025-11-16'),
  (8, 'Componentes y eventos', 'Módulo 2 del curso «Desarrollo de Interfaces».', 2, 1, '2025-11-20'),
  (8, 'Informes y empaquetado', 'Módulo 3 del curso «Desarrollo de Interfaces».', 3, 0, '2025-11-24'),
  (9, 'Entorno y primeros pasos', 'Módulo 1 del curso «Programación de dispositivos móviles».', 1, 1, '2025-12-02'),
  (9, 'Interfaz y navegación', 'Módulo 2 del curso «Programación de dispositivos móviles».', 2, 1, '2025-12-06'),
  (9, 'Datos y servicios', 'Módulo 3 del curso «Programación de dispositivos móviles».', 3, 1, '2025-12-10'),
  (10, 'Fundamentos del marketing digital', 'Módulo 1 del curso «Marketing digital desde cero».', 1, 1, '2025-12-11'),
  (10, 'SEO y contenidos', 'Módulo 2 del curso «Marketing digital desde cero».', 2, 1, '2025-12-15'),
  (10, 'Redes sociales y publicidad', 'Módulo 3 del curso «Marketing digital desde cero».', 3, 1, '2025-12-19'),
  (11, 'Instalación de un ERP', 'Módulo 1 del curso «Sistemas de Gestión Empresarial».', 1, 1, '2026-01-11'),
  (11, 'Configuración y módulos', 'Módulo 2 del curso «Sistemas de Gestión Empresarial».', 2, 1, '2026-01-15'),
  (11, 'Informes y CRM', 'Módulo 3 del curso «Sistemas de Gestión Empresarial».', 3, 1, '2026-01-19'),
  (12, 'Sintaxis de PHP', 'Módulo 1 del curso «Introducción a PHP y MySQL».', 1, 1, '2026-01-21'),
  (12, 'MySQL y SQL', 'Módulo 2 del curso «Introducción a PHP y MySQL».', 2, 1, '2026-01-25'),
  (12, 'Aplicación CRUD completa', 'Módulo 3 del curso «Introducción a PHP y MySQL».', 3, 0, '2026-01-29');

INSERT INTO lecciones (modulo_id, titulo, tipo_recurso, recurso_url, duracion_min, orden, gratuita, fecha_publicacion) VALUES
  (1, 'Fundamentos del lenguaje: introducción', 'video', 'https://www.youtube.com/watch?v=demo011', 28, 1, 1, '2025-10-06'),
  (1, 'Fundamentos del lenguaje: ejemplos prácticos', 'video', 'https://www.youtube.com/watch?v=demo012', 43, 2, 0, '2025-10-07'),
  (1, 'Fundamentos del lenguaje: ejercicios', 'pdf', 'https://learnova.example/recursos/curso01/modulo1/ejercicios.pdf', 23, 3, 0, '2025-10-08'),
  (2, 'Programación orientada a objetos: introducción', 'video', 'https://www.youtube.com/watch?v=demo021', 33, 1, 0, '2025-10-10'),
  (2, 'Programación orientada a objetos: ejemplos prácticos', 'video', 'https://www.youtube.com/watch?v=demo022', 48, 2, 0, '2025-10-11'),
  (2, 'Programación orientada a objetos: ejercicios', 'pdf', 'https://learnova.example/recursos/curso01/modulo2/ejercicios.pdf', 28, 3, 0, '2025-10-12'),
  (3, 'Colecciones y excepciones: introducción', 'video', 'https://www.youtube.com/watch?v=demo031', 38, 1, 0, '2025-10-14'),
  (3, 'Colecciones y excepciones: ejemplos prácticos', 'video', 'https://www.youtube.com/watch?v=demo032', 53, 2, 0, '2025-10-15'),
  (3, 'Colecciones y excepciones: ejercicios', 'pdf', 'https://learnova.example/recursos/curso01/modulo3/ejercicios.pdf', 33, 3, 0, '2025-10-16'),
  (4, 'Reading and Use of English: introducción', 'video', 'https://www.youtube.com/watch?v=demo041', 31, 1, 1, '2025-10-10'),
  (4, 'Reading and Use of English: ejemplos prácticos', 'video', 'https://www.youtube.com/watch?v=demo042', 46, 2, 0, '2025-10-11'),
  (4, 'Reading and Use of English: ejercicios', 'pdf', 'https://learnova.example/recursos/curso02/modulo1/ejercicios.pdf', 26, 3, 0, '2025-10-12'),
  (5, 'Listening and Speaking: introducción', 'video', 'https://www.youtube.com/watch?v=demo051', 36, 1, 0, '2025-10-14'),
  (5, 'Listening and Speaking: ejemplos prácticos', 'video', 'https://www.youtube.com/watch?v=demo052', 51, 2, 0, '2025-10-15'),
  (5, 'Listening and Speaking: ejercicios', 'pdf', 'https://learnova.example/recursos/curso02/modulo2/ejercicios.pdf', 31, 3, 0, '2025-10-16'),
  (6, 'Writing y simulacros: introducción', 'video', 'https://www.youtube.com/watch?v=demo061', 41, 1, 0, '2025-10-18'),
  (6, 'Writing y simulacros: ejemplos prácticos', 'video', 'https://www.youtube.com/watch?v=demo062', 56, 2, 0, '2025-10-19'),
  (6, 'Writing y simulacros: ejercicios', 'pdf', 'https://learnova.example/recursos/curso02/modulo3/ejercicios.pdf', 36, 3, 0, '2025-10-20'),
  (7, 'Álgebra: introducción', 'video', 'https://www.youtube.com/watch?v=demo071', 34, 1, 1, '2025-10-15'),
  (7, 'Álgebra: ejemplos prácticos', 'video', 'https://www.youtube.com/watch?v=demo072', 49, 2, 0, '2025-10-16'),
  (7, 'Álgebra: ejercicios', 'pdf', 'https://learnova.example/recursos/curso03/modulo1/ejercicios.pdf', 29, 3, 0, '2025-10-17'),
  (8, 'Funciones y límites: introducción', 'video', 'https://www.youtube.com/watch?v=demo081', 39, 1, 0, '2025-10-19'),
  (8, 'Funciones y límites: ejemplos prácticos', 'video', 'https://www.youtube.com/watch?v=demo082', 54, 2, 0, '2025-10-20'),
  (8, 'Funciones y límites: ejercicios', 'pdf', 'https://learnova.example/recursos/curso03/modulo2/ejercicios.pdf', 34, 3, 0, '2025-10-21'),
  (9, 'Derivadas e integrales: introducción', 'video', 'https://www.youtube.com/watch?v=demo091', 44, 1, 0, '2025-10-23'),
  (9, 'Derivadas e integrales: ejemplos prácticos', 'video', 'https://www.youtube.com/watch?v=demo092', 59, 2, 0, '2025-10-24'),
  (9, 'Derivadas e integrales: ejercicios', 'pdf', 'https://learnova.example/recursos/curso03/modulo3/ejercicios.pdf', 39, 3, 0, '2025-10-25'),
  (10, 'Orígenes de la teoría cuántica: introducción', 'video', 'https://www.youtube.com/watch?v=demo101', 37, 1, 1, '2025-10-20'),
  (10, 'Orígenes de la teoría cuántica: ejemplos prácticos', 'video', 'https://www.youtube.com/watch?v=demo102', 52, 2, 0, '2025-10-21'),
  (10, 'Orígenes de la teoría cuántica: ejercicios', 'pdf', 'https://learnova.example/recursos/curso04/modulo1/ejercicios.pdf', 32, 3, 0, '2025-10-22'),
  (11, 'La ecuación de Schrödinger: introducción', 'video', 'https://www.youtube.com/watch?v=demo111', 42, 1, 0, '2025-10-24'),
  (11, 'La ecuación de Schrödinger: ejemplos prácticos', 'video', 'https://www.youtube.com/watch?v=demo112', 57, 2, 0, '2025-10-25'),
  (11, 'La ecuación de Schrödinger: ejercicios', 'pdf', 'https://learnova.example/recursos/curso04/modulo2/ejercicios.pdf', 37, 3, 0, '2025-10-26'),
  (12, 'Átomos y partículas: introducción', 'video', 'https://www.youtube.com/watch?v=demo121', 22, 1, 0, '2025-10-28'),
  (12, 'Átomos y partículas: ejemplos prácticos', 'video', 'https://www.youtube.com/watch?v=demo122', 37, 2, 0, '2025-10-29'),
  (12, 'Átomos y partículas: ejercicios', 'pdf', 'https://learnova.example/recursos/curso04/modulo3/ejercicios.pdf', 17, 3, 0, '2025-10-30'),
  (13, 'El método científico: introducción', 'video', 'https://www.youtube.com/watch?v=demo131', 40, 1, 1, '2025-10-25'),
  (13, 'El método científico: ejemplos prácticos', 'video', 'https://www.youtube.com/watch?v=demo132', 55, 2, 0, '2025-10-26'),
  (13, 'El método científico: ejercicios', 'pdf', 'https://learnova.example/recursos/curso05/modulo1/ejercicios.pdf', 35, 3, 0, '2025-10-27'),
  (14, 'Prácticas de laboratorio: introducción', 'video', 'https://www.youtube.com/watch?v=demo141', 20, 1, 0, '2025-10-29'),
  (14, 'Prácticas de laboratorio: ejemplos prácticos', 'video', 'https://www.youtube.com/watch?v=demo142', 35, 2, 0, '2025-10-30'),
  (14, 'Prácticas de laboratorio: ejercicios', 'pdf', 'https://learnova.example/recursos/curso05/modulo2/ejercicios.pdf', 15, 3, 0, '2025-10-31'),
  (15, 'Análisis de resultados: introducción', 'video', 'https://www.youtube.com/watch?v=demo151', 25, 1, 0, '2025-11-02'),
  (15, 'Análisis de resultados: ejemplos prácticos', 'video', 'https://www.youtube.com/watch?v=demo152', 40, 2, 0, '2025-11-03'),
  (15, 'Análisis de resultados: ejercicios', 'pdf', 'https://learnova.example/recursos/curso05/modulo3/ejercicios.pdf', 20, 3, 0, '2025-11-04'),
  (16, 'Edad Antigua y Media: introducción', 'video', 'https://www.youtube.com/watch?v=demo161', 43, 1, 1, '2025-11-06'),
  (16, 'Edad Antigua y Media: ejemplos prácticos', 'video', 'https://www.youtube.com/watch?v=demo162', 58, 2, 0, '2025-11-07'),
  (16, 'Edad Antigua y Media: ejercicios', 'pdf', 'https://learnova.example/recursos/curso06/modulo1/ejercicios.pdf', 38, 3, 0, '2025-11-08'),
  (17, 'Edad Moderna: introducción', 'video', 'https://www.youtube.com/watch?v=demo171', 23, 1, 0, '2025-11-10'),
  (17, 'Edad Moderna: ejemplos prácticos', 'video', 'https://www.youtube.com/watch?v=demo172', 38, 2, 0, '2025-11-11'),
  (17, 'Edad Moderna: ejercicios', 'pdf', 'https://learnova.example/recursos/curso06/modulo2/ejercicios.pdf', 18, 3, 0, '2025-11-12'),
  (18, 'Edad Contemporánea: introducción', 'video', 'https://www.youtube.com/watch?v=demo181', 28, 1, 0, '2025-11-14'),
  (18, 'Edad Contemporánea: ejemplos prácticos', 'video', 'https://www.youtube.com/watch?v=demo182', 43, 2, 0, '2025-11-15'),
  (18, 'Edad Contemporánea: ejercicios', 'pdf', 'https://learnova.example/recursos/curso06/modulo3/ejercicios.pdf', 23, 3, 0, '2025-11-16'),
  (19, 'Ficheros y JDBC: introducción', 'video', 'https://www.youtube.com/watch?v=demo191', 21, 1, 1, '2025-11-15'),
  (19, 'Ficheros y JDBC: ejemplos prácticos', 'video', 'https://www.youtube.com/watch?v=demo192', 36, 2, 0, '2025-11-16'),
  (19, 'Ficheros y JDBC: ejercicios', 'pdf', 'https://learnova.example/recursos/curso07/modulo1/ejercicios.pdf', 16, 3, 0, '2025-11-17'),
  (20, 'Mapeo objeto-relacional: introducción', 'video', 'https://www.youtube.com/watch?v=demo201', 26, 1, 0, '2025-11-19'),
  (20, 'Mapeo objeto-relacional: ejemplos prácticos', 'video', 'https://www.youtube.com/watch?v=demo202', 41, 2, 0, '2025-11-20'),
  (20, 'Mapeo objeto-relacional: ejercicios', 'pdf', 'https://learnova.example/recursos/curso07/modulo2/ejercicios.pdf', 21, 3, 0, '2025-11-21'),
  (21, 'Bases de datos NoSQL: introducción', 'video', 'https://www.youtube.com/watch?v=demo211', 31, 1, 0, '2025-11-23'),
  (21, 'Bases de datos NoSQL: ejemplos prácticos', 'video', 'https://www.youtube.com/watch?v=demo212', 46, 2, 0, '2025-11-24'),
  (21, 'Bases de datos NoSQL: ejercicios', 'pdf', 'https://learnova.example/recursos/curso07/modulo3/ejercicios.pdf', 26, 3, 0, '2025-11-25'),
  (22, 'Diseño de interfaces: introducción', 'video', 'https://www.youtube.com/watch?v=demo221', 24, 1, 1, '2025-11-20'),
  (22, 'Diseño de interfaces: ejemplos prácticos', 'video', 'https://www.youtube.com/watch?v=demo222', 39, 2, 0, '2025-11-21'),
  (22, 'Diseño de interfaces: ejercicios', 'pdf', 'https://learnova.example/recursos/curso08/modulo1/ejercicios.pdf', 19, 3, 0, '2025-11-22'),
  (23, 'Componentes y eventos: introducción', 'video', 'https://www.youtube.com/watch?v=demo231', 29, 1, 0, '2025-11-24'),
  (23, 'Componentes y eventos: ejemplos prácticos', 'video', 'https://www.youtube.com/watch?v=demo232', 44, 2, 0, '2025-11-25'),
  (23, 'Componentes y eventos: ejercicios', 'pdf', 'https://learnova.example/recursos/curso08/modulo2/ejercicios.pdf', 24, 3, 0, '2025-11-26'),
  (24, 'Informes y empaquetado: introducción', 'video', 'https://www.youtube.com/watch?v=demo241', 34, 1, 0, '2025-11-28'),
  (24, 'Informes y empaquetado: ejemplos prácticos', 'video', 'https://www.youtube.com/watch?v=demo242', 49, 2, 0, '2025-11-29'),
  (24, 'Informes y empaquetado: ejercicios', 'pdf', 'https://learnova.example/recursos/curso08/modulo3/ejercicios.pdf', 29, 3, 0, '2025-11-30'),
  (25, 'Entorno y primeros pasos: introducción', 'video', 'https://www.youtube.com/watch?v=demo251', 27, 1, 1, '2025-12-06'),
  (25, 'Entorno y primeros pasos: ejemplos prácticos', 'video', 'https://www.youtube.com/watch?v=demo252', 42, 2, 0, '2025-12-07'),
  (25, 'Entorno y primeros pasos: ejercicios', 'pdf', 'https://learnova.example/recursos/curso09/modulo1/ejercicios.pdf', 22, 3, 0, '2025-12-08'),
  (26, 'Interfaz y navegación: introducción', 'video', 'https://www.youtube.com/watch?v=demo261', 32, 1, 0, '2025-12-10'),
  (26, 'Interfaz y navegación: ejemplos prácticos', 'video', 'https://www.youtube.com/watch?v=demo262', 47, 2, 0, '2025-12-11'),
  (26, 'Interfaz y navegación: ejercicios', 'pdf', 'https://learnova.example/recursos/curso09/modulo2/ejercicios.pdf', 27, 3, 0, '2025-12-12'),
  (27, 'Datos y servicios: introducción', 'video', 'https://www.youtube.com/watch?v=demo271', 37, 1, 0, '2025-12-14'),
  (27, 'Datos y servicios: ejemplos prácticos', 'video', 'https://www.youtube.com/watch?v=demo272', 52, 2, 0, '2025-12-15'),
  (27, 'Datos y servicios: ejercicios', 'pdf', 'https://learnova.example/recursos/curso09/modulo3/ejercicios.pdf', 32, 3, 0, '2025-12-16'),
  (28, 'Fundamentos del marketing digital: introducción', 'video', 'https://www.youtube.com/watch?v=demo281', 30, 1, 1, '2025-12-15'),
  (28, 'Fundamentos del marketing digital: ejemplos prácticos', 'video', 'https://www.youtube.com/watch?v=demo282', 45, 2, 0, '2025-12-16'),
  (28, 'Fundamentos del marketing digital: ejercicios', 'pdf', 'https://learnova.example/recursos/curso10/modulo1/ejercicios.pdf', 25, 3, 0, '2025-12-17'),
  (29, 'SEO y contenidos: introducción', 'video', 'https://www.youtube.com/watch?v=demo291', 35, 1, 0, '2025-12-19'),
  (29, 'SEO y contenidos: ejemplos prácticos', 'video', 'https://www.youtube.com/watch?v=demo292', 50, 2, 0, '2025-12-20'),
  (29, 'SEO y contenidos: ejercicios', 'pdf', 'https://learnova.example/recursos/curso10/modulo2/ejercicios.pdf', 30, 3, 0, '2025-12-21'),
  (30, 'Redes sociales y publicidad: introducción', 'video', 'https://www.youtube.com/watch?v=demo301', 40, 1, 0, '2025-12-23'),
  (30, 'Redes sociales y publicidad: ejemplos prácticos', 'video', 'https://www.youtube.com/watch?v=demo302', 55, 2, 0, '2025-12-24'),
  (30, 'Redes sociales y publicidad: ejercicios', 'pdf', 'https://learnova.example/recursos/curso10/modulo3/ejercicios.pdf', 35, 3, 0, '2025-12-25'),
  (31, 'Instalación de un ERP: introducción', 'video', 'https://www.youtube.com/watch?v=demo311', 33, 1, 1, '2026-01-15'),
  (31, 'Instalación de un ERP: ejemplos prácticos', 'video', 'https://www.youtube.com/watch?v=demo312', 48, 2, 0, '2026-01-16'),
  (31, 'Instalación de un ERP: ejercicios', 'pdf', 'https://learnova.example/recursos/curso11/modulo1/ejercicios.pdf', 28, 3, 0, '2026-01-17'),
  (32, 'Configuración y módulos: introducción', 'video', 'https://www.youtube.com/watch?v=demo321', 38, 1, 0, '2026-01-19'),
  (32, 'Configuración y módulos: ejemplos prácticos', 'video', 'https://www.youtube.com/watch?v=demo322', 53, 2, 0, '2026-01-20'),
  (32, 'Configuración y módulos: ejercicios', 'pdf', 'https://learnova.example/recursos/curso11/modulo2/ejercicios.pdf', 33, 3, 0, '2026-01-21'),
  (33, 'Informes y CRM: introducción', 'video', 'https://www.youtube.com/watch?v=demo331', 43, 1, 0, '2026-01-23'),
  (33, 'Informes y CRM: ejemplos prácticos', 'video', 'https://www.youtube.com/watch?v=demo332', 58, 2, 0, '2026-01-24'),
  (33, 'Informes y CRM: ejercicios', 'pdf', 'https://learnova.example/recursos/curso11/modulo3/ejercicios.pdf', 38, 3, 0, '2026-01-25'),
  (34, 'Sintaxis de PHP: introducción', 'video', 'https://www.youtube.com/watch?v=demo341', 36, 1, 1, '2026-01-25'),
  (34, 'Sintaxis de PHP: ejemplos prácticos', 'video', 'https://www.youtube.com/watch?v=demo342', 51, 2, 0, '2026-01-26'),
  (34, 'Sintaxis de PHP: ejercicios', 'pdf', 'https://learnova.example/recursos/curso12/modulo1/ejercicios.pdf', 31, 3, 0, '2026-01-27'),
  (35, 'MySQL y SQL: introducción', 'video', 'https://www.youtube.com/watch?v=demo351', 41, 1, 0, '2026-01-29'),
  (35, 'MySQL y SQL: ejemplos prácticos', 'video', 'https://www.youtube.com/watch?v=demo352', 56, 2, 0, '2026-01-30'),
  (35, 'MySQL y SQL: ejercicios', 'pdf', 'https://learnova.example/recursos/curso12/modulo2/ejercicios.pdf', 36, 3, 0, '2026-01-31'),
  (36, 'Aplicación CRUD completa: introducción', 'video', 'https://www.youtube.com/watch?v=demo361', 21, 1, 0, '2026-02-02'),
  (36, 'Aplicación CRUD completa: ejemplos prácticos', 'video', 'https://www.youtube.com/watch?v=demo362', 36, 2, 0, '2026-02-03'),
  (36, 'Aplicación CRUD completa: ejercicios', 'pdf', 'https://learnova.example/recursos/curso12/modulo3/ejercicios.pdf', 16, 3, 0, '2026-02-04');

SET FOREIGN_KEY_CHECKS = 1;
