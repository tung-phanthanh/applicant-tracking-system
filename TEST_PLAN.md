# Test Plan - Applicant Tracking System (ATS)

## 1. Scorecard Templates

### 1.1 API Endpoints

| Method | Endpoint | Description | Roles |
|--------|----------|-------------|-------|
| POST | `/api/v1/scorecard-templates` | Create new template | HR_MANAGER, HR |
| GET | `/api/v1/scorecard-templates` | Get all templates | HR_MANAGER, HR, INTERVIEWER |
| GET | `/api/v1/scorecard-templates/{id}` | Get template by ID | HR_MANAGER, HR, INTERVIEWER |
| GET | `/api/v1/scorecard-templates/department/{departmentId}` | Get templates by department | HR_MANAGER, HR |
| PUT | `/api/v1/scorecard-templates/{id}` | Update template | HR_MANAGER, HR |
| DELETE | `/api/v1/scorecard-templates/{id}` | Delete template | HR_MANAGER |

### 1.2 Test Cases

#### TC-ST-001: Create Scorecard Template - Success
- **Input**: Valid name, departmentId, criteria list (name + weight)
- **Expected**: 201 Created, return ScorecardTemplateResponse
- **Validations**: name required, criteria non-empty

#### TC-ST-002: Create Scorecard Template - Missing Name
- **Input**: name = null, valid criteria
- **Expected**: 400 Bad Request with validation error

#### TC-ST-003: Create Scorecard Template - Empty Criteria
- **Input**: Valid name, criteria = []
- **Expected**: 400 Bad Request with validation error

#### TC-ST-004: Create Scorecard Template - Invalid Department
- **Input**: Valid data, departmentId = non-existent UUID
- **Expected**: 400 Bad Request or 404 Not Found

#### TC-ST-005: Create Scorecard Template - Unauthorized Role
- **Input**: Valid data, user role = INTERVIEWER
- **Expected**: 403 Forbidden

#### TC-ST-006: Get All Templates - Success
- **Input**: Valid token (HR_MANAGER role)
- **Expected**: 200 OK, list of templates

#### TC-ST-007: Get All Templates - Empty List
- **Input**: Valid token, no templates in DB
- **Expected**: 200 OK, empty list []

#### TC-ST-008: Get Template by ID - Success
- **Input**: Valid template ID
- **Expected**: 200 OK, ScorecardTemplateResponse

#### TC-ST-009: Get Template by ID - Not Found
- **Input**: Non-existent template ID
- **Expected**: 404 Not Found

#### TC-ST-010: Get Templates by Department - Success
- **Input**: Valid department ID with templates
- **Expected**: 200 OK, list of templates for department

#### TC-ST-011: Update Scorecard Template - Success
- **Input**: Valid template ID, updated name/criteria
- **Expected**: 200 OK, updated template

#### TC-ST-012: Update Scorecard Template - Not Found
- **Input**: Non-existent template ID
- **Expected**: 404 Not Found

#### TC-ST-013: Delete Scorecard Template - Success
- **Input**: Valid template ID, HR_MANAGER role
- **Expected**: 204 No Content

#### TC-ST-014: Delete Scorecard Template - Not Found
- **Input**: Non-existent template ID
- **Expected**: 404 Not Found

#### TC-ST-015: Delete Scorecard Template - Unauthorized Role
- **Input**: Valid ID, user role = HR (not HR_MANAGER)
- **Expected**: 403 Forbidden

#### TC-ST-016: Criteria Weight Validation
- **Input**: Valid name, criteria with negative weight
- **Expected**: 400 Bad Request with validation error

#### TC-ST-017: Template Name Uniqueness
- **Input**: Create template with name already existing
- **Expected**: 201 Created (no uniqueness constraint) OR 400 if enforced

---

## 2. Interview Scorecard

### 2.1 API Endpoints

| Method | Endpoint | Description | Roles |
|--------|----------|-------------|-------|
| POST | `/api/v1/interviews/{interviewId}/scores` | Submit interview scores | HR_MANAGER, HR, INTERVIEWER |
| GET | `/api/v1/interviews/{interviewId}/scores` | Get all scorecards | HR_MANAGER, HR |
| GET | `/api/v1/interviews/{interviewId}/scores/me` | Get own scorecard | INTERVIEWER |

### 2.2 Test Cases

