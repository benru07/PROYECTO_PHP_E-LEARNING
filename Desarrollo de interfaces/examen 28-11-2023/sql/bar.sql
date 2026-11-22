-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Servidor: db
-- Tiempo de generación: 28-11-2023 a las 01:23:01
-- Versión del servidor: 8.1.0
-- Versión de PHP: 8.2.8

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de datos: `bar`
--
CREATE DATABASE IF NOT EXISTS `bar` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_spanish_ci;
USE `bar`;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `pedidos`
--

CREATE TABLE `pedidos` (
  `idpedido` int NOT NULL,
  `cliente` varchar(50) COLLATE utf8mb4_spanish_ci NOT NULL,
  `idplato` int NOT NULL,
  `unidades` int NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_spanish_ci;

--
-- Volcado de datos para la tabla `pedidos`
--

INSERT INTO `pedidos` (`idpedido`, `cliente`, `idplato`, `unidades`) VALUES
(1, 'Paco', 40, 2),
(2, 'Juan', 36, 2),
(3, 'Susana', 34, 3),
(4, 'Paco', 37, 1),
(5, 'Juan', 38, 2),
(6, 'Susana', 35, 1),
(7, 'Susana', 45, 2),
(8, 'Paco ', 39, 1),
(9, 'Gloria', 43, 3),
(10, 'Gloria', 44, 2),
(11, 'Juan', 38, 2);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `platos`
--

CREATE TABLE `platos` (
  `idplato` int NOT NULL,
  `nombre` varchar(50) COLLATE utf8mb4_spanish_ci NOT NULL,
  `descripcion` varchar(200) COLLATE utf8mb4_spanish_ci NOT NULL,
  `precio` decimal(8,2) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_spanish_ci;

--
-- Volcado de datos para la tabla `platos`
--

INSERT INTO `platos` (`idplato`, `nombre`, `descripcion`, `precio`) VALUES
(34, 'SERRANITO', 'Bocadillo de origen sevillano.', 5.50),
(35, 'JAMÓN IBÉRICO 7 JOTAS ', 'Jamón ibérico de bellota de calidad excepcional 7 bellotas.', 20.00),
(36, 'GAMBAS', 'GAMBAS BLANCAS DE HUELVA COCIDAS EN AGUA DE MAR', 10.00),
(37, 'PATATAS BRAVAS', 'Patatas cortadas en forma cúbica con salsa brava', 3.50),
(38, 'SOLOMILLO AL WHISKEY', 'Solomillo de cerdo ibérico en la salsa sevillana al whiskey', 3.00),
(39, 'GARBANZOS CON ESPINACAS', 'Guiso sevillano de garbanzos con espinacas', 2.50),
(40, 'BACALAO CON TOMATE', 'Clásico sevillano para la época de cuaresma, el famoso bacalao con tomate', 4.00),
(41, 'MONTADITO DE PRINGÁ', 'Auténtico montadito sevillano de pringá con chorizo, morcilla y carne de cerdo y ternera.', 2.75),
(42, 'CARACOLES', 'Caracoles, un plato no apto para guiris.', 2.50),
(43, 'CABRILLAS EN TOMATE', 'Cabrillas en salsa de tomate, como los caracoles, pero más grandes.', 3.50),
(44, 'PAVÍAS DE BACALAO', 'Como decía El Pali monstrandosu espíritu pacifista y afición a este plato \"Menos misiles y más pavías de bacalao\" ', 4.50),
(45, 'PAPAS ALIÑÁS', 'Patatas cultivadas en terrenos arenosos de Sanlúcar de Barrameda, cocidas y aliñadas con aceite de oliva virgen extra de Andalucía.', 2.50);

--
-- Índices para tablas volcadas
--

--
-- Indices de la tabla `pedidos`
--
ALTER TABLE `pedidos`
  ADD PRIMARY KEY (`idpedido`),
  ADD KEY `FK_PLATOS` (`idplato`);

--
-- Indices de la tabla `platos`
--
ALTER TABLE `platos`
  ADD PRIMARY KEY (`idplato`);

--
-- AUTO_INCREMENT de las tablas volcadas
--

--
-- AUTO_INCREMENT de la tabla `pedidos`
--
ALTER TABLE `pedidos`
  MODIFY `idpedido` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=12;

--
-- AUTO_INCREMENT de la tabla `platos`
--
ALTER TABLE `platos`
  MODIFY `idplato` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=57;

--
-- Restricciones para tablas volcadas
--

--
-- Filtros para la tabla `pedidos`
--
ALTER TABLE `pedidos`
  ADD CONSTRAINT `FK_PLATOS` FOREIGN KEY (`idplato`) REFERENCES `platos` (`idplato`) ON DELETE RESTRICT ON UPDATE RESTRICT;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
