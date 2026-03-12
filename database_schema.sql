-- =============================================
-- ATS ENTERPRISE MINI - POSTGRESQL SCHEMA
-- WITH UUID SUPPORT
-- =============================================

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- =========================
-- ENUM TYPES
-- =========================

CREATE TYPE user_status_enum AS ENUM ('ACTIVE', 'INACTIVE');

CREATE TYPE job_status_enum AS ENUM (
    'DRAFT',
    'PENDING_APPROVAL',
    'APPROVED',
    'REJECTED',
    'CLOSED'
);

CREATE TYPE application_stage_enum AS ENUM (
    'APPLIED',
    'SCREENING',
    'INTERVIEW',
    'OFFER',
    'HIRED',
    'REJECTED'
);

CREATE TYPE application_status_enum AS ENUM (
    'ACTIVE',
    'WITHDRAWN',
    'REJECTED'
);

CREATE TYPE interview_status_enum AS ENUM (
    'SCHEDULED',
    'COMPLETED',
    'CANCELLED'
);

CREATE TYPE interview_type_enum AS ENUM (
    'ONLINE',
    'OFFLINE'
);

CREATE TYPE offer_status_enum AS ENUM (
    'DRAFT',
    'PENDING_APPROVAL',
    'APPROVED',
    'REJECTED',
    'SENT'
);

CREATE TYPE approval_status_enum AS ENUM (
    'APPROVED',
    'REJECTED'
);

-- =========================
-- CORE / AUTH MODULE
-- =========================

