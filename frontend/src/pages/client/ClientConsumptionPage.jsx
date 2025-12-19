import { useEffect, useState } from "react";
import { useAuth } from "../../auth/AuthContext";
import { apiRequest, getMonitoringData } from "../../services/api";
import { BarChart, Bar, XAxis, YAxis, Tooltip, ResponsiveContainer, CartesianGrid } from 'recharts';

export default function ClientConsumptionPage() {
    const { token } = useAuth();
    const [devices, setDevices] = useState([]);
    const [selectedId, setSelectedId] = useState("");
    const [date, setDate] = useState(new Date().toISOString().split('T')[0]);
    const [chartData, setChartData] = useState([]);

    useEffect(() => {
        apiRequest("/me/devices", { token }).then(res => {
            setDevices(res || []);
            if(res && res.length) setSelectedId(res[0].id);
        });
    }, [token]);

    useEffect(() => {
        if(!selectedId || !date) return;
        getMonitoringData(token, selectedId, date).then(res => {
            const filled = Array.from({length: 24}, (_, i) => ({ hour: `${i}:00`, val: 0 }));
            res.forEach(r => {
                const h = new Date(r.hourTimestamp).getHours();
                if(filled[h]) filled[h].val = r.totalValue;
            });
            setChartData(filled);
        });
    }, [selectedId, date, token]);

    return (
        <div className="page-content">
            <h2>My Energy Charts</h2>
            <div className="panel">
                <select value={selectedId} onChange={e => setSelectedId(e.target.value)}>
                    {devices.map(d => <option key={d.id} value={d.id}>{d.name}</option>)}
                </select>
                <input type="date" value={date} onChange={e => setDate(e.target.value)} style={{marginLeft: '10px'}} />
            </div>
            <div className="panel" style={{ height: 400 }}>
                <ResponsiveContainer>
                    <BarChart data={chartData}>
                        <CartesianGrid strokeDasharray="3 3" />
                        <XAxis dataKey="hour" />
                        <YAxis />
                        <Tooltip />
                        <Bar dataKey="val" fill="#8884d8" name="Consumption (W)" />
                    </BarChart>
                </ResponsiveContainer>
            </div>
        </div>
    );
}