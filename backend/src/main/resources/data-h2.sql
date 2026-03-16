-- ============================================================
-- ATS H2 Seed Data — loaded when spring.profiles.active=h2
-- ============================================================

-- Departments
INSERT INTO departments (id, name, description) VALUES
    ('00000000-0000-0000-0000-000000000001', 'Engineering',   'Software Engineering department'),
    ('00000000-0000-0000-0000-000000000002', 'Product',       'Product & Design department'),
    ('00000000-0000-0000-0000-000000000003', 'Human Resources','People & Culture department');

-- Users (passwords are BCrypt of "Password@123")
INSERT INTO users (id, email, full_name, role, status, active, deleted, account_locked,
                   created_at, last_modified_date,
                   password_hash, department_id)
VALUES
    ('10000000-0000-0000-0000-000000000001',
     'admin@ats.local', 'System Admin', 'SYSTEM_ADMIN', 'ACTIVE', true, false, false,
     CURRENT_TIMESTAMP, CURRENT_TIMESTAMP,
     '$2b$10$hmz6N7oZuvyz1xLaR8Mr5.iFBzNYbOhZkNNUNB3wZkSk07qut.TY.',
     NULL),

    ('10000000-0000-0000-0000-000000000002',
     'hr@ats.local', 'Nguyễn HR', 'HR', 'ACTIVE', true, false, false,
     CURRENT_TIMESTAMP, CURRENT_TIMESTAMP,
     '$2b$10$hmz6N7oZuvyz1xLaR8Mr5.iFBzNYbOhZkNNUNB3wZkSk07qut.TY.',
     '00000000-0000-0000-0000-000000000003'),

    ('10000000-0000-0000-0000-000000000003',
     'hrm@ats.local', 'Trần HR Manager', 'HR_MANAGER', 'ACTIVE', true, false, false,
     CURRENT_TIMESTAMP, CURRENT_TIMESTAMP,
     '$2b$10$hmz6N7oZuvyz1xLaR8Mr5.iFBzNYbOhZkNNUNB3wZkSk07qut.TY.',
     '00000000-0000-0000-0000-000000000003'),

    ('10000000-0000-0000-0000-000000000004',
     'interviewer@ats.local', 'Lê Interviewer', 'INTERVIEWER', 'ACTIVE', true, false, false,
     CURRENT_TIMESTAMP, CURRENT_TIMESTAMP,
     '$2b$10$hmz6N7oZuvyz1xLaR8Mr5.iFBzNYbOhZkNNUNB3wZkSk07qut.TY.',
     '00000000-0000-0000-0000-000000000001');

