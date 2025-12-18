// src/components/Header.jsx
import React from "react";
import { Link, useNavigate } from "react-router-dom";
import { useAuth } from "../auth/AuthContext";

export default function Header() {
    const { user, isAuthenticated, isAdmin, isClient, logout } = useAuth();
    const navigate = useNavigate();

    const handleLogout = () => {
        logout();
        navigate("/login", { replace: true });
    };

    return (
        <header className="ems-header">
            <div className="ems-header-left" onClick={() => navigate("/")}>
                <span className="ems-logo">EMS</span>
            </div>

            <nav className="ems-header-nav">
                {isAdmin && <Link to="/admin">Admin</Link>}
                {isClient && <Link to="/client">My devices</Link>}
            </nav>

            <div className="ems-header-right">
                {isAuthenticated && user ? (
                    <>
            <span className="ems-username">
              {user.username} ({user.role})
            </span>
                        <button className="btn-secondary" onClick={handleLogout}>
                            Logout
                        </button>
                    </>
                ) : (
                    <>
                        <Link to="/login" className="btn-secondary">
                            Login
                        </Link>
                        <Link to="/register" className="btn-primary">
                            Register
                        </Link>
                    </>
                )}
            </div>
        </header>
    );
}
