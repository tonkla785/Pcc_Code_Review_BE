CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- USERS
CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    username VARCHAR(100) UNIQUE NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash TEXT NOT NULL,
    role VARCHAR(50) NOT NULL,
    phone VARCHAR(20) UNIQUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
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
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_comments_issue
        FOREIGN KEY (issue_id)
        REFERENCES issues(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_comments_user
        FOREIGN KEY (user_id)
        REFERENCES users(id)
);

-- INDEXES (For Performance)
CREATE INDEX idx_scans_project_id ON scans(project_id);
CREATE INDEX idx_scan_issues_scan_id ON scan_issues(scan_id);
CREATE INDEX idx_scan_issues_issue_id ON scan_issues(issue_id);
CREATE INDEX idx_issues_assigned_to ON issues(assigned_to);

