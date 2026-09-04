CREATE TABLE roles (
                       id BINARY(16) NOT NULL,
                       created_at DATETIME(6) NOT NULL,
                       updated_at DATETIME(6) NOT NULL,
                       name VARCHAR(50) NOT NULL,

                       PRIMARY KEY (id),
                       CONSTRAINT uk_roles_name UNIQUE (name)
) ENGINE=InnoDB
DEFAULT CHARSET=utf8mb4
COLLATE=utf8mb4_0900_ai_ci;


CREATE TABLE permissions (
                             id BINARY(16) NOT NULL,
                             created_at DATETIME(6) NOT NULL,
                             updated_at DATETIME(6) NOT NULL,
                             name VARCHAR(100) NOT NULL,

                             PRIMARY KEY (id),
                             CONSTRAINT uk_permissions_name UNIQUE (name)
) ENGINE=InnoDB
DEFAULT CHARSET=utf8mb4
COLLATE=utf8mb4_0900_ai_ci;


CREATE TABLE user_roles (
                            user_id BINARY(16) NOT NULL,
                            role_id BINARY(16) NOT NULL,

                            PRIMARY KEY (user_id, role_id),

                            CONSTRAINT fk_user_roles_user
                                FOREIGN KEY (user_id)
                                    REFERENCES users(id),

                            CONSTRAINT fk_user_roles_role
                                FOREIGN KEY (role_id)
                                    REFERENCES roles(id)
) ENGINE=InnoDB
DEFAULT CHARSET=utf8mb4
COLLATE=utf8mb4_0900_ai_ci;


CREATE TABLE role_permissions (
                                  role_id BINARY(16) NOT NULL,
                                  permission_id BINARY(16) NOT NULL,

                                  PRIMARY KEY (role_id, permission_id),

                                  CONSTRAINT fk_role_permissions_role
                                      FOREIGN KEY (role_id)
                                          REFERENCES roles(id),

                                  CONSTRAINT fk_role_permissions_permission
                                      FOREIGN KEY (permission_id)
                                          REFERENCES permissions(id)
) ENGINE=InnoDB
DEFAULT CHARSET=utf8mb4
COLLATE=utf8mb4_0900_ai_ci;


-- Roles
INSERT INTO roles (id, created_at, updated_at, name)
VALUES
    (UUID_TO_BIN('00000000-0000-0000-0000-000000000001'), NOW(6), NOW(6), 'USER'),
    (UUID_TO_BIN('00000000-0000-0000-0000-000000000002'), NOW(6), NOW(6), 'ADMIN'),
    (UUID_TO_BIN('00000000-0000-0000-0000-000000000003'), NOW(6), NOW(6), 'MODERATOR');


-- Permissions
INSERT INTO permissions (id, created_at, updated_at, name)
VALUES
    (UUID_TO_BIN('10000000-0000-0000-0000-000000000001'), NOW(6), NOW(6), 'QUESTION_CREATE'),
    (UUID_TO_BIN('10000000-0000-0000-0000-000000000002'), NOW(6), NOW(6), 'QUESTION_UPDATE'),
    (UUID_TO_BIN('10000000-0000-0000-0000-000000000003'), NOW(6), NOW(6), 'QUESTION_DELETE'),

    (UUID_TO_BIN('20000000-0000-0000-0000-000000000001'), NOW(6), NOW(6), 'ANSWER_CREATE'),
    (UUID_TO_BIN('20000000-0000-0000-0000-000000000002'), NOW(6), NOW(6), 'ANSWER_UPDATE'),
    (UUID_TO_BIN('20000000-0000-0000-0000-000000000003'), NOW(6), NOW(6), 'ANSWER_DELETE'),

    (UUID_TO_BIN('30000000-0000-0000-0000-000000000001'), NOW(6), NOW(6), 'USER_DELETE'),
    (UUID_TO_BIN('30000000-0000-0000-0000-000000000002'), NOW(6), NOW(6), 'ROLE_UPDATE');


-- USER permissions
INSERT INTO role_permissions (role_id, permission_id)
VALUES
    (
        UUID_TO_BIN('00000000-0000-0000-0000-000000000001'),
        UUID_TO_BIN('10000000-0000-0000-0000-000000000001')
    ),
    (
        UUID_TO_BIN('00000000-0000-0000-0000-000000000001'),
        UUID_TO_BIN('10000000-0000-0000-0000-000000000002')
    ),
    (
        UUID_TO_BIN('00000000-0000-0000-0000-000000000001'),
        UUID_TO_BIN('20000000-0000-0000-0000-000000000001')
    ),
    (
        UUID_TO_BIN('00000000-0000-0000-0000-000000000001'),
        UUID_TO_BIN('20000000-0000-0000-0000-000000000002')
    );


-- ADMIN permissions
INSERT INTO role_permissions (role_id, permission_id)
SELECT
    UUID_TO_BIN('00000000-0000-0000-0000-000000000002'),
    id
FROM permissions;


-- MODERATOR permissions
INSERT INTO role_permissions (role_id, permission_id)
VALUES
    (
        UUID_TO_BIN('00000000-0000-0000-0000-000000000003'),
        UUID_TO_BIN('10000000-0000-0000-0000-000000000003')
    ),
    (
        UUID_TO_BIN('00000000-0000-0000-0000-000000000003'),
        UUID_TO_BIN('20000000-0000-0000-0000-000000000003')
    );


-- Give all existing users the USER role
INSERT INTO user_roles (user_id, role_id)
SELECT
    id,
    UUID_TO_BIN('00000000-0000-0000-0000-000000000001')
FROM users;