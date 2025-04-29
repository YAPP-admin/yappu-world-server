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
