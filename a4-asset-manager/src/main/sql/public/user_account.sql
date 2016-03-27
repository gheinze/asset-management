    DO $$
    BEGIN

        IF NOT EXISTS(
            SELECT 1
              FROM information_schema.tables
              WHERE table_schema = 'public'
                AND table_name = 'user_account'
        ) THEN

            DROP TYPE IF EXISTS public.user_account_limited CASCADE;
            DROP TYPE IF EXISTS public.user_account_status CASCADE;


            CREATE TYPE public.user_account_status AS ENUM('ACTIVE', 'LOCKED', 'RETIRED');

            CREATE TYPE public.user_account_limited AS (
               id            bigint
              ,version       integer
              ,name          character varying(32)
              ,status        user_account_status
              ,tenant        VARCHAR(64)
              ,display_name  character varying(64)
              ,email         character varying(64)
            );


            CREATE TABLE public.user_account(
              name               VARCHAR(32)  NOT NULL CONSTRAINT user_account_name UNIQUE
             ,encrypted_password TEXT         NOT NULL
             ,status             user_account_status  NOT NULL DEFAULT 'ACTIVE'::user_account_status
             ,tenant             VARCHAR(64)  NOT NULL
             ,display_name       VARCHAR(64)  NOT NULL
             ,email              VARCHAR(64)  NOT NULL
            ) INHERITS(base)
            ;

            COMMENT ON TABLE public.user_account IS 'All operations performed in the application are associated with a "user_account" (a data owner). The user must authenticate in order to use system services and to have access to data associated with the account.';

            COMMENT ON COLUMN public.user_account.name IS 'The name of the user account used for login purposes';
            COMMENT ON COLUMN public.user_account.encrypted_password IS 'The encrypted password used to log into this user account.';
            COMMENT ON COLUMN public.user_account.status IS 'The status of an account will affect the operations it may perform. An account my be "ACTIVE", "LOCKED", "RETIRED", etc';
            COMMENT ON COLUMN public.user_account.display_name IS 'The name to use for display or greeting purposes.';
            COMMENT ON COLUMN public.user_account.email IS 'An address to which notifications may be sent.';


            PERFORM ist_pk('user_account');
            PERFORM ist_bk('user_account', ARRAY['name']);

            INSERT INTO user_account(name, encrypted_password, tenant, display_name, email)
              VALUES ('gheinze', pgcrypto.crypt( concat('gheinze', 'admin'), pgcrypto.gen_salt('bf')), 'tenant_0', 'Administrator', 'a4.admin@gheinze.com');

            INSERT INTO user_account(name, encrypted_password, tenant, display_name, email)
              VALUES ('kimamura', pgcrypto.crypt( concat('kimamura', 'admin'), pgcrypto.gen_salt('bf')), 'tenant_1', 'Administrator', 'a4.admin@gheinze.com');

        END IF;

    END
    $$;
