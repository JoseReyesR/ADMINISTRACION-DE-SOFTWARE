USE reclamos_db;

-- Tabla para la bitácora de seguimiento (Timeline)
CREATE TABLE historial_seguimientos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    reclamo_id INT NOT NULL,
    usuario_id_responsable INT NOT NULL, -- Identifica quién escribió la nota o hizo el cambio
    estado_anterior_id INT, -- Puede ser nulo si solo es un comentario sin cambiar de estado
    estado_nuevo_id INT,    -- Puede ser nulo si solo es un comentario
    comentario TEXT NOT NULL,
    es_interno BOOLEAN DEFAULT FALSE, -- TRUE = Nota privada BackOffice | FALSE = Visible para el cliente
    fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    -- Relaciones (Llaves foráneas)
    FOREIGN KEY (reclamo_id) REFERENCES reclamos(id) ON DELETE CASCADE,
    FOREIGN KEY (usuario_id_responsable) REFERENCES usuarios(id),
    FOREIGN KEY (estado_anterior_id) REFERENCES estados_reclamo(id),
    FOREIGN KEY (estado_nuevo_id) REFERENCES estados_reclamo(id)
);