#### TC-IS-001: Submit Scores - Success
- **Input**: Valid interviewId, list of ScoreEntry (criterionId, score 1-10, comment)
- **Expected**: 201 Created, return InterviewScorecardResponse
- **Validations**: score range 1-10, valid criterionId

#### TC-IS-002: Submit Scores - Invalid Score Range (Below 1)
- **Input**: score = 0
- **Expected**: 400 Bad Request with validation error

#### TC-IS-003: Submit Scores - Invalid Score Range (Above 10)
- **Input**: score = 11
- **Expected**: 400 Bad Request with validation error

#### TC-IS-004: Submit Scores - Invalid Criterion ID
- **Input**: criterionId = non-existent UUID
- **Expected**: 400 Bad Request or 404 Not Found

#### TC-IS-005: Submit Scores - Duplicate Submission
- **Input**: Same user submits scores twice for same interview
- **Expected**: 400 Bad Request (one submission per interviewer per interview)

#### TC-IS-006: Submit Scores - Interview Not Found
- **Input**: non-existent interviewId
- **Expected**: 404 Not Found

#### TC-IS-007: Get All Scorecards - Success
- **Input**: Valid interviewId
- **Expected**: 200 OK, list of all scorecards for interview

#### TC-IS-008: Get All Scorecards - Empty List
- **Input**: Interview with no scores submitted
- **Expected**: 200 OK, empty list

#### TC-IS-009: Get Own Scorecard - Success
- **Input**: Valid interviewId, current user is interviewer
- **Expected**: 200 OK, user's own scorecard

#### TC-IS-010: Get Own Scorecard - Not Submitted
- **Input**: Valid interviewId, user hasn't submitted scores
- **Expected**: 404 Not Found

#### TC-IS-011: Overall Score Calculation
- **Input**: Multiple criteria with scores 8, 6, 10
- **Expected**: overallScore = average (8+6+10)/3 = 8.0

#### TC-IS-012: Get Scorecards - Unauthorized Role
- **Input**: INTERVIEWER role accessing all scorecards endpoint
- **Expected**: 403 Forbidden

#### TC-IS-013: Score With Empty Comment
- **Input**: Valid score with comment = null or empty
- **Expected**: 201 Created (comment optional)

#### TC-IS-014: Submit All Criteria Required
- **Input**: Only submitting some criteria, missing others
- **Expected**: 201 Created OR 400 (depending on business rule)

---

## 3. Candidate Evaluation Summary

### 3.1 API Endpoints

| Method | Endpoint | Description | Roles |
|--------|----------|-------------|-------|
| GET | `/api/v1/applications/{applicationId}/evaluation` | Get evaluation summary | HR_MANAGER, HR, INTERVIEWER |

### 3.2 Test Cases

#### TC-CE-001: Get Evaluation Summary - Success
- **Input**: Valid applicationId with completed interviews
- **Expected**: 200 OK, CandidateEvaluationResponse with aggregated scores
- **Validations**: applicationId valid UUID

#### TC-CE-002: Get Evaluation Summary - No Interviews
- **Input**: Application with no interviews scheduled
- **Expected**: 200 OK, empty interviews array, overallScore = null/0

#### TC-CE-003: Get Evaluation Summary - Application Not Found
- **Input**: Non-existent applicationId
- **Expected**: 404 Not Found

#### TC-CE-004: Get Evaluation Summary - Calculate Overall Score
- **Input**: Application with multiple interview scorecards
- **Expected**: overallScore = average of all interview overallScores

#### TC-CE-005: Get Evaluation Summary - With Score Details
- **Input**: Valid application
- **Expected**: Response includes nested InterviewEvaluation with ScoreDetail for each criterion

#### TC-CE-006: Get Evaluation Summary - Unauthorized
- **Input**: User without proper role
- **Expected**: 403 Forbidden

#### TC-CE-007: Get Evaluation Summary - Partial Interviews Scored
- **Input**: Some interviews scored, some not
- **Expected**: Only scored interviews included in calculation

#### TC-CE-008: Get Evaluation Summary - Candidate Info
- **Input**: Valid application
- **Expected**: Response includes candidateName, jobTitle from related entities

---

## 4. Candidate Ranking

### 4.1 API Endpoints

| Method | Endpoint | Description | Roles |
|--------|----------|-------------|-------|
| GET | `/api/v1/jobs/{jobId}/ranking` | Get ranked candidates | HR_MANAGER, HR |

