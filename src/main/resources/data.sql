INSERT INTO config (id, created_at, updated_at, label, category, value)
VALUES ('authenticationCodeAdmin', now(), now(), '어드민 인증번호', 'AUTHENTICATION_CODE', '000000'),
       ('authenticationCodeStaff', now(), now(), '운영진 인증번호', 'AUTHENTICATION_CODE', '000001'),
       ('authenticationCodeAlumni', now(), now(), '정회원 인증번호', 'AUTHENTICATION_CODE', '000002'),
       ('authenticationCodeActive', now(), now(), '활동회원 인증번호', 'AUTHENTICATION_CODE', '000003'),
       ('needForceUpdate', now(), now(), '강제 업데이트', 'FORCE_UPDATE', false),
       ('forceUpdateReason', now(), now(), '강제 업데이트 사유', 'FORCE_UPDATE', null),
       ('activeGeneration', now(), now(), '활동 기수', 'ACTIVE_GENERATION', '25'),
       ('kakaoLink', now(), now(), '카카오톡 채팅 문의', 'LINK', '25'),
       ('termsOfServiceLink', now(), now(), '이용약관', 'LINK', '25'),
       ('privacyPolicyLink', now(), now(), '개인정보 처리방침', 'LINK', '25');
