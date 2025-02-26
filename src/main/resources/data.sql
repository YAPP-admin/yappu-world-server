INSERT INTO config (id, created_at, updated_at, value)
VALUES ('authenticationCodeAdmin', now(), now(), '000000'),
       ('authenticationCodeStaff', now(), now(), '000001'),
       ('authenticationCodeAlumni', now(), now(), '000002'),
       ('authenticationCodeActive', now(), now(), '000003'),
       ('needForceUpdate', now(), now(), 'false'),
       ('forceUpdateReason', now(), now(), null),
       ('activeGeneration', now(), now(), '25');
