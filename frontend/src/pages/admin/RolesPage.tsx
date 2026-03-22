import { useState, useEffect } from "react";
import {
    Shield,
    Plus,
    Trash2,
    Users,
    ChevronDown,
    ChevronUp,
    Check,
} from "lucide-react";
import { Button } from "@/components/ui/button";
import { Badge } from "@/components/ui/badge";
import { Switch } from "@/components/ui/switch";
import { Label } from "@/components/ui/label";
import { Input } from "@/components/ui/input";
import { cn } from "@/lib/utils";
import type { PermissionKey, Role, Permission } from "@/types/index";
import { roleService } from "@/services/api/role.service";

export default function RolesPage() {
    const [roles, setRoles] = useState<Role[]>([]);
    const [allPerms, setAllPerms] = useState<Permission[]>([]);
    const [expandedRoleId, setExpandedRoleId] = useState<string | null>(null);
    const [creating, setCreating] = useState(false);
    const [newRoleName, setNewRoleName] = useState("");
    const [newRoleDesc, setNewRoleDesc] = useState("");
    const [deleteConfirm, setDeleteConfirm] = useState<string | null>(null);

    useEffect(() => {
        Promise.all([roleService.getAllRoles(), roleService.getAllPermissions()])
            .then(([rolesData, permsData]) => {
                setRoles(rolesData);
                setAllPerms(permsData);
                if (rolesData.length > 0) setExpandedRoleId(rolesData[0].id);
            }).catch(console.error);
    }, []);

    const togglePermission = async (roleId: string, perm: PermissionKey) => {
        const role = roles.find(r => r.id === roleId);
        if (!role) return;
        const has = role.permissions.includes(perm);
        const newPerms = has ? role.permissions.filter(p => p !== perm) : [...role.permissions, perm];

        // Optimistic update
        setRoles((prev) => prev.map((r) => r.id === roleId ? { ...r, permissions: newPerms } : r));
        try {
            await roleService.updateRole(roleId, { name: role.name, description: role.description, permissions: newPerms });
        } catch (e) {
            console.error("Failed to update permission", e);
            // Revert changes on error
            setRoles((prev) => prev.map((r) => r.id === roleId ? { ...r, permissions: role.permissions } : r));
        }
    };

    const createRole = async () => {
        if (!newRoleName.trim()) return;
        setCreating(false);
        try {
            const newRole = await roleService.createRole({
                name: newRoleName.trim(),
                description: newRoleDesc.trim() || undefined,
                permissions: [] // start empty or defaults
            });
            setRoles((prev) => [...prev, newRole]);
            setExpandedRoleId(newRole.id);
            setNewRoleName("");
            setNewRoleDesc("");
        } catch (e) {
            console.error(e);
        }
    };

    const deleteRole = async (roleId: string) => {
        setDeleteConfirm(null);
        try {
            await roleService.deleteRole(roleId);
            setRoles((prev) => prev.filter((r) => r.id !== roleId));
        } catch (e) {
            console.error(e);
        }
    };

    const categories = Array.from(new Set(allPerms.map(p => p.category)));
    const grouped = categories.map((cat) => ({
        category: cat,
        perms: allPerms.filter((p) => p.category === cat),
    }));

    return (
        <div className="space-y-6">
            {/* Header */}
            <div className="flex items-center justify-between">
                <div>
                    <h2 className="text-2xl font-bold text-foreground">Roles & Permissions</h2>
                    <p className="mt-1 text-sm text-muted-foreground">
                        Define what each role can access within the system.
                    </p>
                </div>
                <Button onClick={() => setCreating(true)} className="gap-2">
                    <Plus className="h-4 w-4" />
                    New Role
                </Button>
            </div>

            {/* Create Role Form */}
            {creating && (
                <div className="rounded-lg border border-border bg-card p-5 shadow-sm">
                    <h3 className="mb-4 font-semibold text-card-foreground">Create New Role</h3>
                    <div className="grid grid-cols-1 gap-4 md:grid-cols-2">
                        <div className="space-y-1.5">
                            <Label htmlFor="role-name">Role Name</Label>
                            <Input
                                id="role-name"
                                placeholder="e.g. Department Lead"
                                value={newRoleName}
                                onChange={(e) => setNewRoleName(e.target.value)}
                            />
                        </div>
                        <div className="space-y-1.5">
                            <Label htmlFor="role-desc">Description</Label>
                            <Input
                                id="role-desc"
                                placeholder="Brief description of this role"
                                value={newRoleDesc}
                                onChange={(e) => setNewRoleDesc(e.target.value)}
                            />
                        </div>
                    </div>
                    <div className="mt-4 flex gap-2">
                        <Button onClick={createRole} disabled={!newRoleName.trim()}>
                            Create Role
                        </Button>
                        <Button variant="outline" onClick={() => setCreating(false)}>
                            Cancel
                        </Button>
                    </div>
                </div>
            )}

            {/* Roles List */}
            <div className="space-y-4">
                {roles.map((role) => {
                    const isExpanded = expandedRoleId === role.id;
                    return (
                        <div
                            key={role.id}
                            className="rounded-lg border border-border bg-card shadow-sm overflow-hidden"
                        >
                            {/* Role Header */}
                            <div
                                className="flex cursor-pointer items-center justify-between p-5 hover:bg-accent/50 transition-colors"
                                onClick={() => setExpandedRoleId(isExpanded ? null : role.id)}
                            >
                                <div className="flex items-center gap-4">
                                    <div className={cn("flex h-10 w-10 shrink-0 items-center justify-center rounded-full text-white", role.color)}>
                                        <Shield className="h-5 w-5" />
                                    </div>
                                    <div>
                                        <div className="flex items-center gap-2">
                                            <span className="font-semibold text-card-foreground">{role.name}</span>
                                            {role.isSystem && (
                                                <Badge variant="secondary" className="text-xs">System</Badge>
                                            )}
                                        </div>
                                        <p className="text-sm text-muted-foreground">{role.description}</p>
                                    </div>
                                </div>
                                <div className="flex items-center gap-4">
                                    <div className="hidden items-center gap-1.5 text-sm text-muted-foreground sm:flex">
                                        <Users className="h-4 w-4" />
                                        <span>{role.userCount} users</span>
                                    </div>
                                    <Badge variant="outline" className="text-xs">
                                        {role.permissions.length} permissions
                                    </Badge>
                                    {isExpanded ? (
                                        <ChevronUp className="h-4 w-4 text-muted-foreground" />
                                    ) : (
                                        <ChevronDown className="h-4 w-4 text-muted-foreground" />
                                    )}
                                </div>
                            </div>

                            {/* Expanded Permissions */}
                            {isExpanded && (
                                <div className="border-t border-border p-5">
                                    <div className="grid grid-cols-1 gap-6 md:grid-cols-3">
                                        {grouped.map(({ category, perms }) => (
                                            <div key={category}>
                                                <h4 className="mb-3 text-xs font-semibold uppercase tracking-wider text-muted-foreground">
                                                    {category}
                                                </h4>
                                                <div className="space-y-3">
                                                    {perms.map((perm) => {
                                                        const active = role.permissions.includes(perm.key);
                                                        return (
                                                            <div key={perm.key} className="flex items-start gap-3">
                                                                <Switch
                                                                    id={`${role.id}-${perm.key}`}
                                                                    checked={active}
                                                                    onCheckedChange={() => togglePermission(role.id, perm.key)}
                                                                    className="mt-0.5"
                                                                />
                                                                <Label
                                                                    htmlFor={`${role.id}-${perm.key}`}
                                                                    className="cursor-pointer leading-snug"
                                                                >
                                                                    <span className={cn("text-sm font-medium", active ? "text-foreground" : "text-muted-foreground")}>
                                                                        {perm.label}
                                                                    </span>
                                                                    <p className="text-xs text-muted-foreground">{perm.description}</p>
                                                                </Label>
                                                            </div>
                                                        );
                                                    })}
                                                </div>
                                            </div>
                                        ))}
                                    </div>

                                    {/* Footer actions */}
                                    {!role.isSystem && (
                                        <div className="mt-5 flex justify-end border-t border-border pt-4">
                                            {deleteConfirm === role.id ? (
                                                <div className="flex items-center gap-2">
                                                    <span className="text-sm text-destructive">Delete this role?</span>
                                                    <Button
                                                        variant="destructive"
                                                        size="sm"
                                                        onClick={() => deleteRole(role.id)}
                                                        className="gap-1.5"
                                                    >
                                                        <Check className="h-4 w-4" />
                                                        Confirm
                                                    </Button>
                                                    <Button
                                                        variant="outline"
                                                        size="sm"
                                                        onClick={() => setDeleteConfirm(null)}
                                                    >
                                                        Cancel
                                                    </Button>
                                                </div>
                                            ) : (
                                                <Button
                                                    variant="outline"
                                                    size="sm"
                                                    onClick={() => setDeleteConfirm(role.id)}
                                                    className="gap-1.5 text-destructive hover:text-destructive"
                                                >
                                                    <Trash2 className="h-4 w-4" />
                                                    Delete Role
                                                </Button>
                                            )}
                                        </div>
                                    )}
                                </div>
                            )}
                        </div>
                    );
                })}
            </div>
        </div>
    );
}
