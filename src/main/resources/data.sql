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

-- 출석코드
INSERT INTO config (name, created_at, updated_at, label, category, value)
VALUES ('attendanceCode', now(), now(), '출석코드', 'ATTENDANCE_CODE', '0000');

INSERT INTO users (id, created_at, updated_at, email, password, name, role, is_active)
VALUES (UUID_TO_BIN('01954809-38fd-1268-e0d6-d3fda39f6b4c'), '2025-03-05 03:11:16', now(), 'admin@admin.com',
        '$2a$10$FlqVcwbK6JAnkVx7gEdFdeH3Gb7bF/bzDfu5u0afry0jss.3I71Oe', '홍길동', 'ACTIVE', true);

INSERT INTO activity_units (id, created_at, updated_at, position, generation, user_id)
VALUES (UUID_TO_BIN(uuid()), now(), now(), 'PM', 25, UUID_TO_BIN('01954809-38fd-1268-e0d6-d3fda39f6b4c'));

INSERT INTO user_devices (id, created_at, updated_at, user_id, fcm_token)
VALUES (UUID_TO_BIN(uuid()), now(), now(), UUID_TO_BIN('01954809-38fd-1268-e0d6-d3fda39f6b4c'), 'fcm_token');

INSERT INTO user_alarm_settings (id, created_at, updated_at, user_id, device, master)
VALUES (UUID_TO_BIN(uuid()), now(), now(), UUID_TO_BIN('01954809-38fd-1268-e0d6-d3fda39f6b4c'), true, true);

