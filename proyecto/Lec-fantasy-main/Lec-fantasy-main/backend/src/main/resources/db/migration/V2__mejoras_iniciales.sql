-- Catálogo inicial de mejoras. Si la condición se cumple en la jornada,
-- los puntos del jugador se multiplican por multiplicador_bono.
-- Si no se cumple, la mejora falla y se pierde lo pagado.
INSERT INTO mejora (nombre, descripcion, precio, multiplicador_bono, condicion, umbral, activa) VALUES
('Triple Kill',   'El jugador consigue al menos una triple kill en la jornada',          1500000.00, 2, 'TRIPLE_KILL',  NULL, TRUE),
('Quadra Kill',   'El jugador consigue al menos una quadra kill en la jornada',          1000000.00, 3, 'QUADRA_KILL',  NULL, TRUE),
('Penta Kill',    'El jugador consigue una pentakill en la jornada',                      500000.00, 5, 'PENTA_KILL',   NULL, TRUE),
('First Blood',   'El jugador participa en la primera sangre de alguna partida',         1000000.00, 2, 'FIRST_BLOOD',  NULL, TRUE),
('MVP',           'El jugador es el MVP de alguna partida de la jornada',                2000000.00, 2, 'MVP',          NULL, TRUE),
('Inmortal',      'El jugador no muere en ninguna partida de la jornada',                1500000.00, 2, 'CERO_MUERTES', NULL, TRUE),
('KDA de élite',  'El KDA del jugador en la jornada es igual o superior al umbral',      1200000.00, 2, 'KDA_MINIMO',   5.00, TRUE),
('Invicto',       'El equipo del jugador gana todas sus partidas de la jornada',          800000.00, 2, 'VICTORIAS',    NULL, TRUE);