CREATE TABLE roles (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(100) UNIQUE NOT NULL,
    description TEXT,
    is_system_role BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE permissions (
    key VARCHAR(100) PRIMARY KEY,
    label VARCHAR(255) NOT NULL,
    category VARCHAR(100) NOT NULL
);

CREATE TABLE role_permissions (
    role_id UUID REFERENCES roles(id) ON DELETE CASCADE,
    permission_key VARCHAR(100) REFERENCES permissions(key) ON DELETE CASCADE,
    PRIMARY KEY (role_id, permission_key)
);

CREATE TABLE departments (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(255) UNIQUE NOT NULL,
    description TEXT,
    manager_id UUID, -- Setup FK later when users table is ready
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash TEXT NOT NULL,
    full_name VARCHAR(255),
    avatar_url VARCHAR(1000),
    active BOOLEAN DEFAULT FALSE,
    deleted BOOLEAN DEFAULT FALSE,
    account_locked BOOLEAN DEFAULT FALSE,
    department_id UUID REFERENCES departments(id) ON DELETE SET NULL,
    role_id UUID REFERENCES roles(id) ON DELETE SET NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

ALTER TABLE departments ADD CONSTRAINT fk_department_manager FOREIGN KEY (manager_id) REFERENCES users(id) ON DELETE SET NULL;

CREATE TABLE refresh_tokens (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID REFERENCES users(id) ON DELETE CASCADE,
    token TEXT NOT NULL UNIQUE,
    expiry_date TIMESTAMP NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- =========================
-- JOB MODULE
-- =========================

CREATE TABLE jobs (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    title VARCHAR(255) NOT NULL,
    description TEXT,
    department_id UUID REFERENCES departments(id),
    hiring_manager_id UUID REFERENCES users(id),
    created_by UUID REFERENCES users(id),
    status job_status_enum DEFAULT 'DRAFT',
    headcount INT DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE job_approvals (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    job_id UUID REFERENCES jobs(id) ON DELETE CASCADE,
    approved_by UUID REFERENCES users(id),
    status approval_status_enum,
    comment TEXT,
    approved_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- =========================
-- CANDIDATE MODULE
-- =========================

CREATE TABLE candidates (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    full_name VARCHAR(255) NOT NULL,
    email VARCHAR(255),
    phone VARCHAR(50),
    current_company VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE applications (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    candidate_id UUID REFERENCES candidates(id) ON DELETE CASCADE,
    job_id UUID REFERENCES jobs(id) ON DELETE CASCADE,
    stage application_stage_enum DEFAULT 'APPLIED',
    status application_status_enum DEFAULT 'ACTIVE',
    applied_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(candidate_id, job_id)
);

CREATE TABLE candidate_stage_history (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    application_id UUID REFERENCES applications(id) ON DELETE CASCADE,
    from_stage application_stage_enum,
    to_stage application_stage_enum,
    changed_by UUID REFERENCES users(id),
    changed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE candidate_notes (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    application_id UUID REFERENCES applications(id) ON DELETE CASCADE,
    created_by UUID REFERENCES users(id),
    content TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- =========================
-- INTERVIEW MODULE
-- =========================

CREATE TABLE interviews (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    application_id UUID REFERENCES applications(id) ON DELETE CASCADE,
    scheduled_at TIMESTAMP NOT NULL,
    location VARCHAR(255),
    type interview_type_enum,
    created_by UUID REFERENCES users(id),
    status interview_status_enum DEFAULT 'SCHEDULED'
);

CREATE TABLE interview_participants (
    interview_id UUID REFERENCES interviews(id) ON DELETE CASCADE,
    user_id UUID REFERENCES users(id) ON DELETE CASCADE,
    role VARCHAR(50), -- INTERVIEWER, OBSERVER
    PRIMARY KEY (interview_id, user_id)
);

-- =========================
-- SCORING MODULE
-- =========================

CREATE TABLE scorecard_templates (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(255) NOT NULL,
    department_id UUID REFERENCES departments(id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE scorecard_criteria (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    template_id UUID REFERENCES scorecard_templates(id) ON DELETE CASCADE,
    name VARCHAR(255) NOT NULL,
    weight NUMERIC(5,2) DEFAULT 1.0
);

CREATE TABLE interview_scores (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    interview_id UUID REFERENCES interviews(id) ON DELETE CASCADE,
    interviewer_id UUID REFERENCES users(id),
    criterion_id UUID REFERENCES scorecard_criteria(id),
    score INT CHECK (score BETWEEN 1 AND 5),
    comment TEXT
);

-- =========================
-- OFFER MODULE
-- =========================

CREATE TABLE offers (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    application_id UUID REFERENCES applications(id) ON DELETE CASCADE,
    salary NUMERIC(15,2),
    position_title VARCHAR(255),
    status offer_status_enum DEFAULT 'DRAFT',
    created_by UUID REFERENCES users(id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE offer_approvals (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    offer_id UUID REFERENCES offers(id) ON DELETE CASCADE,
    approved_by UUID REFERENCES users(id),
    status approval_status_enum,
    comment TEXT,
    approved_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- =========================
-- SYSTEM MODULE
-- =========================

CREATE TABLE notifications (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID REFERENCES users(id) ON DELETE CASCADE,
    title VARCHAR(255),
    content TEXT,
    type VARCHAR(50),
    link VARCHAR(255),
    is_read BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE audit_logs (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID REFERENCES users(id),
    action VARCHAR(100),
    entity_type VARCHAR(100),
    entity_id UUID,
    old_value JSONB,
    new_value JSONB,
    ip_address INET,
    user_agent TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE system_configs (
    key VARCHAR(100) PRIMARY KEY,
    value TEXT NOT NULL,
    updated_by UUID REFERENCES users(id) ON DELETE SET NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- =========================
-- INDEXES (IMPORTANT FOR ATS)
-- =========================

CREATE INDEX idx_users_department ON users(department_id);
CREATE INDEX idx_jobs_department ON jobs(department_id);
CREATE INDEX idx_applications_job ON applications(job_id);
CREATE INDEX idx_applications_stage ON applications(stage);
CREATE INDEX idx_interviews_application ON interviews(application_id);
CREATE INDEX idx_notifications_user ON notifications(user_id);
CREATE INDEX idx_audit_entity ON audit_logs(entity_type, entity_id);

-- =============================================
-- SEED DATA
-- =============================================
INSERT INTO system_configs (key, value) VALUES 
('company_name', 'Tech ATS Inc.'),
('timezone', 'UTC+7'),
('session_timeout_mins', '60'),
('email_notify_new_app', 'true'),
('email_notify_interview', 'true');
