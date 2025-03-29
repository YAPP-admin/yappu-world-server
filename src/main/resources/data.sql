-- 가입코드
INSERT INTO config (name, created_at, updated_at, label, category, value)
VALUES ('authenticationCodeAdmin', now(), now(), '어드민 가입코드', 'AUTHENTICATION_CODE', '000000'),
       ('authenticationCodeStaff', now(), now(), '운영진 가입코드', 'AUTHENTICATION_CODE', '000001'),
       ('authenticationCodeAlumni', now(), now(), '정회원 가입코드', 'AUTHENTICATION_CODE', '000002'),
       ('authenticationCodeGraduate', now(), now(), '수료회원 가입코드', 'AUTHENTICATION_CODE', '000003'),
       ('authenticationCodeActive', now(), now(), '활동회원 가입코드', 'AUTHENTICATION_CODE', '000004');

-- 최소 지원 버전
INSERT INTO config (name, created_at, updated_at, label, category, value)
VALUES ('minSupportVersionInIos', now(), now(), 'iOS 최소 지원 버전', 'FORCE_UPDATE', '1.0.0'),
       ('minSupportVersionInAndroid', now(), now(), '안드로이드 최소 지원 버전', 'FORCE_UPDATE', '1.0.0');

-- 활동기수
INSERT INTO config (name, created_at, updated_at, label, category, value)
VALUES ('activeGeneration', now(), now(), '활동 기수', 'ACTIVE_GENERATION', '25');

-- 이용링크
INSERT INTO config (name, created_at, updated_at, label, category, value)
VALUES ('usageInquiryLink', now(), now(), '이용 문의', 'LINK', 'http://pf.kakao.com/_ixmUxjn/chat'),
       ('termsOfServiceLink', now(), now(), '이용약관', 'LINK',
        'https://yapp-workspace.notion.site/48f4eb2ffdd94740979e8a3b37ca260d?pvs=4'),
       ('privacyPolicyLink', now(), now(), '개인정보 처리방침', 'LINK',
        'https://yapp-workspace.notion.site/fc24f8ba29c34f9eb30eb945c621c1ca?pvs=4');

INSERT INTO users (id, created_at, updated_at, email, password, name, role, is_active)
VALUES ('01954809-38fd-1268-e0d6-d3fda39f6b4c', '2025-03-05 03:11:16', now(), 'admin@admin.com',
        '$2a$10$FlqVcwbK6JAnkVx7gEdFdeH3Gb7bF/bzDfu5u0afry0jss.3I71Oe', '홍길동', 'ADMIN', true);

INSERT INTO activity_units (id, created_at, updated_at, position, generation, user_id)
VALUES (uuid(), now(), now(), 'PM', 1, '01954809-38fd-1268-e0d6-d3fda39f6b4c');

INSERT INTO user_devices (id, created_at, updated_at, user_id, fcm_token)
VALUES (uuid(), now(), now(), '01954809-38fd-1268-e0d6-d3fda39f6b4c', 'fcm_token');

INSERT INTO user_alarm_settings (id, created_at, updated_at, user_id, device, master)
VALUES (uuid(), now(), now(), '01954809-38fd-1268-e0d6-d3fda39f6b4c', true, true);

INSERT INTO boards (id, created_at, updated_at, board_type, notice_type, title, content, content_summary,
                    display_target, writer_id,
                    is_active)
VALUES (uuid(), now(), now(), 'NOTICE', 'SESSION', '제목입니다1', '## 안녕하세요 만나서 반갑습니다', '요약', '몰라?',
        '01954809-38fd-1268-e0d6-d3fda39f6b4c', true),
       (uuid(), now(), now(), 'NOTICE', 'SESSION', '제목입니다2', '## 안녕하세요 만나서 반갑습니다', '요약', '몰라?',
        '01954809-38fd-1268-e0d6-d3fda39f6b4c', true),
       (uuid(), now(), now(), 'NOTICE', 'SESSION', '제목입니다3', '## 안녕하세요 만나서 반갑습니다', '요약', '몰라?',
        '01954809-38fd-1268-e0d6-d3fda39f6b4c', true),
       (uuid(), now(), now(), 'NOTICE', 'OPERATION', '제목입니다4', '## 안녕하세요 만나서 반갑습니다', '요약', '몰라?',
        '01954809-38fd-1268-e0d6-d3fda39f6b4c', true),
       (uuid(), now(), now(), 'NOTICE', 'OPERATION', '제목입니다5', '## 안녕하세요 만나서 반갑습니다', '요약', '몰라?',
        '01954809-38fd-1268-e0d6-d3fda39f6b4c', true);

