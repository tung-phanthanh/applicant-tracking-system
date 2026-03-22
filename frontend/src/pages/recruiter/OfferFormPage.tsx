import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";
import { offerService } from "@/services/offerService";
import type { CreateOfferRequest } from "@/types/offer";

export default function OfferFormPage() {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const isEdit = !!id;

  const [form, setForm] = useState<CreateOfferRequest>({
    applicationId: crypto.randomUUID(),
    salary: 0,
    positionTitle: "",
    startDate: null,
    benefits: null,
    notes: null,
  });
  const [loading, setLoading] = useState(false);
  const [saving, setSaving] = useState(false);

  useEffect(() => {
    if (!id) return;
    const load = async () => {
      setLoading(true);
      try {
        const offer = await offerService.getOffer(id);
        setForm({
          applicationId: offer.applicationId,
          salary: offer.salary,
          positionTitle: offer.positionTitle,
          startDate: offer.startDate,
          benefits: offer.benefits,
          notes: offer.notes,
        });
      } catch {
        alert("Failed to load offer.");
      } finally {
        setLoading(false);
      }
    };
    void load();
  }, [id]);

  const handleChange = (field: keyof CreateOfferRequest, value: string | number | null) => {
    setForm((prev) => ({ ...prev, [field]: value }));
  };

  const handleSubmit = async () => {
    if (!form.applicationId || !form.positionTitle || form.salary <= 0) {
      alert("Please fill all required fields.");
      return;
    }
    if (!/^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$/.test(form.applicationId)) {
      alert("Application ID must be a valid 36-character UUID.");
      return;
    }
    setSaving(true);
    try {
      if (isEdit && id) {
        await offerService.updateDraft(id, form);
      } else {
        await offerService.createDraft(form);
      }
      navigate("/offers");
    } catch {
      alert("Failed to save offer.");
    } finally {
      setSaving(false);
    }
  };

  if (loading) {
    return (
      <div className="flex h-64 items-center justify-center text-muted-foreground">
        Loading offer...
      </div>
    );
  }

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-3xl font-semibold tracking-tight">
          {isEdit ? "Edit Offer" : "Create Offer"}
        </h1>
        <p className="mt-1 text-sm text-muted-foreground">
          {isEdit ? "Update the offer details" : "Draft a new offer for a candidate"}
        </p>
      </div>

      <section className="mx-auto max-w-2xl rounded-lg border border-border bg-card p-6 shadow-sm">
        <div className="space-y-4">
          <div>
            <label className="mb-1 block text-sm font-medium">Application ID *</label>
            <Input
              value={form.applicationId}
              onChange={(e) => handleChange("applicationId", e.target.value)}
              placeholder="UUID of the application"
              disabled={isEdit}
            />
          </div>

          <div>
            <label className="mb-1 block text-sm font-medium">Position Title *</label>
            <Input
              value={form.positionTitle}
              onChange={(e) => handleChange("positionTitle", e.target.value)}
              placeholder="e.g. Senior Software Engineer"
            />
          </div>

          <div>
            <label className="mb-1 block text-sm font-medium">Salary (USD) *</label>
            <Input
              type="number"
              value={form.salary || ""}
              onChange={(e) => handleChange("salary", parseFloat(e.target.value) || 0)}
              placeholder="e.g. 85000"
              min={0}
            />
          </div>

          <div>
            <label className="mb-1 block text-sm font-medium">Start Date</label>
            <Input
              type="date"
              value={form.startDate ?? ""}
              onChange={(e) => handleChange("startDate", e.target.value || null)}
            />
          </div>

          <div>
            <label className="mb-1 block text-sm font-medium">Benefits</label>
            <textarea
              value={form.benefits ?? ""}
              onChange={(e) => handleChange("benefits", e.target.value || null)}
              placeholder="Health insurance, 401k, etc."
              rows={3}
              className="w-full rounded-md border border-input bg-background px-3 py-2 text-sm ring-offset-background placeholder:text-muted-foreground focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring"
            />
          </div>

          <div>
            <label className="mb-1 block text-sm font-medium">Notes</label>
            <textarea
              value={form.notes ?? ""}
              onChange={(e) => handleChange("notes", e.target.value || null)}
              placeholder="Additional notes..."
              rows={3}
              className="w-full rounded-md border border-input bg-background px-3 py-2 text-sm ring-offset-background placeholder:text-muted-foreground focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring"
            />
          </div>
        </div>

        <div className="mt-6 flex justify-end gap-2">
          <Button variant="outline" onClick={() => navigate("/offers")}>
            Cancel
          </Button>
          <Button onClick={handleSubmit} disabled={saving}>
            {saving ? "Saving..." : isEdit ? "Update Offer" : "Create Draft"}
          </Button>
        </div>
      </section>
    </div>
  );
}
