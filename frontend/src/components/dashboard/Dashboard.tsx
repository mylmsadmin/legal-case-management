
import React from 'react';
import { useQuery } from '@tanstack/react-query';
import { api } from '../../utils/api';

interface DashboardStats {
    totalCases: number;
    openCases: number;
    closedCases: number;
    pendingCases: number;
    upcomingHearings: number;
    totalClients: number;
}

const Dashboard: React.FC = () => {
    const { data: stats, isLoading } = useQuery<DashboardStats>({
        queryKey: ['dashboard-stats'],
        queryFn: async () => {
            const response = await api.get('/dashboard/stats');
            return response.data;
        },
    });

    if (isLoading) {
        return <div className="flex justify-center items-center h-64">Loading...</div>;
    }

    const statCards = [
        { title: 'Total Cases', value: stats?.totalCases || 0, color: 'bg-blue-500' },
        { title: 'Open Cases', value: stats?.openCases || 0, color: 'bg-green-500' },
        { title: 'Pending Cases', value: stats?.pendingCases || 0, color: 'bg-yellow-500' },
        { title: 'Closed Cases', value: stats?.closedCases || 0, color: 'bg-gray-500' },
        { title: 'Upcoming Hearings', value: stats?.upcomingHearings || 0, color: 'bg-red-500' },
        { title: 'Total Clients', value: stats?.totalClients || 0, color: 'bg-purple-500' },
    ];

    return (
        <div className="p-6">
        <h1 className="text-3xl font-bold text-gray-900 mb-6">Dashboard</h1>

            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6 mb-8">
        {statCards.map((card, index) => (
                <div key={index} className="bg-white overflow-hidden shadow rounded-lg">
            <div className="p-5">
            <div className="flex items-center">
            <div className="flex-shrink-0">
            <div className={`w-8 h-8 ${card.color} rounded-md`}></div>
        </div>
        <div className="ml-5 w-0 flex-1">
    <dl>
        <dt className="text-sm font-medium text-gray-500 truncate">
        {card.title}
        </dt>
        <dd className="text-lg font-medium text-gray-900">
        {card.value}
        </dd>
        </dl>
        </div>
        </div>
        </div>
        </div>
))}
    </div>

    <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
    <div className="bg-white overflow-hidden shadow rounded-lg">
    <div className="px-4 py-5 sm:p-6">
    <h3 className="text-lg leading-6 font-medium text-gray-900 mb-4">
        Recent Cases
    </h3>
    <RecentCases />
    </div>
    </div>

    <div className="bg-white overflow-hidden shadow rounded-lg">
    <div className="px-4 py-5 sm:p-6">
    <h3 className="text-lg leading-6 font-medium text-gray-900 mb-4">
        Upcoming Hearings
    </h3>
    <UpcomingHearings />
    </div>
    </div>
    </div>
    </div>
);
};
// Recent Cases component
const RecentCases: React.FC = () => {
    const { data: cases, isLoading } = useQuery({
        queryKey: ['recent-cases'],
        queryFn: async () => {
            const response = await api.get('/cases?page=0&size=5&sort=createdDate,desc');
            return response.data.content;
        },
    });

    if (isLoading) return <div>Loading recent cases...</div>;

    return (
        <div className="space-y-3">
            {cases?.map((case_: any) => (
                <div key={case_.id} className="flex items-center justify-between py-2 border-b">
    <div>
        <p className="text-sm font-medium text-gray-900">{case_.title}</p>
        <p className="text-sm text-gray-500">{case_.client.name}</p>
        </div>
        <span className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${
        case_.status === 'OPEN' ? 'bg-green-100 text-green-800' :
            case_.status === 'PENDING' ? 'bg-yellow-100 text-yellow-800' :
                'bg-gray-100 text-gray-800'
    }`}>
    {case_.status}
    </span>
    </div>
))}
    </div>
);
};

// Upcoming Hearings component
const UpcomingHearings: React.FC = () => {
    const { data: hearings, isLoading } = useQuery({
        queryKey: ['upcoming-hearings'],
        queryFn: async () => {
            const response = await api.get('/hearings?upcoming=true&size=5');
            return response.data.content;
        },
    });

    if (isLoading) return <div>Loading upcoming hearings...</div>;

    return (
        <div className="space-y-3">
            {hearings?.map((hearing: any) => (
                <div key={hearing.id} className="flex items-center justify-between py-2 border-b">
    <div>
        <p className="text-sm font-medium text-gray-900">{hearing.case.title}</p>
        <p className="text-sm text-gray-500">
        {new Date(hearing.date).toLocaleDateString()}
        </p>
        </div>
        <div className="text-sm text-gray-500">
        {new Date(hearing.date).toLocaleTimeString()}
        </div>
        </div>
))}
    </div>
);
};

export default Dashboard;