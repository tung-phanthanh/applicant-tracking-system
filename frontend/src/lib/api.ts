const BASE_URL = import.meta.env.VITE_API_URL || "http://localhost:8386/api/v1";

type HttpMethod = "GET" | "POST" | "PUT" | "PATCH" | "DELETE";

interface FetchOptions {
    method?: HttpMethod;
    body?: unknown;
    params?: Record<string, string | number | boolean | undefined>;
}

class ApiError extends Error {
    status: number;
    data?: unknown;

    constructor(
        status: number,
        message: string,
        data?: unknown,
    ) {
        super(message);
        this.status = status;
        this.data = data;
        this.name = "ApiError";
    }
}

function buildUrl(
    path: string,
    params?: Record<string, string | number | boolean | undefined>,
): string {
    const url = new URL(BASE_URL + path);
    if (params) {
        Object.entries(params).forEach(([k, v]) => {
            if (v !== undefined && v !== null) {
                url.searchParams.append(k, String(v));
            }
        });
    }
    return url.toString();
}

function getToken(): string | null {
    return localStorage.getItem("accessToken");
}

async function apiFetch<T>(path: string, options: FetchOptions = {}): Promise<T> {
    const { method = "GET", body, params } = options;
    const token = getToken();

    const headers: HeadersInit = {
        "Content-Type": "application/json",
    };
    if (token) {
        headers["Authorization"] = `Bearer ${token}`;
    }

    const response = await fetch(buildUrl(path, params), {
        method,
        headers,
        body: body !== undefined ? JSON.stringify(body) : undefined,
    });

    if (!response.ok) {
        let data: unknown;
        try {
            data = await response.json();
        } catch {
            data = null;
        }
        const message =
            (data as { message?: string })?.message ||
            `HTTP ${response.status}: ${response.statusText}`;
        throw new ApiError(response.status, message, data);
    }

    // 204 No Content
    if (response.status === 204) {
        return undefined as T;
    }

    return response.json() as Promise<T>;
}

export { apiFetch, ApiError, BASE_URL };