### 4.2 Test Cases

#### TC-CR-001: Get Ranking - Success
- **Input**: Valid jobId with multiple applications
- **Expected**: 200 OK, list of CandidateRankingResponse sorted by score desc
- **Validations**: jobId valid UUID

#### TC-CR-002: Get Ranking - Empty List
- **Input**: Job with no applications
- **Expected**: 200 OK, empty list []

#### TC-CR-003: Get Ranking - Job Not Found
- **Input**: Non-existent jobId
- **Expected**: 404 Not Found

#### TC-CR-004: Get Ranking - Sort by Score
- **Input**: Multiple candidates with different scores
- **Expected**: Sorted by score descending (highest first)

#### TC-CR-005: Get Ranking - Tie Breaker Experience
- **Input**: Candidates with same score
- **Expected**: Secondary sort by experienceYears descending

#### TC-CR-006: Get Ranking - Rank Assignment
- **Input**: 5 candidates
- **Expected**: Ranks 1, 2, 3, 4, 5 assigned sequentially

#### TC-CR-007: Get Ranking - Top 3 Icons
- **Input**: Candidates ranked
- **Expected**: rank field indicates position for frontend icons (Trophy, Medal, Award)

#### TC-CR-008: Get Ranking - Include Application Stage
- **Input**: Valid job
- **Expected**: Each candidate includes current stage (APPLIED, INTERVIEW, OFFER, etc.)

#### TC-CR-009: Get Ranking - Score Color Coding Data
- **Input**: Candidates with scores
- **Expected**: score field present for frontend color logic (green >= 7, yellow >= 4, red < 4)

#### TC-CR-010: Get Ranking - Unauthorized Role
- **Input**: INTERVIEWER role
- **Expected**: 403 Forbidden

#### TC-CR-011: Get Ranking - Candidates Without Scores
- **Input**: Applications without interviews/scores
- **Expected**: Handled gracefully (score = null/0, ranked last or excluded)

#### TC-CR-012: Get Ranking - Multiple Interviews Average
- **Input**: Candidate with multiple interviews
- **Expected**: Overall score = average of all interview scores

---

## 5. Offer Draft

### 5.1 API Endpoints

| Method | Endpoint | Description | Roles |
|--------|----------|-------------|-------|
| POST | `/api/v1/offers` | Create offer draft | HR_MANAGER, HR |
| PUT | `/api/v1/offers/{id}` | Update offer draft | HR_MANAGER, HR |
| GET | `/api/v1/offers` | Get all offers | HR_MANAGER, HR |
| GET | `/api/v1/offers/{id}` | Get offer by ID | HR_MANAGER, HR |

### 5.2 Test Cases

#### TC-OD-001: Create Offer Draft - Success
- **Input**: Valid applicationId, salary, positionTitle, startDate, benefits, notes
- **Expected**: 201 Created, OfferResponse with status = DRAFT
- **Validations**: applicationId required, salary > 0, valid date

#### TC-OD-002: Create Offer Draft - Missing Required Fields
- **Input**: Missing applicationId or positionTitle
- **Expected**: 400 Bad Request with validation errors

#### TC-OD-003: Create Offer Draft - Invalid Application
- **Input**: applicationId = non-existent UUID
- **Expected**: 400 Bad Request or 404 Not Found

#### TC-OD-004: Create Offer Draft - Duplicate for Application
- **Input**: Offer already exists for this application
- **Expected**: 201 Created (if allowed) OR 400 (if one offer per application enforced)

#### TC-OD-005: Update Offer Draft - Success
- **Input**: Valid offer ID in DRAFT status, updated fields
- **Expected**: 200 OK, updated OfferResponse

#### TC-OD-006: Update Offer Draft - Non-Draft Status
- **Input**: Offer ID with status != DRAFT (e.g., APPROVED)
- **Expected**: 400 Bad Request (cannot edit non-draft offers)

#### TC-OD-007: Update Offer Draft - Not Found
- **Input**: Non-existent offer ID
- **Expected**: 404 Not Found

#### TC-OD-008: Get All Offers - Success
- **Input**: Valid token
- **Expected**: 200 OK, list sorted by createdAt DESC

#### TC-OD-009: Get All Offers - Filter by Status
- **Input**: Query param status = DRAFT
- **Expected**: 200 OK, only draft offers

