import { apiFetch } from "@/lib/api";
import type {
    ScorecardTemplate,
    ScorecardCriterion,
    Page,
    PageParams,
} from "@/types/models";

export interface CreateTemplateRequest {
    name: string;
    departmentId?: number;
    description?: string;
    criteria?: { name: string; weight: number }[];
}

export interface UpdateTemplateRequest {
    name?: string;
    departmentId?: number;
    description?: string;
}

export interface CreateCriterionRequest {
    name: string;
    weight: number;
}

function pageToParams(p: PageParams): Record<string, string | number | boolean | undefined> {
    return { page: p.page ?? 0, size: p.size ?? 10, sort: p.sort };
}

export const scorecardService = {
    getAll(params: PageParams = {}) {
        return apiFetch<Page<ScorecardTemplate>>("/scorecards/templates", {
            params: pageToParams(params),
        });
    },

    getById(id: number) {
        return apiFetch<ScorecardTemplate>(`/scorecards/templates/${id}`);
    },

    create(body: CreateTemplateRequest) {
        return apiFetch<ScorecardTemplate>("/scorecards/templates", {
            method: "POST",
            body,
        });
    },

    update(id: number, body: UpdateTemplateRequest) {
        return apiFetch<ScorecardTemplate>(`/scorecards/templates/${id}`, {
            method: "PUT",
            body,
        });
    },

    delete(id: number) {
        return apiFetch<void>(`/scorecards/templates/${id}`, { method: "DELETE" });
    },

    archive(id: number) {
        return apiFetch<ScorecardTemplate>(`/scorecards/templates/${id}/archive`, {
            method: "PATCH",
        });
    },

    unarchive(id: number) {
        return apiFetch<ScorecardTemplate>(`/scorecards/templates/${id}/unarchive`, {
            method: "PATCH",
        });
    },

    addCriterion(templateId: number, body: CreateCriterionRequest) {
        return apiFetch<ScorecardCriterion>(
            `/scorecards/templates/${templateId}/criteria`,
            { method: "POST", body },
        );
    },

    updateCriterion(criterionId: number, body: CreateCriterionRequest) {
        return apiFetch<ScorecardCriterion>(`/scorecards/criteria/${criterionId}`, {
            method: "PUT",
            body,
        });
    },

    deleteCriterion(criterionId: number) {
        return apiFetch<void>(`/scorecards/criteria/${criterionId}`, {
            method: "DELETE",
        });
    },

    reorderCriteria(templateId: number, orderedIds: number[]) {
        return apiFetch<void>(
            `/scorecards/templates/${templateId}/criteria/reorder`,
            { method: "PUT", body: orderedIds },
        );
    },
};
