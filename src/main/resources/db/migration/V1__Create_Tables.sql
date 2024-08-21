CREATE TABLE image(
    id BIGSERIAL PRIMARY KEY,
    url TEXT NOT NULL,
    label TEXT NOT NULL,
    object_recognition_enabled BOOLEAN NOT NULL DEFAULT false
);

CREATE TABLE stored_object(
    id BIGSERIAL PRIMARY KEY,
    name TEXT NOT NULL UNIQUE
);

CREATE TABLE stored_object_reference(
    image_id BIGINT NOT NULL REFERENCES image ON DELETE CASCADE,
    stored_object_id BIGINT NOT NULL REFERENCES stored_object ON DELETE CASCADE,
    PRIMARY KEY (image_id, stored_object_id)
);

CREATE INDEX sto_obj_ref_obj_index on stored_object_reference (stored_object_id);