import React from "react";
import { Routes, Route, Navigate } from "react-router-dom";
import { useAuth } from "./auth/AuthContext";

import Navbar from "./components/Navbar";
import LoginPage from "./pages/LoginPage";
import RegisterPage from "./pages/RegisterPage";
import AdminDashboard from "./pages/admin/AdminDashboard";
import ClientDashboard from "./pages/client/ClientDashboard";
import PrivateRoute from "./auth/PrivateRoute";
import "./App.css";

export default function App() {
    const { user, isAuthenticated } = useAuth();

    const defaultRedirect = () => {
        if (!isAuthenticated) return <Navigate to="/login" replace />;
        if (user?.role === "ADMIN") return <Navigate to="/admin" replace />;
        if (user?.role === "CLIENT") return <Navigate to="/client" replace />;
        return <Navigate to="/login" replace />;
    };

    return (
        <>
            <Navbar />
            <Routes>
                <Route path="/" element={defaultRedirect()} />

                <Route path="/login" element={<LoginPage />} />
                <Route path="/register" element={<RegisterPage />} />

                <Route
                    path="/admin"
                    element={
                        <PrivateRoute roles={["ADMIN"]}>
                            <AdminDashboard />
                        </PrivateRoute>
                    }
                />

                <Route
                    path="/client"
                    element={
                        <PrivateRoute roles={["CLIENT"]}>
                            <ClientDashboard />
                        </PrivateRoute>
                    }
                />

                <Route path="*" element={<Navigate to="/" replace />} />
            </Routes>
        </>
    );
}
