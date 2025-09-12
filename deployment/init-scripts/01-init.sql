CREATE EXTENSION IF NOT EXISTS "pgcrypto";
-- Estados
CREATE TABLE IF NOT EXISTS public.estados (
     id_estado SMALLINT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    descripcion TEXT
);

CREATE SEQUENCE IF NOT EXISTS public.estados_id_estado_seq
    AS SMALLINT
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER SEQUENCE public.estados_id_estado_seq OWNED BY public.estados.id_estado;
ALTER TABLE ONLY public.estados ALTER COLUMN id_estado SET DEFAULT nextval('public.estados_id_estado_seq'::regclass);

CREATE TABLE IF NOT EXISTS public.tipo_prestamos (
    id_tipo_prestamo UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nombre VARCHAR(150) NOT NULL UNIQUE,
    monto_minimo NUMERIC(15,2) NOT NULL,
    monto_maximo NUMERIC(15,2) NOT NULL,
    tasa_interes NUMERIC(5,2) NOT NULL,
    validacion_automatica BOOLEAN DEFAULT FALSE NOT NULL
);

CREATE TABLE IF NOT EXISTS public.solicitudes (
    id_solicitud UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    id_cliente UUID NOT NULL,
    id_estado SMALLINT NOT NULL,
    monto NUMERIC(15,2) NOT NULL,
    plazo_en_meses INTEGER NOT NULL,
    id_tipo_prestamo UUID NOT NULL,
    CONSTRAINT fk_estado FOREIGN KEY (id_estado) REFERENCES public.estados(id_estado) ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_tipo_prestamo FOREIGN KEY (id_tipo_prestamo) REFERENCES public.tipo_prestamos(id_tipo_prestamo) ON UPDATE CASCADE ON DELETE RESTRICT
);

-- Insert initial data
INSERT INTO public.estados (id_estado, nombre, descripcion) VALUES
    (1, 'PENDIENTE_REVISION', 'Aún no se procesa'),
    (2, 'RECHAZADA', 'Solicitud denegada'),
    (3, 'REVISION_MANUAL', 'Revisión por asesor');

INSERT INTO public.tipo_prestamos (id_tipo_prestamo, nombre, monto_minimo, monto_maximo, tasa_interes, validacion_automatica) VALUES
    ('06b65645-d989-4a22-b474-a57e28aec1ac', 'LIBRE_INVERSION', 1000000.00, 80000000.00, 15.50, TRUE),
    ('defa8c68-d223-4282-9be4-f072be4613a6', 'HIPOTECARIO', 20000000.00, 500000000.00, 10.20, FALSE),
    ('1c5a0c6e-500e-42da-bae0-e51e44f49590', 'VEHICULO', 5000000.00, 150000000.00, 12.80, TRUE),
    ('b6f46273-0ea5-42cf-b286-6ede3a3a9a3c', 'EDUCACION', 2000000.00, 50000000.00, 8.50, TRUE),
    ('28194020-8f60-4609-b0ac-cbf3331b8a1c', 'NOMINA', 500000.00, 60000000.00, 14.00, TRUE),
    ('483bb393-ea4c-45c8-a83b-48d68a5d8e0c', 'ROTATIVO', 1000000.00, 40000000.00, 18.00, TRUE),
    ('8ee407ff-898e-4375-9f3a-e5f57e6662b8', 'MICROCREDITO', 500000.00, 20000000.00, 22.00, FALSE),
    ('f540304d-bfe7-4b1f-aa36-6b5ac4c7c526', 'LIBRE_EMPRESARIAL', 10000000.00, 300000000.00, 16.00, FALSE);