import axios from "axios";

export function resolveApiError(err: unknown): string {
    if (axios.isAxiosError(err)) {
        const msg: string | undefined = err.response?.data?.message;
        if (err.response?.status === 400) {
            const details: string[] | undefined = err.response?.data?.details;
            return details?.join(", ") || msg || "Invalid input.";
        }
        return msg || "An error occurred.";
    }
    return "An unexpected error occurred.";
}
