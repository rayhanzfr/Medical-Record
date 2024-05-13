CREATE TABLE pro_patients(
    id text NOT NULL DEFAULT uuid_generate_v4(),
    name text NOT NULL,
    birth_date date NOT NULL,
    email text NOT NULL,
    phone text NOT NULL,
    created_by text NOT NULL DEFAULT 'SYSTEM'::text,
    created_at timestamp with time zone NOT NULL DEFAULT now(),
    updated_by text NOT NULL DEFAULT 'SYSTEM'::text,
    updated_at timestamp with time zone NOT NULL DEFAULT now(),
    "version" int8 NOT NULL DEFAULT 0,
    is_active bool DEFAULT true,
    deleted_at timestamp with time zone NULL,
    CONSTRAINT pro_patients_pk PRIMARY KEY (id)
);
CREATE UNIQUE INDEX pro_patients_un ON pro_patients (name,email)
    WHERE deleted_at IS NULL;

CREATE TABLE pro_medical_records(
    id text NOT NULL DEFAULT uuid_generate_v4(),
    patient_id text not null
        CONSTRAINT pro_medical_records_fk_pro_patients REFERENCES pro_patients(id),
    recommendation_medic text,
    created_by text NOT NULL DEFAULT 'SYSTEM'::text,
    created_at timestamp with time zone NOT NULL DEFAULT now(),
    updated_by text NOT NULL DEFAULT 'SYSTEM'::text,
    updated_at timestamp with time zone NOT NULL DEFAULT now(),
    "version" int8 NOT NULL DEFAULT 0,
    is_active bool DEFAULT true,
    deleted_at timestamp with time zone NULL,
    CONSTRAINT pro_medical_records_pk PRIMARY KEY (id)
);
CREATE TABLE pro_diagnoses(
    id text NOT NULL DEFAULT uuid_generate_v4(),
    medical_record_id text not null
      CONSTRAINT pro_diagnoses_fk_pro_medical_records REFERENCES pro_medical_records(id),
    code text not null,
    description text not null,
    created_by text NOT NULL DEFAULT 'SYSTEM'::text,
    created_at timestamp with time zone NOT NULL DEFAULT now(),
    updated_by text NOT NULL DEFAULT 'SYSTEM'::text,
    updated_at timestamp with time zone NOT NULL DEFAULT now(),
    "version" int8 NOT NULL DEFAULT 0,
    is_active bool DEFAULT true,
    deleted_at timestamp with time zone NULL,
    CONSTRAINT pro_diagnoses_pk PRIMARY KEY (id)
);
CREATE UNIQUE INDEX pro_diagnoses_un ON pro_diagnoses (medical_record_id,code)
    WHERE deleted_at IS NULL;

CREATE TABLE pro_recipe_medicines(
    id text NOT NULL DEFAULT public.uuid_generate_v4(),
    medical_record_id text not null
        CONSTRAINT pro_recipe_medicines_fk_pro_medical_records REFERENCES pro_medical_records(id),
    name text NOT NULL,
    kfa_code text NOT NULL,
    dose_per_unit integer NOT NULL,
    created_by text NOT NULL DEFAULT 'SYSTEM'::text,
    created_at timestamp with time zone NOT NULL DEFAULT now(),
    updated_by text NOT NULL DEFAULT 'SYSTEM'::text,
    updated_at timestamp with time zone NOT NULL DEFAULT now(),
    "version" int8 NOT NULL DEFAULT 0,
    is_active bool DEFAULT true,
    deleted_at timestamp with time zone NULL,
    CONSTRAINT pro_recipe_medicines_pk PRIMARY KEY (id)
);
CREATE UNIQUE INDEX pro_recipe_medicines_un ON pro_recipe_medicines (medical_record_id,kfa_code)
    WHERE deleted_at IS NULL;