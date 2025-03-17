import { useAuthStore } from "@/store/user-storage";
import { NavItem } from "./NavItem";
import { AvatarMenu } from "./AvatarMenu";

export function NavigationHeader() {
  const { hasRole, isAuthenticated } = useAuthStore();

    return (
    <nav className="flex items-center justify-between px-6 py-4 border-b">
      <div className="flex items-center space-x-4">
        {isAuthenticated && <NavItem href="/home/me">My Profile</NavItem>}
        {hasRole("ADMIN") && <NavItem href="/dashboard/admin">Admin</NavItem>}
        {hasRole("SUPER_ADMIN") && <NavItem href="/dashboard/superadmin">Super Admin</NavItem>}
      </div>
      <AvatarMenu />
    </nav>
  );
}