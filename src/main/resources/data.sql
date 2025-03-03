INSERT INTO config (id, created_at, updated_at, label, category, value)
VALUES ('authenticationCodeAdmin', now(), now(), '어드민 인증번호', 'AUTHENTICATION_CODE', '000000'),
       ('authenticationCodeStaff', now(), now(), '운영진 인증번호', 'AUTHENTICATION_CODE', '000001'),
       ('authenticationCodeAlumni', now(), now(), '정회원 인증번호', 'AUTHENTICATION_CODE', '000002'),
       ('authenticationCodeActive', now(), now(), '활동회원 인증번호', 'AUTHENTICATION_CODE', '000003'),
       ('needForceUpdate', now(), now(), '강제 업데이트', 'FORCE_UPDATE', false),
       ('forceUpdateReason', now(), now(), '강제 업데이트 사유', 'FORCE_UPDATE', null),
       ('activeGeneration', now(), now(), '활동 기수', 'ACTIVE_GENERATION', '25'),
       ('usageInquiryLink', now(), now(), '이용 문의', 'LINK', 'http://pf.kakao.com/_ixmUxjn/chat'),
       ('termsOfServiceLink', now(), now(), '이용약관', 'LINK',
        'https://yapp-workspace.notion.site/48f4eb2ffdd94740979e8a3b37ca260d?pvs=4'),
       ('privacyPolicyLink', now(), now(), '개인정보 처리방침', 'LINK',
        'https://yapp-workspace.notion.site/fc24f8ba29c34f9eb30eb945c621c1ca?pvs=4');

INSERT INTO users (id, created_at, updated_at, email, password, name, role, is_active)
VALUES ('01954809-38fd-1268-e0d6-d3fda39f6b4c', now(), now(), 'admin@admin.com',
        '$2a$10$FlqVcwbK6JAnkVx7gEdFdeH3Gb7bF/bzDfu5u0afry0jss.3I71Oe', '홍길동', 'ADMIN', true);

INSERT INTO activity_units (id, created_at, updated_at, position, generation, user_id)
VALUES (uuid(), now(), now(), 'PM', 1, '01954809-38fd-1268-e0d6-d3fda39f6b4c');
