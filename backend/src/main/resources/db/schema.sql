-- Regenerated MySQL schema from JPA entity model (entity + enum aware)
SET FOREIGN_KEY_CHECKS = 0;

CREATE TABLE departments (
    id CHAR(36) NOT NULL,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(1000),
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE users (
    id CHAR(36) NOT NULL,
    email VARCHAR(100) NOT NULL,
    password_hash TEXT,
    full_name VARCHAR(100),
    avatar_url VARCHAR(1000),
    active BOOLEAN NOT NULL DEFAULT FALSE,
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    account_locked BOOLEAN NOT NULL DEFAULT FALSE,
    role VARCHAR(50) NOT NULL,
    department_id CHAR(36),
    reset_token VARCHAR(100),
    reset_token_expires_at DATETIME,
    activation_token VARCHAR(100),
    activation_token_expires_at DATETIME,
    created_by CHAR(36),
    created_at DATETIME NOT NULL,
    modified_by CHAR(36),
    last_modified_date DATETIME,
    PRIMARY KEY (id),
    UNIQUE KEY uq_users_email (email),
    CONSTRAINT fk_users_dept FOREIGN KEY (department_id) REFERENCES departments(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE jobs (
    id CHAR(36) NOT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    location VARCHAR(255),
    salary VARCHAR(255),
    department_id CHAR(36),
    hiring_manager_id CHAR(36),
    status VARCHAR(50) NOT NULL DEFAULT 'DRAFT',
    headcount INT NOT NULL DEFAULT 1,
    created_by CHAR(36),
    created_at DATETIME NOT NULL,
    modified_by CHAR(36),
    last_modified_date DATETIME,
    PRIMARY KEY (id),
    CONSTRAINT fk_jobs_dept FOREIGN KEY (department_id) REFERENCES departments(id) ON DELETE SET NULL,
    CONSTRAINT fk_jobs_hiring_manager FOREIGN KEY (hiring_manager_id) REFERENCES users(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE candidates (
    id CHAR(36) NOT NULL,
    full_name VARCHAR(255) NOT NULL,
    email VARCHAR(255),
    phone VARCHAR(50),
    current_company VARCHAR(255),
    source VARCHAR(255),
    location VARCHAR(255),
    experience_years INT,
    summary TEXT,
    created_by CHAR(36),
    created_at DATETIME NOT NULL,
    modified_by CHAR(36),
    last_modified_date DATETIME,
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE applications (
    id CHAR(36) NOT NULL,
    candidate_id CHAR(36),
    job_id CHAR(36),
    stage VARCHAR(50) NOT NULL DEFAULT 'APPLIED',
    status VARCHAR(50) NOT NULL DEFAULT 'ACTIVE',
    applied_at DATETIME,
    created_by CHAR(36),
    created_at DATETIME NOT NULL,
    modified_by CHAR(36),
    last_modified_date DATETIME,
    PRIMARY KEY (id),
    UNIQUE KEY uq_applications_candidate_job (candidate_id, job_id),
    CONSTRAINT fk_applications_candidate FOREIGN KEY (candidate_id) REFERENCES candidates(id) ON DELETE SET NULL,
    CONSTRAINT fk_applications_job FOREIGN KEY (job_id) REFERENCES jobs(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE scorecard_templates (
    id CHAR(36) NOT NULL,
    name VARCHAR(255),
    department_id CHAR(36),
    created_by CHAR(36),
    created_at DATETIME NOT NULL,
    modified_by CHAR(36),
    last_modified_date DATETIME,
    PRIMARY KEY (id),
    CONSTRAINT fk_scorecard_templates_dept FOREIGN KEY (department_id) REFERENCES departments(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE scorecard_criteria (
    id CHAR(36) NOT NULL,
    template_id CHAR(36) NOT NULL,
    name VARCHAR(255),
    weight DECIMAL(15,2) NOT NULL DEFAULT 1.00,
    created_by CHAR(36),
    created_at DATETIME NOT NULL,
    modified_by CHAR(36),
    last_modified_date DATETIME,
    PRIMARY KEY (id),
    CONSTRAINT fk_scorecard_criteria_template FOREIGN KEY (template_id) REFERENCES scorecard_templates(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE interviews (
    id CHAR(36) NOT NULL,
    application_id CHAR(36) NOT NULL,
    template_id CHAR(36) NOT NULL,
    scheduled_at DATETIME,
    started_at DATETIME,
    ended_at DATETIME,
    location VARCHAR(255),
    meeting_link VARCHAR(1000),
    type VARCHAR(50),
    status VARCHAR(50) NOT NULL DEFAULT 'SCHEDULED',
    created_by CHAR(36),
    created_at DATETIME NOT NULL,
    modified_by CHAR(36),
    last_modified_date DATETIME,
    PRIMARY KEY (id),
    CONSTRAINT fk_interviews_application FOREIGN KEY (application_id) REFERENCES applications(id) ON DELETE CASCADE,
    CONSTRAINT fk_interviews_template FOREIGN KEY (template_id) REFERENCES scorecard_templates(id) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE interview_participants (
    interview_id CHAR(36) NOT NULL,
    user_id CHAR(36) NOT NULL,
    role VARCHAR(50),
    feedback TEXT,
    overall_score DECIMAL(10,2),
    PRIMARY KEY (interview_id,user_id),
    CONSTRAINT fk_interview_participants_interview FOREIGN KEY (interview_id) REFERENCES interviews(id) ON DELETE CASCADE,
    CONSTRAINT fk_interview_participants_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE interview_scores (
    id CHAR(36) NOT NULL,
    interview_id CHAR(36) NOT NULL,
    user_id CHAR(36) NOT NULL,
    criterion_id CHAR(36) NOT NULL,
    score INT,
    comment TEXT,
    created_by CHAR(36),
    created_at DATETIME NOT NULL,
    modified_by CHAR(36),
    last_modified_date DATETIME,
    PRIMARY KEY (id),
    UNIQUE KEY uq_interview_scores_unique (interview_id,user_id,criterion_id),
    CONSTRAINT fk_interview_scores_interview FOREIGN KEY (interview_id) REFERENCES interviews(id) ON DELETE CASCADE,
    CONSTRAINT fk_interview_scores_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_interview_scores_criterion FOREIGN KEY (criterion_id) REFERENCES scorecard_criteria(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE candidate_notes (
    id CHAR(36) NOT NULL,
    application_id CHAR(36),
    content TEXT,
    created_by CHAR(36),
    created_at DATETIME NOT NULL,
    modified_by CHAR(36),
    last_modified_date DATETIME,
    PRIMARY KEY (id),
    CONSTRAINT fk_candidate_notes_application FOREIGN KEY (application_id) REFERENCES applications(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE candidate_documents (
    id CHAR(36) NOT NULL,
    candidate_id CHAR(36),
    file_name VARCHAR(255) NOT NULL,
    file_url VARCHAR(1000) NOT NULL,
    file_type VARCHAR(50),
    file_size_bytes BIGINT,
    uploaded_at DATETIME,
    created_by CHAR(36),
    created_at DATETIME NOT NULL,
    modified_by CHAR(36),
    last_modified_date DATETIME,
    PRIMARY KEY (id),
    CONSTRAINT fk_candidate_documents_candidate FOREIGN KEY (candidate_id) REFERENCES candidates(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE candidate_stage_history (
    id CHAR(36) NOT NULL,
    application_id CHAR(36),
    from_stage VARCHAR(50),
    to_stage VARCHAR(50),
    created_by CHAR(36),
    created_at DATETIME NOT NULL,
    modified_by CHAR(36),
    last_modified_date DATETIME,
    PRIMARY KEY (id),
    CONSTRAINT fk_candidate_stage_history_application FOREIGN KEY (application_id) REFERENCES applications(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE offers (
    id CHAR(36) NOT NULL,
    application_id CHAR(36),
    salary DECIMAL(15,2),
    position_title VARCHAR(255),
    status VARCHAR(50) NOT NULL DEFAULT 'DRAFT',
    created_by CHAR(36),
    created_at DATETIME NOT NULL,
    modified_by CHAR(36),
    last_modified_date DATETIME,
    PRIMARY KEY (id),
    CONSTRAINT fk_offers_application FOREIGN KEY (application_id) REFERENCES applications(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE job_approvals (
    id CHAR(36) NOT NULL,
    job_id CHAR(36),
    approved_by CHAR(36),
    status VARCHAR(50),
    comment TEXT,
    created_by CHAR(36),
    created_at DATETIME NOT NULL,
    modified_by CHAR(36),
    last_modified_date DATETIME,
    PRIMARY KEY (id),
    CONSTRAINT fk_job_approvals_job FOREIGN KEY (job_id) REFERENCES jobs(id) ON DELETE CASCADE,
    CONSTRAINT fk_job_approvals_user FOREIGN KEY (approved_by) REFERENCES users(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE offer_approvals (
    id CHAR(36) NOT NULL,
    offer_id CHAR(36),
    approved_by CHAR(36),
    status VARCHAR(50),
    comment TEXT,
    created_by CHAR(36),
    created_at DATETIME NOT NULL,
    modified_by CHAR(36),
    last_modified_date DATETIME,
    PRIMARY KEY (id),
    CONSTRAINT fk_offer_approvals_offer FOREIGN KEY (offer_id) REFERENCES offers(id) ON DELETE CASCADE,
    CONSTRAINT fk_offer_approvals_user FOREIGN KEY (approved_by) REFERENCES users(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE refresh_tokens (
    id CHAR(36) NOT NULL,
    token TEXT NOT NULL,
    user_id CHAR(36) NOT NULL,
    expiry_date DATETIME NOT NULL,
    revoked BOOLEAN NOT NULL DEFAULT FALSE,
    created_by CHAR(36),
    created_at DATETIME NOT NULL,
    modified_by CHAR(36),
    last_modified_date DATETIME,
    PRIMARY KEY (id),
    UNIQUE KEY uq_refresh_tokens_token (token),
    CONSTRAINT fk_refresh_tokens_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE notifications (
    id BIGINT NOT NULL AUTO_INCREMENT,
    user_id CHAR(36) NOT NULL,
    type VARCHAR(50) NOT NULL,
    title VARCHAR(255) NOT NULL,
    message TEXT NOT NULL,
    is_read BOOLEAN NOT NULL DEFAULT FALSE,
    reference_id BIGINT,
    created_at DATETIME NOT NULL,
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE audit_logs (
    id BIGINT NOT NULL AUTO_INCREMENT,
    user_id CHAR(36),
    action VARCHAR(255) NOT NULL,
    entity_type VARCHAR(255) NOT NULL,
    entity_id VARCHAR(255),
    old_value TEXT,
    new_value TEXT,
    ip_address VARCHAR(255),
    user_agent VARCHAR(1024),
    created_at DATETIME NOT NULL,
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE system_configs (
    config_key VARCHAR(100) NOT NULL,
    value TEXT NOT NULL,
    updated_by CHAR(36),
    updated_at DATETIME,
    PRIMARY KEY (config_key)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

SET FOREIGN_KEY_CHECKS = 1;
