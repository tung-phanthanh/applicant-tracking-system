1. Project Overview

This project is an Enterprise Applicant Tracking System (ATS) — an internal recruitment management system for companies.

The frontend is built as a Single Page Application (SPA) using:

React 19

TypeScript (strict mode)

Vite 7

Tailwind CSS v4 (via @tailwindcss/vite)

Shadcn UI (component source copied into src/components/ui/)

React Router 7

Lucide React (icons)

Frontend root directory:

d:/course-project/ats-system/frontend
2. Tech Stack
Technology	Version	Notes
React	19	with react-dom
TypeScript	~5.9	strict mode enabled
Vite	7	bundler
Tailwind CSS	4	via @tailwindcss/vite (NO tailwind.config.js old pattern)
Shadcn UI	—	components copied into src/components/ui
React Router	7	BrowserRouter + Routes
Lucide React	0.563	icon library
class-variance-authority	0.7	variant styling
clsx + tailwind-merge	—	cn() utility
3. Folder Structure
src/
├── App.tsx
├── main.tsx
├── index.css
├── assets/
├── components/
│   ├── shared/
│   │   ├── Header.tsx
│   │   ├── Sidebar.tsx
│   │   └── StatCard.tsx
│   └── ui/                 # DO NOT MODIFY
├── contexts/
│   └── AuthContext.tsx
├── hooks/
│   └── useAuth.ts
├── layouts/
│   ├── AppLayout.tsx
│   └── AuthLayout.tsx
├── lib/
│   └── utils.ts            # cn()
├── pages/
│   ├── auth/
│   │   └── LoginPage.tsx
│   └── recruiter/
│       ├── DashboardPage.tsx
│       └── ProfilePage.tsx
├── routes/
│   └── ProtectedRoute.tsx
└── types/
    └── auth.ts
4. Design System Rules (MANDATORY)
Use CSS tokens only (from index.css)

Do NOT hardcode hex/rgb colors.

Use:

bg-background

text-foreground

bg-card

text-card-foreground

bg-muted

text-muted-foreground

bg-primary

text-primary-foreground

bg-secondary

border-border

text-destructive

bg-sidebar

bg-sidebar-primary

Border Radius

rounded-lg → cards

rounded-md → inputs/badges

rounded-full → avatars

Spacing

Page padding → p-6 or p-8

Section spacing → space-y-6 or space-y-8

Card gap → gap-4 or gap-6

5. Coding Conventions (STRICT)
TypeScript

NEVER use any

Always define prop types

Use:

interface for object shapes

type for unions

Components

One component per file

PascalCase file names

Functional components only

Use cn() from @/lib/utils

Use alias imports (@/) only

Never use relative imports like ../../

UI Rules

DO NOT install other UI frameworks

DO NOT modify files inside src/components/ui/

If a new Shadcn primitive is needed → create a new file there

6. Authentication System

Use:

import { useAuth } from "@/hooks/useAuth";

User type:

type UserRole = "recruiter" | "admin";

interface User {
  id: string;
  name: string;
  email: string;
  role: UserRole;
  department: string;
  phone: string;
  initials: string;
}

Auth is currently mock-based.
When backend is connected, only update login() in AuthContext.tsx.

7. Completed Modules 

Login Page

Dashboard Page (mock data)

Profile Page (mock data)

Sidebar

Header

StatCard

AuthContext + useAuth

ProtectedRoute

AppLayout / AuthLayout

8.  NEXT TASKS (EDIT THIS SECTION ONLY)

When continuing development, focus ONLY on the modules below.

8.1 Jobs Module (/jobs)

JobsPage

Job list

Search

Filters

Pagination

JobDetailPage

Job information

List of applied candidates

CreateJobPage (or modal)

Form to create job posting

8.2 Candidates Module (/candidates)

CandidatesPage

Candidate list

Filter by status

Filter by job

CandidateDetailPage

Candidate profile

Pipeline history

Notes section

CV upload form

Add recruiter notes

8.3 Interviews Module (/interviews)

InterviewsPage

Calendar view OR list view

ScheduleInterviewPage

Interview scheduling form

8.4 Admin Module (/admin)

Visible ONLY when:

user.role === "admin"

Pages:

AdminUsersPage

AdminSettingsPage

8.5 Backend Integration (Future)

Replace mock login with real API call

Use VITE_API_URL

Store JWT in localStorage or sessionStorage

9. Routing Rule

All protected routes must be inside:

<Route element={<AppLayout />}>

Example:

<Route path="/jobs" element={<JobsPage />} />
<Route path="/jobs/:id" element={<JobDetailPage />} />

 CRITICAL RULES — DO NOT VIOLATE

No hardcoded colors

No external CSS frameworks

No relative imports

No any

No editing src/components/ui/

Must follow folder structure

Must follow TypeScript strict typing