-- =============================================================================
-- Applicant Tracking System — database seed (MySQL)
-- =============================================================================
-- Run after the schema exists (e.g. `schema.sql` or JPA `ddl-auto` has created tables).
-- Use on an empty database or truncate tables in reverse FK order first.
--
-- PLAINTEXT LOGIN CREDENTIALS (before hashing; use these to sign in)
--   Email                      | Password
--   --------------------------- | ----------------
--   admin@example.com          | admin123
--   manager@example.com        | manager123
--   hr@example.com             | hr12345
--   recruiter@example.com      | recruiter123
--
-- Hashes below are BCrypt with cost 10 ($2b$...), compatible with Spring Security
-- BCryptPasswordEncoder (same algorithm as DemoUserSeedService / UserServiceImpl).
-- =============================================================================

USE applicant_tracking;

SET FOREIGN_KEY_CHECKS = 0;

-- Departments
INSERT INTO departments (id, name, description) VALUES
('11111111-1111-1111-1111-111111111111', 'Engineering', 'Engineering department'),
('22222222-2222-2222-2222-222222222222', 'Product', 'Product management'),
('33333333-3333-3333-3333-333333333333', 'HR', 'Human resources');

-- Users (password_hash = BCrypt of plaintext password on the same row comment)
INSERT INTO users (id, email, password_hash, full_name, avatar_url, active, deleted, account_locked, role, department_id, created_by, created_at)
VALUES
-- admin@example.com / admin123
('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'admin@example.com', '$2b$10$n8baWIgX9XodM.qTaqL6teATUtUGtmfYQTAuwRdMKmwCmbvP7dGlG', 'Admin User', '', 1, 0, 0, 'SYSTEM_ADMIN', '11111111-1111-1111-1111-111111111111', NULL, NOW()),
-- manager@example.com / manager123
('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 'manager@example.com', '$2b$10$CNsx0vnvZWqK.p7YizfZEO6jhWrJ6oz6psNF/XKPPMHs2IK62.5kO', 'HR Manager', '', 1, 0, 0, 'HR_MANAGER', '11111111-1111-1111-1111-111111111111', NULL, NOW()),
-- hr@example.com / hr12345
('dddddddd-dddd-dddd-dddd-dddddddddddd', 'hr@example.com', '$2b$10$oR2oSLtQI6bYr55TZjaBFe97Se.p8y1mtGmeOazj/wFuVlkvNvX2e', 'HR User', '', 1, 0, 0, 'HR', '33333333-3333-3333-3333-333333333333', NULL, NOW()),
-- recruiter@example.com / recruiter123
('cccccccc-cccc-cccc-cccc-cccccccccccc', 'recruiter@example.com', '$2b$10$16XaZ6BAm6oZNrix3ZA6ROoQQ2uIhFvAeMJoh0.L0IYAjGwLKSZdC', 'Recruiter User', '', 1, 0, 0, 'INTERVIEWER', '33333333-3333-3333-3333-333333333333', NULL, NOW());

-- Jobs (JobStatus values aligned with Java enum: JobStatus)
INSERT INTO jobs (id, title, description, location, salary, department_id, hiring_manager_id, status, headcount, created_by, created_at)
VALUES
('10101010-1010-1010-1010-101010101010', 'Senior Backend Engineer', 'Design backend services', 'Ho Chi Minh', '$45k-$60k', '11111111-1111-1111-1111-111111111111', 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 'APPROVED', 2, 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', NOW()),
('12121212-1212-1212-1212-121212121212', 'Product Owner', 'Own product roadmap', 'Ho Chi Minh', '$40k-$55k', '22222222-2222-2222-2222-222222222222', 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 'APPROVED', 1, 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', NOW()),
('13131313-1313-1313-1313-131313131313', 'DevOps Engineer', 'CI/CD and cloud infrastructure', 'Ho Chi Minh', '$35k-$50k', '11111111-1111-1111-1111-111111111111', 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 'PENDING_APPROVAL', 1, 'dddddddd-dddd-dddd-dddd-dddddddddddd', NOW());

-- Candidates
INSERT INTO candidates (id, full_name, email, phone, current_company, source, location, experience_years, summary, created_by, created_at)
VALUES
('d1d1d1d1-d1d1-d1d1-d1d1-d1d1d1d1d1d1', 'Alice Nguyen', 'alice@example.com', '+84123456789', 'Acme Corp', 'LinkedIn', 'HCMC', 5, 'Experienced backend engineer', 'cccccccc-cccc-cccc-cccc-cccccccccccc', NOW()),
('d2d2d2d2-d2d2-d2d2-d2d2-d2d2d2d2d2d2', 'Bob Tran', 'bob@example.com', '+84987654321', 'Beta Co', 'Referral', 'Hanoi', 3, 'Fullstack developer', 'cccccccc-cccc-cccc-cccc-cccccccccccc', NOW());

-- Applications
INSERT INTO applications (id, candidate_id, job_id, stage, status, applied_at, created_by, created_at)
VALUES
('e1e1e1e1-e1e1-e1e1-e1e1-e1e1e1e1e1e1', 'd1d1d1d1-d1d1-d1d1-d1d1-d1d1d1d1d1d1', '10101010-1010-1010-1010-101010101010', 'APPLIED', 'ACTIVE', NOW(), 'cccccccc-cccc-cccc-cccc-cccccccccccc', NOW()),
('e2e2e2e2-e2e2-e2e2-e2e2-e2e2e2e2e2e2', 'd2d2d2d2-d2d2-d2d2-d2d2-d2d2d2d2d2d2', '12121212-1212-1212-1212-121212121212', 'APPLIED', 'ACTIVE', NOW(), 'cccccccc-cccc-cccc-cccc-cccccccccccc', NOW());

-- Scorecard templates
INSERT INTO scorecard_templates (id, name, department_id, created_by, created_at)
VALUES
('f1f1f1f1-f1f1-f1f1-f1f1-f1f1f1f1f1f1', 'Engineering Template', '11111111-1111-1111-1111-111111111111', 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', NOW());

-- Scorecard criteria
INSERT INTO scorecard_criteria (id, template_id, name, weight, created_by, created_at)
VALUES
('g1g1g1g1-g1g1-g1g1-g1g1-g1g1g1g1g1g1', 'f1f1f1f1-f1f1-f1f1-f1f1-f1f1f1f1f1f1', 'Coding Skills', 40, 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', NOW()),
('g2g2g2g2-g2g2-g2g2-g2g2-g2g2g2g2g2g2', 'f1f1f1f1-f1f1-f1f1-f1f1-f1f1f1f1f1f1', 'Communication', 30, 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', NOW()),
('g3g3g3g3-g3g3-g3g3-g3g3-g3g3g3g3g3g3', 'f1f1f1f1-f1f1-f1f1-f1f1-f1f1f1f1f1f1', 'Problem Solving', 30, 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', NOW());

-- Interviews
INSERT INTO interviews (id, application_id, template_id, scheduled_at, location, meeting_link, type, status, created_by, created_at)
VALUES
('h1h1h1h1-h1h1-h1h1-h1h1-h1h1h1h1h1h1', 'e1e1e1e1-e1e1-e1e1-e1e1-e1e1e1e1e1e1', 'f1f1f1f1-f1f1-f1f1-f1f1-f1f1f1f1f1f1', NOW() + INTERVAL 2 DAY, 'Video', 'https://meet.example.com/123', 'ONLINE', 'SCHEDULED', 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', NOW());

-- Interview participants (ParticipantRole enum uses INTERVIEWER)
INSERT INTO interview_participants (interview_id, user_id, role, feedback, overall_score)
VALUES
('h1h1h1h1-h1h1-h1h1-h1h1-h1h1h1h1h1h1', 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 'INTERVIEWER', NULL, NULL);

-- Interview scores
INSERT INTO interview_scores (id, interview_id, user_id, criterion_id, score, comment, created_by, created_at)
VALUES
('i1i1i1i1-i1i1-i1i1-i1i1-i1i1i1i1i1i1', 'h1h1h1h1-h1h1-h1h1-h1h1-h1h1h1h1h1h1', 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 'g1g1g1g1-g1g1-g1g1-g1g1-g1g1g1g1g1g1', 8, 'Strong code base', 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', NOW());

-- Candidate notes
INSERT INTO candidate_notes (id, application_id, content, created_by, created_at)
VALUES
('j1j1j1j1-j1j1-j1j1-j1j1-j1j1j1j1j1j1', 'e1e1e1e1-e1e1-e1e1-e1e1-e1e1e1e1e1e1', 'Positive initial screen', 'cccccccc-cccc-cccc-cccc-cccccccccccc', NOW());

-- Candidate documents
INSERT INTO candidate_documents (id, candidate_id, file_name, file_url, file_type, file_size_bytes, uploaded_at, created_by, created_at)
VALUES
('k1k1k1k1-k1k1-k1k1-k1k1-k1k1k1k1k1k1', 'd1d1d1d1-d1d1-d1d1-d1d1-d1d1d1d1d1d1', 'CV.pdf', 'https://cdn.example.com/cv/alice.pdf', 'application/pdf', 34567, NOW(), 'cccccccc-cccc-cccc-cccc-cccccccccccc', NOW());

-- Candidate stage history
INSERT INTO candidate_stage_history (id, application_id, from_stage, to_stage, created_by, created_at)
VALUES
('l1l1l1l1-l1l1-l1l1-l1l1-l1l1l1l1l1l1', 'e1e1e1e1-e1e1-e1e1-e1e1-e1e1e1e1e1e1', 'APPLIED', 'SCREENING', 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', NOW());

-- Offers
INSERT INTO offers (id, application_id, salary, position_title, status, created_by, created_at)
VALUES
('m1m1m1m1-m1m1-m1m1-m1m1-m1m1m1m1m1m1', 'e1e1e1e1-e1e1-e1e1-e1e1-e1e1e1e1e1e1', 120000, 'Senior Backend Engineer', 'DRAFT', 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', NOW());

-- Job approvals (ApprovalStatus: PENDING | APPROVED | REJECTED)
INSERT INTO job_approvals (id, job_id, approved_by, status, comment, created_by, created_at)
VALUES
('n1n1n1n1-n1n1-n1n1-n1n1-n1n1n1n1n1n1', '10101010-1010-1010-1010-101010101010', 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 'APPROVED', 'Approved for posting', 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', NOW()),
('n2n2n2n2-n2n2-n2n2-n2n2-n2n2n2n2n2n2', '13131313-1313-1313-1313-131313131313', NULL, 'PENDING', 'Submitted for HR Manager review', 'dddddddd-dddd-dddd-dddd-dddddddddddd', NOW());

-- Offer approvals
INSERT INTO offer_approvals (id, offer_id, approved_by, status, comment, created_by, created_at)
VALUES
('o1o1o1o1-o1o1-o1o1-o1o1-o1o1o1o1o1o1', 'm1m1m1m1-m1m1-m1m1-m1m1-m1m1m1m1m1m1', 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 'APPROVED', 'Salary confirmed and approved', 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', NOW());

-- Refresh tokens
INSERT INTO refresh_tokens (id, token, user_id, expiry_date, revoked, created_by, created_at)
VALUES
('p1p1p1p1-p1p1-p1p1-p1p1-p1p1p1p1p1p1', 'rt-token-123', 'cccccccc-cccc-cccc-cccc-cccccccccccc', NOW() + INTERVAL 30 DAY, 0, 'cccccccc-cccc-cccc-cccc-cccccccccccc', NOW());

-- Notifications (NotificationType enum)
INSERT INTO notifications (user_id, type, title, message, is_read, reference_id, created_at)
VALUES
('dddddddd-dddd-dddd-dddd-dddddddddddd', 'SYSTEM_ALERT', 'Welcome', 'Welcome to the Applicant Tracking System', 0, NULL, NOW());

-- Audit logs
INSERT INTO audit_logs (user_id, action, entity_type, entity_id, old_value, new_value, ip_address, user_agent, created_at)
VALUES
('cccccccc-cccc-cccc-cccc-cccccccccccc', 'CREATE', 'candidate', 'd1d1d1d1-d1d1-d1d1-d1d1-d1d1d1d1d1d1', NULL, '{"full_name":"Alice Nguyen"}', '127.0.0.1', 'PostmanRuntime/7.0', NOW());

-- System config (column name is config_key per schema / JPA)
INSERT INTO system_configs (config_key, value, updated_by, updated_at)
VALUES
('app.name', 'Enterprise ATS', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', NOW()),
('email.from', 'no-reply@example.com', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', NOW());

SET FOREIGN_KEY_CHECKS = 1;
