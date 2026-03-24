import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import type { Job, JobRequest } from "@/types/job";

export default function EditJobPage() {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const [step, setStep] = useState(1);
  const [form, setForm] = useState<JobRequest>({ title: "" });

  const totalSteps = 4;

  useEffect(() => {
    if (id) {
      fetch(`/api/jobs/${id}`)
        .then((res) => res.json())
        .then((data: Job) => {
          setForm({
            title: data.title,
            department: data.department,
            location: data.location,
            description: data.description,
            status: data.status,
          });
        })
        .catch(console.error);
    }
  }, [id]);

  const updateStep = (s: number) => {
    if (s >= 1 && s <= totalSteps) setStep(s);
  };
  const handleNext = () => {
    if (step < totalSteps) setStep(step + 1);
  };
  const handlePrev = () => {
    if (step > 1) setStep(step - 1);
  };

  const handleChange = (
    e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement>
  ) => {
    const { name, value } = e.target;
    setForm((f) => ({ ...f, [name]: value }));
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!id) return;
    fetch(`/api/jobs/${id}`, {
      method: "PUT",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(form),
    })
      .then((res) => res.json())
      .then(() => navigate("/jobs"))
      .catch(console.error);
  };

  return (
    <div className="max-w-5xl mx-auto px-4 sm:px-6 lg:px-8">
      {/* Alert */}
      <div className="rounded-md bg-yellow-50 p-4 mb-6 border border-yellow-200">
        <div className="flex">
          <div className="flex-shrink-0">
            <svg
              xmlns="http://www.w3.org/2000/svg"
              className="h-5 w-5 text-yellow-400"
              fill="none"
              viewBox="0 0 24 24"
              stroke="currentColor"
            >
              <path
                strokeLinecap="round"
                strokeLinejoin="round"
                strokeWidth={2}
                d="M13 16h-1v-4h-1m1-4h.01M12 22c5.523 0 10-4.477 10-10S17.523 2 12 2 2 6.477 2 12s4.477 10 10 10z"
              />
            </svg>
          </div>
          <div className="ml-3">
            <h3 className="text-sm font-medium text-yellow-800">Unsaved Changes</h3>
            <div className="mt-2 text-sm text-yellow-700">
              <p>You have unsaved changes. Make sure to click "Publish" before leaving.</p>
            </div>
          </div>
        </div>
      </div>

      {/* Stepper same as create */}
      <nav aria-label="Progress">
        <ol role="list" className="space-y-4 md:flex md:space-x-8 md:space-y-0 mb-8">
          {[1, 2, 3, 4].map((s) => (
            <li key={s} className="md:flex-1">
              <a
                href="#"
                onClick={(e) => {
                  e.preventDefault();
                  updateStep(s);
                }}
                className={`group flex flex-col border-l-4 py-2 pl-4 hover:border-slate-800 md:border-l-0 md:border-t-4 md:pb-0 md:pl-0 md:pt-4 step-link ${
                  s === step
                    ? "border-slate-900 text-slate-900"
                    : s < step
                    ? "border-slate-900 text-slate-900"
                    : "border-gray-200 text-gray-500"
                }`}
                data-step={s}
              >
                <span className="text-sm font-medium">{`Step ${s}`}</span>
                <span className="text-sm font-medium">
                  {s === 1
                    ? "Job Details"
                    : s === 2
                    ? "Requirements"
                    : s === 3
                    ? "Workflow"
                    : "Review"}
                </span>
              </a>
            </li>
          ))}
        </ol>
      </nav>

      <form onSubmit={handleSubmit} className="bg-white shadow rounded-lg p-6 sm:p-8">
        {/* content identical to create but maybe header text updated depending on step etc. reuse logic? */}
        {step === 1 && (
          <div className="space-y-6">
            <div>
              <h2 className="text-lg font-medium leading-6 text-gray-900">
                Job Details
              </h2>
              <p className="mt-1 text-sm text-gray-500">
                Update basic information about the role.
              </p>
            </div>
            <div className="grid grid-cols-1 gap-y-6 gap-x-4 sm:grid-cols-6">
              <div className="sm:col-span-4">
                <label htmlFor="title" className="block text-sm font-medium text-gray-700">
                  Job Title
                </label>
                <div className="mt-1">
                  <input
                    type="text"
                    name="title"
                    id="title"
                    value={form.title}
                    onChange={handleChange}
                    required
                    className="block w-full rounded-md border-0 py-1.5 text-gray-900 ring-1 ring-inset ring-gray-300 focus:ring-2 focus:ring-inset focus:ring-slate-600 sm:text-sm sm:leading-6"
                  />
                </div>
              </div>

              <div className="sm:col-span-3">
                <label htmlFor="department" className="block text-sm font-medium text-gray-700">
                  Department
                </label>
                <div className="mt-1">
                  <select
                    id="department"
                    name="department"
                    value={form.department || ""}
                    onChange={handleChange}
                    className="block w-full rounded-md border-0 py-1.5 text-gray-900 ring-1 ring-inset ring-gray-300 focus:ring-2 focus:ring-inset focus:ring-slate-600 sm:text-sm sm:leading-6"
                  >
                    <option value="">Select</option>
                    <option value="engineering">Engineering</option>
                    <option value="product">Product</option>
                    <option value="design">Design</option>
                    <option value="marketing">Marketing</option>
                  </select>
                </div>
              </div>

              <div className="sm:col-span-3">
                <label htmlFor="location" className="block text-sm font-medium text-gray-700">
                  Location
                </label>
                <div className="mt-1">
                  <input
                    type="text"
                    name="location"
                    id="location"
                    value={form.location || ""}
                    onChange={handleChange}
                    className="block w-full rounded-md border-0 py-1.5 text-gray-900 ring-1 ring-inset ring-gray-300 focus:ring-2 focus:ring-inset focus:ring-slate-600 sm:text-sm sm:leading-6"
                  />
                </div>
              </div>

              <div className="sm:col-span-6">
                <label htmlFor="description" className="block text-sm font-medium text-gray-700">
                  Description
                </label>
                <div className="mt-1">
                  <textarea
                    id="description"
                    name="description"
                    rows={5}
                    value={form.description || ""}
                    onChange={handleChange}
                    className="block w-full rounded-md border-0 py-1.5 text-gray-900 ring-1 ring-inset ring-gray-300 focus:ring-2 focus:ring-inset focus:ring-slate-600 sm:text-sm sm:leading-6"
                  />
                </div>
              </div>
            </div>
          </div>
        )}
        {step === 2 && (
          <div className="space-y-6">
            <h2 className="text-lg font-medium leading-6 text-gray-900">
              Requirements
            </h2>
            <p className="mt-1 text-sm text-gray-500">
              Update skills and experience needed.
            </p>
            <div className="space-y-4">
              <div>
                <label className="block text-sm font-medium text-gray-700">
                  Skills
                </label>
                <div className="mt-2">
                  <input
                    type="text"
                    name="skills"
                    value={(form as any).skills || ""}
                    onChange={handleChange}
                    className="block w-full rounded-md border-0 py-1.5 text-gray-900 ring-1 ring-inset ring-gray-300 focus:ring-2 focus:ring-inset focus:ring-slate-600 sm:text-sm sm:leading-6"
                  />
                </div>
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700">
                  Years of Experience
                </label>
                <div className="mt-1">
                  <select
                    name="experience"
                    value={(form as any).experience || ""}
                    onChange={handleChange}
                    className="block w-full rounded-md border-0 py-1.5 text-gray-900 ring-1 ring-inset ring-gray-300 focus:ring-2 focus:ring-inset focus:ring-slate-600 sm:text-sm sm:leading-6"
                  >
                    <option value="">Select</option>
                    <option value="0-1">0-1</option>
                    <option value="1-3">1-3</option>
                    <option value="3+">3+</option>
                  </select>
                </div>
              </div>
            </div>
          </div>
        )}
        {step === 3 && (
          <div className="space-y-6">
            <h2 className="text-lg font-medium leading-6 text-gray-900">
              Hiring Workflow
            </h2>
            <p className="mt-1 text-sm text-gray-500">
              Update hiring stages.
            </p>
            <div className="space-y-2">
              <p className="text-sm text-gray-500">(workflow editor placeholder)</p>
            </div>
          </div>
        )}
        {step === 4 && (
          <div className="space-y-6">
            <h2 className="text-lg font-medium leading-6 text-gray-900">
              Review & Update
            </h2>
            <p className="mt-1 text-sm text-gray-500">
              Review your changes.
            </p>
            <div className="bg-gray-50 rounded-md p-4 space-y-4">
              <div>
                <h3 className="text-sm font-medium text-gray-500">Job Title</h3>
                <p className="mt-1 text-sm text-gray-900 font-semibold">
                  {form.title}
                </p>
              </div>
              <div>
                <h3 className="text-sm font-medium text-gray-500">Department</h3>
                <p className="mt-1 text-sm text-gray-900">
                  {form.department}
                </p>
              </div>
            </div>
          </div>
        )}

        {/* Actions */}
        <div className="pt-5 border-t border-gray-200 mt-6">
          <div className="flex justify-between">
            <button
              type="button"
              id="prev-btn"
              onClick={handlePrev}
              className="hidden rounded-md border border-gray-300 bg-white px-4 py-2 text-sm font-medium text-gray-700 shadow-sm hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-slate-500 focus:ring-offset-2"
              style={{ visibility: step === 1 ? "hidden" : "visible" }}
            >
              Back
            </button>
            <div className="flex space-x-3 ml-auto">
              <button
                type="button"
                onClick={handlePrev}
                className="rounded-md border border-gray-300 bg-white px-4 py-2 text-sm font-medium text-gray-700 shadow-sm hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-slate-500 focus:ring-offset-2"
                style={{ display: "none" }}
              >
                Cancel
              </button>
              <button
                type="button"
                id="next-btn"
                onClick={handleNext}
                className="rounded-md bg-primary px-4 py-2 text-sm font-medium text-primary-foreground shadow hover:bg-primary/90 focus:outline-none focus:ring-2 focus:ring-primary"
                style={{ visibility: step === totalSteps ? "hidden" : "visible" }}
              >
                Next
              </button>
              <button
                type="submit"
                id="submit-btn"
                className="hidden rounded-md bg-primary px-4 py-2 text-sm font-medium text-primary-foreground shadow hover:bg-primary/90 focus:outline-none focus:ring-2 focus:ring-primary"
                style={{ visibility: step === totalSteps ? "visible" : "hidden" }}
              >
                Update Job
              </button>
            </div>
          </div>
        </div>
      </form>
    </div>
  );
}
