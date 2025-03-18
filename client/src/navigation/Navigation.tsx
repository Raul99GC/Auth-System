import { JSX, useEffect } from "react";
import { Routes, Route, Navigate } from "react-router";
import AuthPage from '@/screens/Auth';
import Error404Page from '@/screens/Error404Page';
import ProfilePage from '@/screens/home/ProfilePage';
import AdminPage from "@/screens/dashboard/admin/AdminPage";
import SuperAdminPage from "@/screens/dashboard/superAdmin/SuperAdminPage";
import { useAuthStore } from "@/store/auth-storage"; // Ajusta la ruta a donde tengas definido el store
import { NavigationHeader } from "@/components/home/NavigationHeader";

type Role = "ADMIN" | "SUPER_ADMIN" | "USER"; // ejemplo de un tipo de rol

const PrivateRoute = ({
  children,
  requiredRoles = [],
}: {
  children: JSX.Element;
  requiredRoles?: Role[]; // Cambiar el tipo a Role[]
}) => {
  const { isAuthenticated, isLoading, user } = useAuthStore();

  if (isLoading) return <div>Loading...</div>;

  if (!isAuthenticated) return <Navigate to="/auth" replace />;

  if (requiredRoles.length > 0) {
    // Verifica que el usuario tenga al menos uno de los roles requeridos
    const hasRequiredRole = requiredRoles.some((role) =>
      user?.roles.includes(role) // Asegúrate de que user?.roles también sea un array de 'Role'
    );
    if (!hasRequiredRole) return <Navigate to="/home/me" replace />;
  }

  return children;
};
// Ruta para páginas de autenticación. Si el usuario ya está autenticado, se redirige.
const AuthRoute = ({ children }: { children: JSX.Element }) => {
  const { isAuthenticated, isLoading, } = useAuthStore();

  useEffect(() => {

    if (!isAuthenticated) {
      useAuthStore.persist.clearStorage()
    }
  }, [isAuthenticated]);

  if (isLoading) return <div>Loading...</div>;

  if (isAuthenticated) return <Navigate to="/home/me" replace />;

  return children;
};

const Navigation = () => {
  return (
    <Routes>
      {/* Redirecciones */}
      <Route path="/" element={<Navigate replace to="/home/me" />} />
      <Route path="/home" element={<Navigate replace to="/home/me" />} />

      {/* Ruta para la vista "me", accesible sólo para usuarios autenticados */}
      <Route
        path="/home/me"
        element={
          <PrivateRoute>
            <div className="min-h-screen h-screen bg-gray-50">
              <NavigationHeader />
              <ProfilePage />
            </div>
          </PrivateRoute>
        }
      />

      {/* Dashboard para Admin, accesible sólo si el usuario tiene el rol "admin" */}
      <Route
        path="/dashboard/admin"
        element={
          <PrivateRoute requiredRoles={["ADMIN"]}>
            <AdminPage />
          </PrivateRoute>
        }
      />

      {/* Dashboard para SuperAdmin, accesible sólo si el usuario tiene el rol "superadmin" */}
      <Route
        path="/dashboard/superadmin"
        element={
          <PrivateRoute requiredRoles={["SUPER_ADMIN"]}>
            <SuperAdminPage />
          </PrivateRoute>
        }
      />

      {/* Ruta de autenticación */}
      <Route
        path="/auth"
        element={
          <AuthRoute>
            <AuthPage />
          </AuthRoute>
        }
      />

      {/* Ruta para manejar páginas no encontradas */}
      <Route path="*" element={<Error404Page />} />
    </Routes>
  );
};

export default Navigation;