-- Scorecard Templates
INSERT INTO scorecard_templates (id, name, department_id, created_at, last_modified_date) VALUES
    ('20000000-0000-0000-0000-000000000001', 'Backend Technical Interview',
     '00000000-0000-0000-0000-000000000001', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('20000000-0000-0000-0000-000000000002', 'Frontend Technical Interview',
     '00000000-0000-0000-0000-000000000001', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('20000000-0000-0000-0000-000000000003', 'Culture Fit Interview',
     '00000000-0000-0000-0000-000000000003', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Scorecard Criteria
INSERT INTO scorecard_criteria (id, template_id, name, weight, created_at, last_modified_date) VALUES
    ('30000000-0000-0000-0000-000000000001', '20000000-0000-0000-0000-000000000001', 'Data Structures & Algorithms', 2.0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('30000000-0000-0000-0000-000000000002', '20000000-0000-0000-0000-000000000001', 'System Design',               1.5, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('30000000-0000-0000-0000-000000000003', '20000000-0000-0000-0000-000000000001', 'Code Quality',                1.0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('30000000-0000-0000-0000-000000000004', '20000000-0000-0000-0000-000000000002', 'JavaScript / TypeScript',     2.0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('30000000-0000-0000-0000-000000000005', '20000000-0000-0000-0000-000000000002', 'React & State Management',    1.5, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('30000000-0000-0000-0000-000000000006', '20000000-0000-0000-0000-000000000002', 'CSS & Responsive Design',     1.0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('30000000-0000-0000-0000-000000000007', '20000000-0000-0000-0000-000000000003', 'Communication',               1.0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('30000000-0000-0000-0000-000000000008', '20000000-0000-0000-0000-000000000003', 'Teamwork',                    1.0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('30000000-0000-0000-0000-000000000009', '20000000-0000-0000-0000-000000000003', 'Problem Solving Mindset',     1.0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Jobs
INSERT INTO jobs (id, title, description, department_id, hiring_manager_id, created_by, status, headcount, created_at, updated_at) VALUES
    ('40000000-0000-0000-0000-000000000001', 'Senior Frontend Developer', 'React/TS Developer', '00000000-0000-0000-0000-000000000001', '10000000-0000-0000-0000-000000000002', '10000000-0000-0000-0000-000000000002', 'APPROVED', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('40000000-0000-0000-0000-000000000002', 'Product Designer', 'UI/UX Designer', '00000000-0000-0000-0000-000000000002', '10000000-0000-0000-0000-000000000002', '10000000-0000-0000-0000-000000000002', 'APPROVED', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Candidates
INSERT INTO candidates (id, full_name, email, phone, current_company, source, location, experience_years, summary, created_at) VALUES
    ('50000000-0000-0000-0000-000000000001', 'Nguyễn Văn A', 'nva@example.com', '0123456789', 'Company A', 'LinkedIn', 'Ha Noi', 5, 'Senior Frontend with 5 years exp', CURRENT_TIMESTAMP),
    ('50000000-0000-0000-0000-000000000002', 'Trần Thị B', 'ttb@example.com', '0987654321', 'Company B', 'Referral', 'Ho Chi Minh', 3, 'Product Designer', CURRENT_TIMESTAMP);

-- Applications
INSERT INTO applications (id, candidate_id, job_id, stage, status, applied_at, updated_at) VALUES
    ('60000000-0000-0000-0000-000000000001', '50000000-0000-0000-0000-000000000001', '40000000-0000-0000-0000-000000000001', 'OFFER', 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('60000000-0000-0000-0000-000000000002', '50000000-0000-0000-0000-000000000002', '40000000-0000-0000-0000-000000000002', 'APPLIED', 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Offers
INSERT INTO offers (id, application_id, position_title, salary, equity, sign_on_bonus,
                    start_date, expiry_date, status, created_by, created_at, last_modified_date)
VALUES
    ('70000000-0000-0000-0000-000000000001',
     '60000000-0000-0000-0000-000000000001',
     'Senior Frontend Developer', 120000000.00, 10, 15000000.00,
     '2024-06-01', '2024-05-20', 'PENDING_APPROVAL',
     '10000000-0000-0000-0000-000000000002',
     CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

    ('70000000-0000-0000-0000-000000000002',
     '60000000-0000-0000-0000-000000000002',
     'Product Designer', 95000000.00, 5, 10000000.00,
     '2024-07-01', '2024-06-15', 'DRAFT',
     '10000000-0000-0000-0000-000000000002',
     CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Onboarding Tasks (applicationId = 60000000-0000-0000-0000-000000000001)
INSERT INTO onboarding_tasks (id, application_id, title, category, assigned_to, due_date,
                               completed, sort_order, created_at, last_modified_date)
VALUES
    ('80000000-0000-0000-0000-000000000001',
     '60000000-0000-0000-0000-000000000001',
     'Send Welcome Email', 'Before Start Date', 'HR Team',         '2024-05-25', true,  0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('80000000-0000-0000-0000-000000000002',
     '60000000-0000-0000-0000-000000000001',
     'Prepare Laptop & Accounts', 'Before Start Date', 'IT Department', '2024-05-30', true,  1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('80000000-0000-0000-0000-000000000003',
     '60000000-0000-0000-0000-000000000001',
     'Complete Background Check', 'Before Start Date', 'HR Team',   '2024-05-28', false, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('80000000-0000-0000-0000-000000000004',
     '60000000-0000-0000-0000-000000000001',
     'Office Tour & Introductions', 'Day 1', 'Buddy',               '2024-06-01', false, 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('80000000-0000-0000-0000-000000000005',
     '60000000-0000-0000-0000-000000000001',
     'IT Setup & System Access', 'Day 1', 'IT Department',          '2024-06-01', false, 4, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('80000000-0000-0000-0000-000000000006',
     '60000000-0000-0000-0000-000000000001',
     'Team Lunch', 'Day 1', 'Engineering Manager',                  '2024-06-01', false, 5, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
