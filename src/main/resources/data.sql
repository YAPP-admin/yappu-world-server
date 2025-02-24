INSERT INTO config (id, created_at, updated_at, value)
VALUES ('authenticationCodeAdmin', now(), now(), '000000'),
       ('authenticationCodeAlumni', now(), now(), '000001'),
       ('authenticationCodeActive', now(), now(), '000002'),
       ('needForceUpdate', now(), now(), 'false'),
       ('forceUpdateReason', now(), now(), '');
