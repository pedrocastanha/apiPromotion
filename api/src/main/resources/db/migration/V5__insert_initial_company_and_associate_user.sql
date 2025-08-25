DO $$
DECLARE
    new_company_id INTEGER;
BEGIN
    INSERT INTO company (name, type, description)
    VALUES ('Castanha''s Espetinhos', 'Restaurante', NULL)
    RETURNING id INTO new_company_id;

    UPDATE users
    SET company_id = new_company_id
    WHERE id = 1;
END $$;