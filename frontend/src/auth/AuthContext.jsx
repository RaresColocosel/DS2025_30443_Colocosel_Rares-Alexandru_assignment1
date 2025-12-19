import { createContext, useContext, useEffect, useState } from "react";

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
    // 1. Initialize state synchronously from localStorage
    const [authData, setAuthData] = useState(() => {
        const stored = localStorage.getItem("auth");
        return stored ? JSON.parse(stored) : { token: null, role: null, username: null };
    });

    const login = (jwtToken, userRole, userName) => {
        const newData = { token: jwtToken, role: userRole, username: userName };
        setAuthData(newData);
        localStorage.setItem("auth", JSON.stringify(newData));
    };

    const logout = () => {
        setAuthData({ token: null, role: null, username: null });
        localStorage.removeItem("auth");
    };

    // 2. Destructure for easier use in the value object
    const { token, role, username } = authData;

    return (
        <AuthContext.Provider value={{ token, role, username, login, logout }}>
            {children}
        </AuthContext.Provider>
    );
}

export const useAuth = () => useContext(AuthContext);