INSERT INTO posts (id, created_at, updated_at, type, notice_type, title, content, content_summary,
                   display_target, writer_id,
                   is_active)
VALUES (UUID_TO_BIN(uuid()), now(), now(), 'NOTICE', 'SESSION', '제목입니다1', '## 안녕하세요 만나서 반갑습니다', '요약', '몰라?',
        UUID_TO_BIN('01954809-38fd-1268-e0d6-d3fda39f6b4c'), true),
       (UUID_TO_BIN(uuid()), now(), now(), 'NOTICE', 'SESSION', '제목입니다2', '## 안녕하세요 만나서 반갑습니다', '요약', '몰라?',
        UUID_TO_BIN('01954809-38fd-1268-e0d6-d3fda39f6b4c'), true),
       (UUID_TO_BIN(uuid()), now(), now(), 'NOTICE', 'SESSION', '제목입니다3', '## 안녕하세요 만나서 반갑습니다', '요약', '몰라?',
        UUID_TO_BIN('01954809-38fd-1268-e0d6-d3fda39f6b4c'), true),
       (UUID_TO_BIN(uuid()), now(), now(), 'NOTICE', 'OPERATION', '제목입니다4', '## 안녕하세요 만나서 반갑습니다', '요약', '몰라?',
        UUID_TO_BIN('01954809-38fd-1268-e0d6-d3fda39f6b4c'), true),
       (UUID_TO_BIN(uuid()), now(), now(), 'NOTICE', 'OPERATION', '제목입니다5', '## 안녕하세요 만나서 반갑습니다', '요약', '몰라?',
        UUID_TO_BIN('01954809-38fd-1268-e0d6-d3fda39f6b4c'), true);

INSERT INTO generations (value, start_date, end_date, is_active)
VALUES (1, null, null, false),
       (2, null, null, false),
       (3, null, null, false),
       (4, null, null, false),
       (5, null, null, false),
       (6, null, null, false),
       (7, null, null, false),
       (8, null, null, false),
       (9, null, null, false),
       (10, null, null, false),
       (11, null, null, false),
       (12, null, null, false),
       (13, null, null, false),
       (14, null, null, false),
       (15, null, null, false),
       (16, null, null, false),
       (17, null, null, false),
       (18, null, null, false),
       (19, null, null, false),
       (20, '2022-04-02', '2022-08-06', false),
       (21, '2022-10-23', '2023-02-26', false),
       (22, '2023-04-29', '2023-08-05', false),
       (23, '2023-10-28', '2024-02-24', false),
       (24, '2024-05-11', '2024-09-14', false),
       (25, '2024-11-16', '2025-03-08', false);

INSERT INTO schedules (id, created_at, updated_at, is_deleted, name, description, place, date, end_date, time, end_time,
                       is_all_day, generation, type, session_type)
VALUES (UUID_TO_BIN(uuid()), now(), now(), false, 'OT', '첫 세션이에요',
        '강북노동자복지관', '2024-11-1', '2024-11-1', '14:00:00', '18:00:00',
        false, 25, 'SESSION', 'OFFLINE'),
       (UUID_TO_BIN(uuid()), now(), now(), false, '팀세션', '첫 팀세션',
        null, '2024-11-1', '2024-11-1', null, null, true, 25, 'SESSION',
        'TEAM'),
       (UUID_TO_BIN(uuid()), now(), now(), false, '팀매칭',
        '팀을 매칭해요', 'SBA 산학센터', '2024-11-3', '2024-11-3',
        '14:00:00', '18:00:00', false, 25, 'SESSION', 'OFFLINE');
