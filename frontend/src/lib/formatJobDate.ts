export function formatJobDate(iso: string): string {
    try {
        return new Intl.DateTimeFormat(undefined, { dateStyle: "medium" }).format(
            new Date(iso),
        );
    } catch {
        return iso;
    }
}