#### TC-OD-010: Get Offer by ID - Success
- **Input**: Valid offer ID
- **Expected**: 200 OK, full OfferResponse with candidateName, jobTitle

#### TC-OD-011: Get Offer by ID - Not Found
- **Input**: Non-existent offer ID
- **Expected**: 404 Not Found

#### TC-OD-012: Create Offer - Negative Salary
- **Input**: salary = -50000
- **Expected**: 400 Bad Request with validation error

#### TC-OD-013: Create Offer - Past Start Date
- **Input**: startDate = yesterday
- **Expected**: 400 Bad Request (if date validation enforced) OR 201 Created

#### TC-OD-014: Unauthorized Role
- **Input**: INTERVIEWER role
- **Expected**: 403 Forbidden

#### TC-OD-015: Create Offer - Optional Fields
- **Input**: Only required fields (applicationId, positionTitle), benefits=null, notes=null
- **Expected**: 201 Created with null optional fields

---

## 6. Offer Approval

### 6.1 API Endpoints

| Method | Endpoint | Description | Roles |
|--------|----------|-------------|-------|
| PATCH | `/api/v1/offers/{id}/submit` | Submit for approval | HR_MANAGER, HR |
| POST | `/api/v1/offers/{id}/approval` | Approve or reject | HR_MANAGER |
| GET | `/api/v1/offers/{id}/approvals` | Get approval history | HR_MANAGER, HR |

### 6.2 Test Cases

#### TC-OA-001: Submit for Approval - Success
- **Input**: Valid offer ID in DRAFT status
- **Expected**: 200 OK, status changes to PENDING_APPROVAL

#### TC-OA-002: Submit for Approval - Non-Draft Status
- **Input**: Offer already in PENDING_APPROVAL status
- **Expected**: 400 Bad Request

#### TC-OA-003: Submit for Approval - Not Found
- **Input**: Non-existent offer ID
- **Expected**: 404 Not Found

#### TC-OA-004: Approve Offer - Success
- **Input**: Valid offer ID, status=APPROVED, comment="Looks good"
- **Expected**: 200 OK, offer status = APPROVED, approval record created

#### TC-OA-005: Reject Offer - Success
- **Input**: Valid offer ID, status=REJECTED, comment="Salary too high"
- **Expected**: 200 OK, offer status = REJECTED, approval record created

#### TC-OA-006: Approve Offer - Only HR_MANAGER
- **Input**: HR role attempting approval
- **Expected**: 403 Forbidden

#### TC-OA-007: Approve Offer - No Comment
- **Input**: Approval with status only, comment=null
- **Expected**: 200 OK (comment optional)

#### TC-OA-008: Approve Already Approved Offer
- **Input**: Offer already APPROVED
- **Expected**: 400 Bad Request

#### TC-OA-009: Get Approval History - Success
- **Input**: Valid offer ID with approvals
- **Expected**: 200 OK, list of OfferApprovalResponse ordered by createdAt DESC

#### TC-OA-010: Get Approval History - Empty
- **Input**: Offer with no approval history
- **Expected**: 200 OK, empty list

#### TC-OA-011: Get Approval History - Not Found
- **Input**: Non-existent offer ID
- **Expected**: 404 Not Found

#### TC-OA-012: Approval Includes Approver Info
- **Input**: Approval record
- **Expected**: Response includes approvedByName

#### TC-OA-013: Submit - Rejected Offer Cannot Submit
- **Input**: Rejected offer attempting to submit again
- **Expected**: 400 Bad Request

#### TC-OA-014: Workflow State Transition Validation
- **Input**: DRAFT -> PENDING_APPROVAL -> APPROVED/REJECTED
- **Expected**: Only valid transitions allowed

---

## 7. Offer PDF Preview

### 7.1 API Endpoints

| Method | Endpoint | Description | Roles |
|--------|----------|-------------|-------|
| GET | `/api/v1/offers/{id}/pdf` | Generate and preview PDF | HR_MANAGER, HR |

### 7.2 Test Cases

#### TC-PDF-001: Generate PDF - Success
- **Input**: Valid approved offer ID
- **Expected**: 200 OK, Content-Type: application/pdf, PDF bytes returned

#### TC-PDF-002: Generate PDF - Draft Offer
- **Input**: Offer in DRAFT status
- **Expected**: 200 OK (generates PDF for any status)

