CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- USERS
CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    username VARCHAR(100) UNIQUE NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    status VARCHAR(20) NOT NULL,
    password_hash TEXT NOT NULL,
    role VARCHAR(50) NOT NULL,
    phone VARCHAR(20) UNIQUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Notification
CREATE TABLE notification_settings (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL UNIQUE REFERENCES users(id) ON DELETE CASCADE,
    scans_enabled BOOLEAN DEFAULT true,
    issues_enabled BOOLEAN DEFAULT true,
    system_enabled BOOLEAN DEFAULT true,
    reports_enabled BOOLEAN DEFAULT false,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- SonarConfig
CREATE TABLE sonarqube_config (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL UNIQUE REFERENCES users(id) ON DELETE CASCADE,
    server_url VARCHAR(500),
    auth_token VARCHAR(500),
    organization VARCHAR(255),
    -- Angular Settings
    angular_run_npm BOOLEAN DEFAULT false,
    angular_coverage BOOLEAN DEFAULT false,
    angular_ts_files BOOLEAN DEFAULT false,
    angular_exclusions TEXT,
    -- Spring Settings
    spring_run_tests BOOLEAN DEFAULT false,
    spring_jacoco BOOLEAN DEFAULT false,
    spring_build_tool VARCHAR(50),
    spring_jdk_version INTEGER,
    -- Quality Gates
    qg_fail_on_error BOOLEAN DEFAULT false,
    qg_coverage_threshold INTEGER DEFAULT 80,
    qg_max_bugs INTEGER DEFAULT 0,
    qg_max_vulnerabilities INTEGER DEFAULT 0,
    qg_max_code_smells INTEGER DEFAULT 0,

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- PROJECTS
CREATE TABLE projects (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(255) NOT NULL,
    repository_url TEXT NOT NULL,
    project_type VARCHAR(50),
    sonar_project_key VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- SCANS
CREATE TABLE scans (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    project_id UUID NOT NULL,
    status VARCHAR(50),
    started_at TIMESTAMP,
    completed_at TIMESTAMP,
    quality_gate VARCHAR(20),
    metrics JSONB,
    log_file_path TEXT,

    CONSTRAINT fk_scans_project
        FOREIGN KEY (project_id)
        REFERENCES projects(id)
        ON DELETE CASCADE
);

-- Report history
CREATE TABLE report_history (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    project_id UUID NOT NULL REFERENCES projects(id) ON DELETE CASCADE,

    -- Report metadata
    project_name VARCHAR(255) NOT NULL,
    date_from DATE NOT NULL,
    date_to DATE NOT NULL,
    format VARCHAR(20) NOT NULL,              -- 'PDF', 'Excel', 'Word', 'PowerPoint'
    generated_by VARCHAR(100),                 -- username
    generated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    -- Selected sections
    include_quality_gate BOOLEAN DEFAULT false,
    include_issue_breakdown BOOLEAN DEFAULT false,
    include_security_analysis BOOLEAN DEFAULT false,

    -- Optional: เก็บ snapshot data (JSON แทน table แยก)
    snapshot_data JSONB,                       -- เก็บ scans, issues, securityData

    file_size_bytes BIGINT
);

CREATE TABLE issues (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    issue_key VARCHAR(255) NOT NULL UNIQUE,
    project_id UUID NOT NULL,

    type VARCHAR(50),
    severity VARCHAR(20),
    rule_key VARCHAR(100),
    component TEXT,
    line INT,
    message TEXT,
    assigned_to UUID,
    status VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_issues_project
        FOREIGN KEY (project_id)
        REFERENCES projects(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_issues_user
        FOREIGN KEY (assigned_to)
        REFERENCES users(id)
);

--SCAN ISSUE
CREATE TABLE scan_issues (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),

    scan_id UUID NOT NULL,
    issue_id UUID NOT NULL,

    CONSTRAINT fk_scan_issues_scan
        FOREIGN KEY (scan_id)
        REFERENCES scans(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_scan_issues_issue
        FOREIGN KEY (issue_id)
        REFERENCES issues(id)
        ON DELETE CASCADE,

    CONSTRAINT uq_scan_issue UNIQUE (scan_id, issue_id)
);

--ISSUES Details
CREATE TABLE issue_details (
    issue_id UUID PRIMARY KEY,
    description TEXT,
    vulnerable_code TEXT,
    recommended_fix TEXT,

    CONSTRAINT fk_issue_details_issue
        FOREIGN KEY (issue_id)
        REFERENCES issues(id)
        ON DELETE CASCADE
);

-- COMMENTS
CREATE TABLE comments (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    issue_id UUID NOT NULL,
    user_id UUID NOT NULL,
    comment TEXT NOT NULL,
    parent_comment_id UUID REFERENCES comments(id) ON DELETE CASCADE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_comments_issue
        FOREIGN KEY (issue_id)
        REFERENCES issues(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_comments_user
        FOREIGN KEY (user_id)
        REFERENCES users(id)
);

-- Notification Data
CREATE TABLE notifications (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    type VARCHAR(50) NOT NULL,          -- 'Scans', 'Issues', 'System', 'Reports'
    title VARCHAR(255),
    message TEXT NOT NULL,
    is_read BOOLEAN DEFAULT false,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    -- เชื่อมโยงกับ entity ที่เกี่ยวข้อง
    related_project_id UUID REFERENCES projects(id) ON DELETE SET NULL,
    related_scan_id UUID REFERENCES scans(id) ON DELETE SET NULL,
    related_issue_id UUID REFERENCES issues(id) ON DELETE SET NULL,
    related_comment_id UUID REFERENCES comments(id) ON DELETE SET NULL
);

-- INDEXES (For Performance)
CREATE INDEX idx_scans_project_id ON scans(project_id);
CREATE INDEX idx_scan_issues_scan_id ON scan_issues(scan_id);
CREATE INDEX idx_scan_issues_issue_id ON scan_issues(issue_id);
CREATE INDEX idx_issues_assigned_to ON issues(assigned_to);

-- PASSWORD RESET TOKENS
CREATE TABLE password_reset_tokens (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  user_id UUID NOT NULL,
  token_hash TEXT NOT NULL,
  expires_at TIMESTAMP NOT NULL,
  used_at TIMESTAMP NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_prt_user
    FOREIGN KEY (user_id)
    REFERENCES users(id)
    ON DELETE CASCADE
);

CREATE INDEX idx_prt_token_hash ON password_reset_tokens(token_hash);
CREATE INDEX idx_prt_user_id ON password_reset_tokens(user_id);
CREATE INDEX idx_notifications_user_id ON notifications(user_id);
CREATE INDEX idx_notifications_is_read ON notifications(user_id, is_read);
CREATE INDEX idx_notifications_created_at ON notifications(user_id, created_at DESC);
CREATE INDEX idx_report_history_user_id ON report_history(user_id);
CREATE INDEX idx_report_history_project_id ON report_history(project_id);
CREATE INDEX idx_report_history_generated_at ON report_history(user_id, generated_at DESC);


CREATE TABLE email_verification_tokens (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  user_id UUID NOT NULL,
  token_hash TEXT NOT NULL,
  expires_at TIMESTAMP NOT NULL,
  used_at TIMESTAMP NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

  CONSTRAINT fk_evt_user
    FOREIGN KEY (user_id)
    REFERENCES users(id)
    ON DELETE CASCADE
);

-- token_hash ควร unique กันชน
CREATE UNIQUE INDEX uq_evt_token_hash ON email_verification_tokens(token_hash);

-- index ช่วยหาเร็ว
CREATE INDEX idx_evt_user_id ON email_verification_tokens(user_id);
CREATE INDEX idx_evt_expires_at ON email_verification_tokens(expires_at);