INSERT INTO boards (id, created_at, updated_at, board_type, notice_type, title, content, content_summary,
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

INSERT INTO posts (id, created_at, updated_at, type, notice_type, title, content, content_summary,
                   display_target, writer_id,
                   is_active)
VALUES (UUID_TO_BIN('0195ead2-2577-2f48-3d98-38abc4a0624c'), '2025-03-31 15:09:55.588721', '2025-03-31 15:09:55.588721',
        'NOTICE', 'SESSION', '제목입니다1', '## 안녕하세요 만나서 반갑습니다', '요약', '몰라?',
        UUID_TO_BIN('01954809-38fd-1268-e0d6-d3fda39f6b4c'), true),
       (UUID_TO_BIN('0195ead2-36a1-6006-56c8-336f73ed78fe'), '2025-03-31 15:09:59.969386', '2025-03-31 15:09:59.969386',
        'NOTICE', 'SESSION', '제목입니다2', '## 안녕하세요 만나서 반갑습니다', '요약', '몰라?',
        UUID_TO_BIN('01954809-38fd-1268-e0d6-d3fda39f6b4c'), true),
       (UUID_TO_BIN('0195ead2-4342-96e2-9cf2-4dc2e91c24b0'), '2025-03-31 15:10:03.202644', '2025-03-31 15:10:03.202644',
        'NOTICE', 'SESSION', '제목입니다3', '## 안녕하세요 만나서 반갑습니다', '요약', '몰라?',
        UUID_TO_BIN('01954809-38fd-1268-e0d6-d3fda39f6b4c'), true),
       (UUID_TO_BIN('0195ead2-4f1a-3822-51cd-3bc627c968f8'), '2025-03-31 15:10:06.234822', '2025-03-31 15:10:06.234822',
        'NOTICE', 'OPERATION', '제목입니다4', '## 안녕하세요 만나서 반갑습니다', '요약', '몰라?',
        UUID_TO_BIN('01954809-38fd-1268-e0d6-d3fda39f6b4c'), true),
       (UUID_TO_BIN('0195ead2-6304-2f5b-839a-15113f3469b3'), '2025-03-31 15:10:11.333284', '2025-03-31 15:10:11.333284',
        'NOTICE', 'OPERATION', '제목입니다5', '## 안녕하세요 만나서 반갑습니다', '요약', '몰라?',
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
       (25, '2024-11-16', '2025-03-08', true);

INSERT INTO schedules VALUES (UUID_TO_BIN('c076cadd-1b30-11f0-add0-0242ac140002'), NOW(), NOW(), FALSE, 'OT', 'OT 진행 & PM·디자이너 팀매칭', '강북노동자복지관', '2024-11-16', '2024-11-16', '14:00:00', '18:00:00', FALSE, 25, 'SESSION', 'OFFLINE');
INSERT INTO schedules VALUES (UUID_TO_BIN('c076ff63-1b30-11f0-add0-0242ac140002'), NOW(), NOW(), FALSE, '팀 매칭', null, 'SBA 산학센터', '2024-11-23', '2024-11-23', '14:00:00', '18:00:00', FALSE, 25, 'SESSION', 'OFFLINE');
INSERT INTO schedules VALUES (UUID_TO_BIN('c0775226-1b30-11f0-add0-0242ac140002'), NOW(), NOW(), FALSE, '팀 세션', null, null, '2024-11-30', '2024-11-30', null, null, FALSE, 25, 'SESSION', 'TEAM');
INSERT INTO schedules VALUES (UUID_TO_BIN('c0778896-1b30-11f0-add0-0242ac140002'), NOW(), NOW(), FALSE, '관심사 세션', '팀별 자율 진행 (출석 인증 필수)', null, '2024-12-07', '2024-12-07', '14:00:00', '18:00:00', FALSE, 25, 'SESSION', 'OFFLINE');
INSERT INTO schedules VALUES (UUID_TO_BIN('c077c803-1b30-11f0-add0-0242ac140002'), NOW(), NOW(), FALSE, '기획 세션', '기획 & 와이어프레임 발표', null, '2024-12-21', '2024-12-21', '13:00:00', '17:00:00', FALSE, 25, 'SESSION', 'OFFLINE');
INSERT INTO schedules VALUES (UUID_TO_BIN('c077fc61-1b30-11f0-add0-0242ac140002'), NOW(), NOW(), FALSE, '1차 Dev.camp', '1차 UT & 해커톤', '성수 엘리스 랩', '2025-01-04', '2025-01-04', '09:00:00', '21:00:00', FALSE, 25, 'SESSION', 'OFFLINE');
INSERT INTO schedules VALUES (UUID_TO_BIN('c0784091-1b30-11f0-add0-0242ac140002'), NOW(), NOW(), FALSE, '팀 세션', '팀별 자율 진행 (출석 인증 필수)', null, '2025-01-11', '2025-01-11', null, null, FALSE, 25, 'SESSION', 'TEAM');
INSERT INTO schedules VALUES (UUID_TO_BIN('c0788497-1b30-11f0-add0-0242ac140002'), NOW(), NOW(), FALSE, '2차 Dev.camp', '1차 데모 & 직군 피드백', '공덕창업허브', '2025-01-18', '2025-01-18', '14:00:00', '18:00:00', FALSE, 25, 'SESSION', 'OFFLINE');
INSERT INTO schedules VALUES (UUID_TO_BIN('c078c671-1b30-11f0-add0-0242ac140002'), NOW(), NOW(), FALSE, '팀 세션', '팀별 자율 진행 (출석 인증 필수)', null, '2025-01-25', '2025-01-25', null, null, FALSE, 25, 'SESSION', 'TEAM');
INSERT INTO schedules VALUES (UUID_TO_BIN('c0791ec0-1b30-11f0-add0-0242ac140002'), NOW(), NOW(), FALSE, '팀 세션', '팀별 자율 진행 (출석 인증 필수)', null, '2025-02-01', '2025-02-01', null, null, FALSE, 25, 'SESSION', 'TEAM');
INSERT INTO schedules VALUES (UUID_TO_BIN('c0795e0f-1b30-11f0-add0-0242ac140002'), NOW(), NOW(), FALSE, '3차 Dev.camp', '2차 UT & 직군 세션', '공덕창업허브', '2025-02-08', '2025-02-08', '14:00:00', '18:00:00', FALSE, 25, 'SESSION', 'OFFLINE');
INSERT INTO schedules VALUES (UUID_TO_BIN('c079a452-1b30-11f0-add0-0242ac140002'), NOW(), NOW(), FALSE, '팀 세션', '팀별 자율 진행 (출석 인증 필수)', null, '2025-02-15', '2025-02-15', null, null, FALSE, 25, 'SESSION', 'TEAM');
INSERT INTO schedules VALUES (UUID_TO_BIN('c079db6e-1b30-11f0-add0-0242ac140002'), NOW(), NOW(), FALSE, '데모데이', '연합 데모데이', '공덕창업허브', '2025-02-22', '2025-02-22', '14:00:00', '17:00:00', FALSE, 25, 'SESSION', 'OFFLINE');
INSERT INTO schedules VALUES (UUID_TO_BIN('c07a1f04-1b30-11f0-add0-0242ac140002'), NOW(), NOW(), FALSE, '팀 세션', '팀별 자율 진행 (출석 인증 필수)', null, '2025-03-01', '2025-03-01', null, null, FALSE, 25, 'SESSION', 'TEAM');
INSERT INTO schedules VALUES (UUID_TO_BIN('c07a6213-1b30-11f0-add0-0242ac140002'), NOW(), NOW(), FALSE, '성과 공유회', null, '서울시공익활동지원센터', '2025-03-08', '2025-03-08', '13:30:00', '17:00:00', FALSE, 25, 'SESSION', 'OFFLINE');
INSERT INTO schedules VALUES (UUID_TO_BIN('c07aa77e-1b30-11f0-add0-0242ac140002'), NOW(), NOW(), FALSE, '가짜 세션1', null, '아몰랑', '2025-04-18', '2025-04-18', '13:30:00', '17:00:00', FALSE, 25, 'SESSION', 'OFFLINE');
INSERT INTO schedules VALUES (UUID_TO_BIN('c07afa8b-1b30-11f0-add0-0242ac140002'), NOW(), NOW(), FALSE, '가짜 세션2', null, '아몰랑', '2025-05-08', '2025-05-08', '13:30:00', '17:00:00', FALSE, 25, 'SESSION', 'OFFLINE');

INSERT INTO attendances (id, created_at, updated_at, user_id, schedule_id, status)
VALUES (UUID_TO_BIN(uuid()), now(), now(), UUID_TO_BIN('01954809-38fd-1268-e0d6-d3fda39f6b4c'), UUID_TO_BIN('c076cadd-1b30-11f0-add0-0242ac140002'), 'ON_TIME'),
       (UUID_TO_BIN(uuid()), now(), now(), UUID_TO_BIN('01954809-38fd-1268-e0d6-d3fda39f6b4c'), UUID_TO_BIN('c076ff63-1b30-11f0-add0-0242ac140002'), 'LATE'),
       (UUID_TO_BIN(uuid()), now(), now(), UUID_TO_BIN('01954809-38fd-1268-e0d6-d3fda39f6b4c'), UUID_TO_BIN('c0775226-1b30-11f0-add0-0242ac140002'), 'ABSENT'),
       (UUID_TO_BIN(uuid()), now(), now(), UUID_TO_BIN('01954809-38fd-1268-e0d6-d3fda39f6b4c'), UUID_TO_BIN('c0778896-1b30-11f0-add0-0242ac140002'), 'ON_TIME'),
       (UUID_TO_BIN(uuid()), now(), now(), UUID_TO_BIN('01954809-38fd-1268-e0d6-d3fda39f6b4c'), UUID_TO_BIN('c077c803-1b30-11f0-add0-0242ac140002'), 'ON_TIME'),
       (UUID_TO_BIN(uuid()), now(), now(), UUID_TO_BIN('01954809-38fd-1268-e0d6-d3fda39f6b4c'), UUID_TO_BIN('c077fc61-1b30-11f0-add0-0242ac140002'), 'ON_TIME'),
       (UUID_TO_BIN(uuid()), now(), now(), UUID_TO_BIN('01954809-38fd-1268-e0d6-d3fda39f6b4c'), UUID_TO_BIN('c0784091-1b30-11f0-add0-0242ac140002'), 'ABSENT'),
       (UUID_TO_BIN(uuid()), now(), now(), UUID_TO_BIN('01954809-38fd-1268-e0d6-d3fda39f6b4c'), UUID_TO_BIN('c0788497-1b30-11f0-add0-0242ac140002'), 'ON_TIME'),
       (UUID_TO_BIN(uuid()), now(), now(), UUID_TO_BIN('01954809-38fd-1268-e0d6-d3fda39f6b4c'), UUID_TO_BIN('c078c671-1b30-11f0-add0-0242ac140002'), 'ON_TIME'),
       (UUID_TO_BIN(uuid()), now(), now(), UUID_TO_BIN('01954809-38fd-1268-e0d6-d3fda39f6b4c'), UUID_TO_BIN('c0791ec0-1b30-11f0-add0-0242ac140002'), 'LATE'),
       (UUID_TO_BIN(uuid()), now(), now(), UUID_TO_BIN('01954809-38fd-1268-e0d6-d3fda39f6b4c'), UUID_TO_BIN('c0795e0f-1b30-11f0-add0-0242ac140002'), 'ON_TIME'),
       (UUID_TO_BIN(uuid()), now(), now(), UUID_TO_BIN('01954809-38fd-1268-e0d6-d3fda39f6b4c'), UUID_TO_BIN('c079a452-1b30-11f0-add0-0242ac140002'), 'LATE'),
       (UUID_TO_BIN(uuid()), now(), now(), UUID_TO_BIN('01954809-38fd-1268-e0d6-d3fda39f6b4c'), UUID_TO_BIN('c079db6e-1b30-11f0-add0-0242ac140002'), 'ON_TIME'),
       (UUID_TO_BIN(uuid()), now(), now(), UUID_TO_BIN('01954809-38fd-1268-e0d6-d3fda39f6b4c'), UUID_TO_BIN('c07a1f04-1b30-11f0-add0-0242ac140002'), 'ON_TIME'),
       (UUID_TO_BIN(uuid()), now(), now(), UUID_TO_BIN('01954809-38fd-1268-e0d6-d3fda39f6b4c'), UUID_TO_BIN('c07a6213-1b30-11f0-add0-0242ac140002'), 'ON_TIME');

INSERT INTO late_passes (id, created_at, updated_at, user_id, generation, reason)
VALUES (UUID_TO_BIN(uuid()), now(), now(), UUID_TO_BIN('01954809-38fd-1268-e0d6-d3fda39f6b4c'), 25, '한 번 봐드림');

