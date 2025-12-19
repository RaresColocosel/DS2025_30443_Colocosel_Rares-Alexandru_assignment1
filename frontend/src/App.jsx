import { Routes, Route, Navigate } from "react-router-dom";
import LoginPage from "./pages/LoginPage";
import RegisterPage from "./pages/RegisterPage";
import AdminDashboard from "./pages/admin/AdminDashboard";
import MonitoringPage from "./pages/admin/MonitoringPage"; // Import new page
import ClientDashboard from "./pages/client/ClientDashboard";
import PrivateRoute from "./auth/PrivateRoute";

function App() {
    return (
        <Routes>
            <Route path="/login" element={<LoginPage />} />
            <Route path="/register" element={<RegisterPage />} />

            {/* Admin Dashboard */}
            <Route
                path="/admin"
                element={
                    <PrivateRoute role="ADMIN">
                        <AdminDashboard />
                    </PrivateRoute>
                }
            />

            {/* Shared Route for Monitoring */}
            <Route
                path="/monitoring"
                element={
                    <PrivateRoute> {/* Empty role = accessible to any authenticated user */}
                        <MonitoringPage />
                    </PrivateRoute>
                }
            />

            {/* Client Dashboard */}
            <Route
                path="/client"
                element={
                    <PrivateRoute role="CLIENT">
                        <ClientDashboard />
                    </PrivateRoute>
                }
            />


            <Route path="*" element={<Navigate to="/login" />} />
        </Routes>
    );
}

export default App;