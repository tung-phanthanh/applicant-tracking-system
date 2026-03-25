-- admin@example.com / admin123
-- manager@example.com / manager123
-- hr@example.com / hr123

USE applicant_tracking;

-- Departments
INSERT INTO departments (id,name,description) VALUES
('11111111-1111-1111-1111-111111111111','Engineering','Engineering department'),
('22222222-2222-2222-2222-222222222222','Product','Product management'),
('33333333-3333-3333-3333-333333333333','HR','Human resources');

-- Users
INSERT INTO users (id,email,password_hash,full_name,avatar_url,active,deleted,account_locked,role,department_id,created_by,created_at)
VALUES
('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa','admin@example.com','$2a$10$EIXw5b4becP2K1p8mqLr7uCKsZmfjC4iV8oOqfpx0WcPLTIzY3kG6','Admin User','','1','0','0','ADMIN','11111111-1111-1111-1111-111111111111',NULL,NOW()),
('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb','manager@example.com','$2a$10$e0NRcaKcOZAPIiZNjMTpUOCIYBmqJ6D/m5N/t3WbS3hL2VyNGrJnW','HR Manager','','1','0','0','HR_MANAGER','11111111-1111-1111-1111-111111111111',NULL,NOW()),
('dddddddd-dddd-dddd-dddd-dddddddddddd','hr@example.com','$2a$10$J9nw5rsP.8ZGOiY90VMC6.ZyIGPj1G9f8g6v3TpaJB3Djikx97C4i','HR User','','1','0','0','HR','33333333-3333-3333-3333-333333333333',NULL,NOW());

-- Jobs
INSERT INTO jobs (id,title,description,location,salary,department_id,hiring_manager_id,status,headcount,created_by,created_at)
VALUES
('10101010-1010-1010-1010-101010101010','Senior Backend Engineer','Design backend services','Ho Chi Minh','$45k-$60k','11111111-1111-1111-1111-111111111111','bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb','OPEN',2,'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb',NOW()),
('12121212-1212-1212-1212-121212121212','Product Owner','Own product roadmap','Ho Chi Minh','$40k-$55k','22222222-2222-2222-2222-222222222222','bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb','OPEN',1,'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb',NOW());

-- Candidates
INSERT INTO candidates (id,full_name,email,phone,current_company,source,location,experience_years,summary,created_by,created_at)
VALUES
('d1d1d1d1-d1d1-d1d1-d1d1-d1d1d1d1d1d1','Alice Nguyen','alice@example.com','+84123456789','Acme Corp','LinkedIn','HCMC',5,'Experienced backend engineer','cccccccc-cccc-cccc-cccc-cccccccccccc',NOW()),
('d2d2d2d2-d2d2-d2d2-d2d2-d2d2d2d2d2d2','Bob Tran','bob@example.com','+84987654321','Beta Co','Referral','Hanoi',3,'Fullstack developer','cccccccc-cccc-cccc-cccc-cccccccccccc',NOW());

-- Applications
INSERT INTO applications (id,candidate_id,job_id,stage,status,applied_at,created_by,created_at)
VALUES
('e1e1e1e1-e1e1-e1e1-e1e1-e1e1e1e1e1e1','d1d1d1d1-d1d1-d1d1-d1d1-d1d1d1d1d1d1','10101010-1010-1010-1010-101010101010','APPLIED','ACTIVE',NOW(),'cccccccc-cccc-cccc-cccc-cccccccccccc',NOW()),
('e2e2e2e2-e2e2-e2e2-e2e2-e2e2e2e2e2e2','d2d2d2d2-d2d2-d2d2-d2d2-d2d2d2d2d2d2','12121212-1212-1212-1212-121212121212','APPLIED','ACTIVE',NOW(),'cccccccc-cccc-cccc-cccc-cccccccccccc',NOW());

-- Scorecard templates
INSERT INTO scorecard_templates (id,name,department_id,created_by,created_at)
VALUES
('f1f1f1f1-f1f1-f1f1-f1f1-f1f1f1f1f1f1','Engineering Template','11111111-1111-1111-1111-111111111111','bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb',NOW());

-- Scorecard criteria
INSERT INTO scorecard_criteria (id,template_id,name,weight,created_by,created_at)
VALUES
('g1g1g1g1-g1g1-g1g1-g1g1-g1g1g1g1g1g1','f1f1f1f1-f1f1-f1f1-f1f1-f1f1f1f1f1f1','Coding Skills',40,'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb',NOW()),
('g2g2g2g2-g2g2-g2g2-g2g2-g2g2g2g2g2g2','f1f1f1f1-f1f1-f1f1-f1f1-f1f1f1f1f1f1','Communication',30,'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb',NOW()),
('g3g3g3g3-g3g3-g3g3-g3g3-g3g3g3g3g3g3','f1f1f1f1-f1f1-f1f1-f1f1-f1f1f1f1f1f1','Problem Solving',30,'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb',NOW());