#### TC-PDF-003: Generate PDF - Not Found
- **Input**: Non-existent offer ID
- **Expected**: 404 Not Found

#### TC-PDF-004: Generate PDF - Content Verification
- **Input**: Valid offer
- **Expected**: PDF contains: company header, candidate name, job title, salary, start date, benefits, notes, footer

#### TC-PDF-005: Generate PDF - Correct Format
- **Input**: Valid offer
- **Expected**: PDF is A4 size, valid PDF structure

#### TC-PDF-006: Generate PDF - Unauthorized
- **Input**: INTERVIEWER role
- **Expected**: 403 Forbidden

#### TC-PDF-007: Generate PDF - Authentication Required
- **Input**: No JWT token
- **Expected**: 401 Unauthorized

#### TC-PDF-008: Generate PDF - Inline Display
- **Input**: Valid offer
- **Expected**: Content-Disposition: inline (not attachment)

#### TC-PDF-009: Generate PDF - Filename
- **Input**: Valid offer ID
- **Expected**: Filename includes offer ID (e.g., offer-{id}.pdf)

#### TC-PDF-010: Generate PDF - Unicode Characters
- **Input**: Offer with Unicode characters in name/position
- **Expected**: PDF renders Unicode correctly (if font supports)

#### TC-PDF-011: Generate PDF - Long Text Handling
- **Input**: Offer with very long notes/benefits
- **Expected**: PDF handles overflow gracefully (wrapping or truncation)

#### TC-PDF-012: Generate PDF - Missing Optional Fields
- **Input**: Offer with null benefits/notes
- **Expected**: PDF omits or shows empty for those fields

---

## 8. Onboarding Checklist

### 8.1 API Endpoints

| Method | Endpoint | Description | Roles |
|--------|----------|-------------|-------|
| POST | `/api/v1/onboarding` | Create checklist | HR_MANAGER, HR |
| GET | `/api/v1/onboarding/{id}` | Get by ID | HR_MANAGER, HR |
| GET | `/api/v1/onboarding/application/{applicationId}` | Get by application | HR_MANAGER, HR |
| PUT | `/api/v1/onboarding/{id}` | Update checklist | HR_MANAGER, HR |
| PATCH | `/api/v1/onboarding/{id}/tasks/{taskId}` | Toggle task | HR_MANAGER, HR |

### 8.2 Test Cases

#### TC-OB-001: Create Checklist - Success
- **Input**: Valid applicationId, title, list of TaskEntry (title, description, dueDate, assignedUserId)
- **Expected**: 201 Created, OnboardingChecklistResponse with status=NOT_STARTED

#### TC-OB-002: Create Checklist - Missing Application ID
- **Input**: applicationId = null
- **Expected**: 400 Bad Request with validation error

#### TC-OB-003: Create Checklist - Invalid Application
- **Input**: applicationId = non-existent UUID
- **Expected**: 400 Bad Request or 404 Not Found

#### TC-OB-004: Create Checklist - Empty Tasks
- **Input**: tasks = []
- **Expected**: 201 Created (if empty tasks allowed) OR 400

#### TC-OB-005: Create Checklist - With Task Details
- **Input**: Valid checklist with multiple tasks
- **Expected**: 201 Created, all tasks created with isCompleted=false

#### TC-OB-006: Get Checklist by ID - Success
- **Input**: Valid checklist ID
- **Expected**: 200 OK, OnboardingChecklistResponse with progress stats

#### TC-OB-007: Get Checklist by ID - Not Found
- **Input**: Non-existent checklist ID
- **Expected**: 404 Not Found

#### TC-OB-008: Get Checklist by Application - Success
- **Input**: Valid application ID with checklist
- **Expected**: 200 OK, checklist details

#### TC-OB-009: Get Checklist by Application - No Checklist
- **Input**: Application without checklist
- **Expected**: 404 Not Found OR 200 with null/empty

#### TC-OB-010: Update Checklist - Success
- **Input**: Valid checklist ID, updated title/tasks
- **Expected**: 200 OK, updated checklist

#### TC-OB-011: Update Checklist - Not Found
- **Input**: Non-existent checklist ID
- **Expected**: 404 Not Found

#### TC-OB-012: Toggle Task - Mark Complete
- **Input**: Valid checklist ID, valid task ID
- **Expected**: 200 OK, task isCompleted=true

