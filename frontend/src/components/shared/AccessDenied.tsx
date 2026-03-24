import { Link } from "react-router-dom";
import { ShieldAlert } from "lucide-react";

export function AccessDenied() {
    return (
        <div className="flex h-full w-full flex-col items-center justify-center p-8 text-center text-slate-800 dark:text-slate-200">
            <ShieldAlert className="mb-4 h-16 w-16 text-rose-500" strokeWidth={1.5} />
            <h1 className="mb-2 text-3xl font-bold">Access Denied</h1>
            <p className="mb-6 max-w-md text-slate-500 dark:text-slate-400">
                You don't have permission to access this page. If you believe this is
                an error, please contact your system administrator.
            </p>
            <Link
                to="/admin"
                className="rounded-md bg-indigo-600 px-6 py-2.5 text-sm font-medium text-white transition-colors hover:bg-indigo-700"
            >
                Return to Dashboard
            </Link>
        </div>
    );
}
