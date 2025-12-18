import { Navigate, Outlet } from "react-router-dom";
import { useAuth } from "./AuthContext";

export default function PrivateRoute({ allowedRoles }) {
    const { token, role } = useAuth();

    // not logged in
    if (!token) {
        return <Navigate to="/login" replace />;
    }

    // logged in but wrong role
    if (allowedRoles && !allowedRoles.includes(role)) {
        return <Navigate to="/login" replace />;
    }

    // authorized
    return <Outlet />;
}