#### TC-OB-013: Toggle Task - Mark Incomplete
- **Input**: Valid checklist ID, valid task ID (toggle back)
- **Expected**: 200 OK, task isCompleted=false

#### TC-OB-014: Toggle Task - Invalid Task ID
- **Input**: Valid checklist ID, non-existent task ID
- **Expected**: 404 Not Found

#### TC-OB-015: Toggle Task - Task Not In Checklist
- **Input**: Task ID from different checklist
- **Expected**: 404 Not Found or 400 Bad Request

#### TC-OB-016: Status Auto-Update - All Complete
- **Input**: All tasks marked complete
- **Expected**: Checklist status = COMPLETED

#### TC-OB-017: Status Auto-Update - Some Complete
- **Input**: Some tasks complete, some not
- **Expected**: Checklist status = IN_PROGRESS

#### TC-OB-018: Status Auto-Update - None Complete
- **Input**: No tasks complete
- **Expected**: Checklist status = NOT_STARTED

#### TC-OB-019: Progress Calculation
- **Input**: Checklist with 10 tasks, 3 complete
- **Expected**: Progress = 30%, completedTasks = 3, totalTasks = 10

#### TC-OB-020: Get Checklist - Response Includes Candidate Info
- **Input**: Valid checklist
- **Expected**: Response includes candidateName, jobTitle from application

#### TC-OB-021: Unauthorized Role
- **Input**: INTERVIEWER role for any endpoint
- **Expected**: 403 Forbidden

#### TC-OB-022: Create Checklist - Past Due Date
- **Input**: task dueDate = yesterday
- **Expected**: 201 Created (past date allowed) OR 400

#### TC-OB-023: Task Sort Order
- **Input**: Multiple tasks with different sortOrder values
- **Expected**: Tasks returned in sortOrder sequence

---

## 9. Summary Statistics

| Module | API Test Cases | Total |
|--------|----------------|-------|
| Scorecard Templates | TC-ST-001 to TC-ST-017 | 17 |
| Interview Scorecard | TC-IS-001 to TC-IS-014 | 14 |
| Candidate Evaluation | TC-CE-001 to TC-CE-008 | 8 |
| Candidate Ranking | TC-CR-001 to TC-CR-012 | 12 |
| Offer Draft | TC-OD-001 to TC-OD-015 | 15 |
| Offer Approval | TC-OA-001 to TC-OA-014 | 14 |
| Offer PDF Preview | TC-PDF-001 to TC-PDF-012 | 12 |
| Onboarding Checklist | TC-OB-001 to TC-OB-023 | 23 |
| **Total** | | **115** |

---

## 10. Test Data Requirements

### 10.1 Required Test Data
- **Users**: HR, HR_MANAGER, INTERVIEWER roles
- **Departments**: At least 2 departments
- **Jobs**: At least 1 job with multiple applications
- **Applications**: At least 3 applications per job
- **Interviews**: At least 2 interviews per application
- **Scorecard Templates**: At least 2 templates with different criteria
- **Offers**: Offers in various statuses (DRAFT, PENDING_APPROVAL, APPROVED, REJECTED)
- **Onboarding Checklists**: At least 1 checklist with 5+ tasks

### 10.2 Test Scenarios for Data Setup
1. Application with completed interviews and scores
2. Application without interviews
3. Application with partial interview scores
4. Offer with complete approval history
5. Checklist with mixed task completion states

---

## 11. Test Environment Setup

### 11.1 Backend Testing
- Use `@SpringBootTest` for integration tests
- Use `@WebMvcTest` for controller tests with mocked services
- Use `@MockBean` for repository mocking
- Clean database state before each test class
- Use H2 in-memory database for testing

### 11.2 Frontend Testing
- Setup Vitest or Jest
- Use React Testing Library for component tests
- Mock API responses with MSW (Mock Service Worker)

---

## 12. Priority Matrix

| Priority | Description | Test Cases |
|----------|-------------|------------|
| P0 - Critical | Core workflow broken | OD-001, OD-006, OA-001, OA-004, OB-001, OB-016 |
| P1 - High | Major feature not working | ST-001, IS-001, CR-001, PDF-001, CE-001 |
| P2 - Medium | Feature degraded | ST-006, IS-007, OD-009, OB-006 |
| P3 - Low | Edge cases | ST-003, IS-003, OD-012, OB-022 |