-- Interviews
INSERT INTO interviews (id,application_id,template_id,scheduled_at,location,meeting_link,type,status,created_by,created_at)
VALUES
('h1h1h1h1-h1h1-h1h1-h1h1-h1h1h1h1h1h1','e1e1e1e1-e1e1-e1e1-e1e1-e1e1e1e1e1e1','f1f1f1f1-f1f1-f1f1-f1f1-f1f1f1f1f1f1',NOW() + INTERVAL 2 DAY,'Video','https://meet.example.com/123','TECHNICAL','SCHEDULED','bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb',NOW());

-- Interview participants
INSERT INTO interview_participants (interview_id,user_id,role,feedback,overall_score)
VALUES
('h1h1h1h1-h1h1-h1h1-h1h1-h1h1h1h1h1h1','bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb','Interviewer',NULL,NULL);

-- Interview scores
INSERT INTO interview_scores (id,interview_id,user_id,criterion_id,score,comment,created_by,created_at)
VALUES
('i1i1i1i1-i1i1-i1i1-i1i1-i1i1i1i1i1i1','h1h1h1h1-h1h1-h1h1-h1h1-h1h1h1h1h1h1','bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb','g1g1g1g1-g1g1-g1g1-g1g1-g1g1g1g1g1g1',8,'Strong code base','bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb',NOW());

-- Candidate notes
INSERT INTO candidate_notes (id,application_id,content,created_by,created_at)
VALUES
('j1j1j1j1-j1j1-j1j1-j1j1-j1j1j1j1j1j1','e1e1e1e1-e1e1-e1e1-e1e1-e1e1e1e1e1e1','Positive initial screen','cccccccc-cccc-cccc-cccc-cccccccccccc',NOW());

-- Candidate documents
INSERT INTO candidate_documents (id,candidate_id,file_name,file_url,file_type,file_size_bytes,uploaded_at,created_by,created_at)
VALUES
('k1k1k1k1-k1k1-k1k1-k1k1-k1k1k1k1k1k1','d1d1d1d1-d1d1-d1d1-d1d1-d1d1d1d1d1d1','CV.pdf','https://cdn.example.com/cv/alice.pdf','application/pdf',34567,NOW(),'cccccccc-cccc-cccc-cccc-cccccccccccc',NOW());

-- Candidate stage history
INSERT INTO candidate_stage_history (id,application_id,from_stage,to_stage,created_by,created_at)
VALUES
('l1l1l1l1-l1l1-l1l1-l1l1-l1l1l1l1l1l1','e1e1e1e1-e1e1-e1e1-e1e1-e1e1e1e1e1e1','APPLIED','SCREENING','bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb',NOW());

-- Offers
INSERT INTO offers (id,application_id,salary,position_title,status,created_by,created_at)
VALUES
('m1m1m1m1-m1m1-m1m1-m1m1-m1m1m1m1m1m1','e1e1e1e1-e1e1-e1e1-e1e1-e1e1e1e1e1e1',120000,'Senior Backend Engineer','DRAFT','bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb',NOW());

-- Job approvals
INSERT INTO job_approvals (id,job_id,approved_by,status,comment,created_by,created_at)
VALUES
('n1n1n1n1-n1n1-n1n1-n1n1-n1n1n1n1n1n1','10101010-1010-1010-1010-101010101010','bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb','PENDING','Awaiting review','bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb',NOW());

-- Offer approvals
INSERT INTO offer_approvals (id,offer_id,approved_by,status,comment,created_by,created_at)
VALUES
('o1o1o1o1-o1o1-o1o1-o1o1-o1o1o1o1o1o1','m1m1m1m1-m1m1-m1m1-m1m1-m1m1m1m1m1m1','bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb','PENDING','Need salary confirmation','bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb',NOW());

-- Refresh tokens
INSERT INTO refresh_tokens (id,token,user_id,expiry_date,revoked,created_by,created_at)
VALUES
('p1p1p1p1-p1p1-p1p1-p1p1-p1p1p1p1p1p1','rt-token-123','cccccccc-cccc-cccc-cccc-cccccccccccc',NOW() + INTERVAL 30 DAY,0,'cccccccc-cccc-cccc-cccc-cccccccccccc',NOW());

-- Notifications
INSERT INTO notifications (user_id,type,title,message,is_read,reference_id,created_at)
VALUES
('cccccccc-cccc-cccc-cccc-cccccccccccc','INFO','Welcome','Welcome to the Applicant Tracking System',0,NULL,NOW());

-- Audit logs
INSERT INTO audit_logs (user_id,action,entity_type,entity_id,old_value,new_value,ip_address,user_agent,created_at)
VALUES
('cccccccc-cccc-cccc-cccc-cccccccccccc','CREATE','candidate','d1d1d1d1-d1d1-d1d1-d1d1-d1d1d1d1d1d1',NULL,'{"full_name":"Alice Nguyen"}','127.0.0.1','PostmanRuntime/7.0',NOW());

-- System config
INSERT INTO system_configs (`key`,`value`,updated_by,updated_at)
VALUES
('app.name','Enterprise ATS','aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa',NOW()),
('email.from','no-reply@example.com','aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa',NOW());
