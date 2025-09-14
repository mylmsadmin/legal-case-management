import React, { useState } from 'react';
import { useQuery } from '@tanstack/react-query';
import { Link } from 'react-router-dom';
import {
    Scale,
    Users,
    FileText,
    Calendar,
    MessageSquare,
    TrendingUp,
    Clock,
    CheckCircle,
    AlertCircle,
    Menu,
    X,
    Bell,
    Search,
    User,
    LogOut,
    Home,
    Briefcase,
    Gavel
} from 'lucide-react';
import { api } from '../../utils/api';

const Dashboard = () => {
    const [sidebarOpen, setSidebarOpen] = useState(false);

    // Fetch dashboard statistics
    const { data: stats, isLoading: statsLoading } = useQuery({
        queryKey: ['dashboard-stats'],
        queryFn: async () => {
            const response = await api.get('/dashboard/stats');
            return response.data;
        },
    });

    // Fetch recent cases
    const { data: recentCases, isLoading: casesLoading } = useQuery({
        queryKey: ['recent-cases'],
        queryFn: async () => {
            const response = await api.get('/dashboard/recent-cases');
            return response.data;
        },
    });

    // Fetch upcoming hearings
    const { data: upcomingHearings, isLoading: hearingsLoading } = useQuery({
        queryKey: ['upcoming-hearings'],
        queryFn: async () => {
            const response = await api.get('/dashboard/upcoming-hearings');
            return response.data;
        },
    });

    // Default stats if loading or error
    const dashboardStats = [
        {
            label: 'Total Cases',
            value: statsLoading ? '...' : (stats?.totalCases || '0'),
            icon: FileText,
            color: 'bg-blue-500',
            trend: '+0%'
        },
        {
            label: 'Open Cases',
            value: statsLoading ? '...' : (stats?.openCases || '0'),
            icon: AlertCircle,
            color: 'bg-orange-500',
            trend: '+0%'
        },
        {
            label: 'Pending Cases',
            value: statsLoading ? '...' : (stats?.pendingCases || '0'),
            icon: Clock,
            color: 'bg-yellow-500',
            trend: '+0%'
        },
        {
            label: 'Closed Cases',
            value: statsLoading ? '...' : (stats?.closedCases || '0'),
            icon: CheckCircle,
            color: 'bg-green-500',
            trend: '+0%'
        },
        {
            label: 'Upcoming Hearings',
            value: hearingsLoading ? '...' : (stats?.upcomingHearings || '0'),
            icon: Calendar,
            color: 'bg-purple-500',
            trend: '+0%'
        },
        {
            label: 'Total Clients',
            value: statsLoading ? '...' : (stats?.totalClients || '0'),
            icon: Users,
            color: 'bg-indigo-500',
            trend: '+0%'
        }
    ];

    const navigationItems = [
        { name: 'Dashboard', icon: Home, href: '/dashboard', current: true },
        { name: 'Cases', icon: FileText, href: '/cases', current: false },
        { name: 'Clients', icon: Users, href: '/clients', current: false },
        { name: 'Hearings', icon: Calendar, href: '/hearings', current: false },
        { name: 'Messages', icon: MessageSquare, href: '/messages', current: false },
    ];

    const handleLogout = () => {
        localStorage.removeItem('jwt_token');
        window.location.href = '/login';
    };

    const Sidebar = () => (
        <div className={`fixed inset-y-0 left-0 z-50 w-64 bg-slate-900 transform ${sidebarOpen ? 'translate-x-0' : '-translate-x-full'} transition-transform duration-300 ease-in-out lg:translate-x-0 lg:static lg:inset-0`}>
            <div className="flex items-center justify-between h-16 px-4 bg-slate-800">
                <div className="flex items-center space-x-3">
                    <Scale className="h-8 w-8 text-blue-400" />
                    <span className="text-white font-bold text-lg">LegalCMS</span>
                </div>
                <button
                    onClick={() => setSidebarOpen(false)}
                    className="lg:hidden text-gray-400 hover:text-white"
                >
                    <X className="h-6 w-6" />
                </button>
            </div>

            <nav className="mt-8 px-4 space-y-2">
                {navigationItems.map((item) => (
                    <Link
                        key={item.name}
                        to={item.href}
                        className={`flex items-center px-4 py-3 text-sm font-medium rounded-lg transition-colors ${
                            item.current
                                ? 'bg-blue-600 text-white'
                                : 'text-gray-300 hover:bg-slate-800 hover:text-white'
                        }`}
                    >
                        <item.icon className="mr-3 h-5 w-5" />
                        {item.name}
                    </Link>
                ))}
            </nav>

            <div className="absolute bottom-4 left-4 right-4">
                <div className="bg-slate-800 rounded-lg p-4">
                    <div className="flex items-center space-x-3">
                        <div className="bg-blue-500 rounded-full p-2">
                            <User className="h-5 w-5 text-white" />
                        </div>
                        <div className="flex-1 min-w-0">
                            <p className="text-sm font-medium text-white truncate">Legal User</p>
                            <p className="text-xs text-gray-400 truncate">System User</p>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );

    const Header = () => (
        <header className="bg-white shadow-sm border-b border-gray-200">
            <div className="flex items-center justify-between h-16 px-4 lg:px-6">
                <div className="flex items-center space-x-4">
                    <button
                        onClick={() => setSidebarOpen(true)}
                        className="lg:hidden text-gray-500 hover:text-gray-700"
                    >
                        <Menu className="h-6 w-6" />
                    </button>
                    <h1 className="text-2xl font-bold text-gray-900">Dashboard</h1>
                </div>

                <div className="flex items-center space-x-4">
                    <div className="relative">
                        <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 h-4 w-4" />
                        <input
                            type="text"
                            placeholder="Search cases, clients..."
                            className="pl-10 pr-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                        />
                    </div>
                    <button className="relative p-2 text-gray-500 hover:text-gray-700">
                        <Bell className="h-5 w-5" />
                        <span className="absolute top-0 right-0 h-2 w-2 bg-red-500 rounded-full"></span>
                    </button>
                    <button
                        onClick={handleLogout}
                        className="flex items-center space-x-2 text-gray-700 hover:text-gray-900"
                    >
                        <LogOut className="h-5 w-5" />
                        <span className="hidden sm:block">Logout</span>
                    </button>
                </div>
            </div>
        </header>
    );

    const StatCard = ({ stat }) => (
        <div className="bg-white rounded-xl shadow-sm p-6 border border-gray-100">
            <div className="flex items-center justify-between">
                <div>
                    <p className="text-sm font-medium text-gray-600">{stat.label}</p>
                    <p className="text-3xl font-bold text-gray-900 mt-2">{stat.value}</p>
                    <div className="flex items-center mt-2">
                        <TrendingUp className="h-4 w-4 text-green-500 mr-1" />
                        <span className="text-sm text-green-600">{stat.trend}</span>
                        <span className="text-sm text-gray-500 ml-1">from last month</span>
                    </div>
                </div>
                <div className={`${stat.color} rounded-full p-3`}>
                    <stat.icon className="h-6 w-6 text-white" />
                </div>
            </div>
        </div>
    );

    return (
        <div className="flex h-screen bg-gray-50">
            <Sidebar />

            {/* Overlay for mobile */}
            {sidebarOpen && (
                <div
                    className="fixed inset-0 bg-black bg-opacity-50 z-40 lg:hidden"
                    onClick={() => setSidebarOpen(false)}
                />
            )}

            <div className="flex-1 flex flex-col overflow-hidden">
                <Header />

                <main className="flex-1 overflow-y-auto p-4 lg:p-6">
                    {/* Stats Grid */}
                    <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6 mb-8">
                        {dashboardStats.map((stat, index) => (
                            <StatCard key={index} stat={stat} />
                        ))}
                    </div>

                    {/* Content Grid */}
                    <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
                        {/* Recent Cases */}
                        <div className="bg-white rounded-xl shadow-sm border border-gray-100">
                            <div className="flex items-center justify-between p-6 border-b border-gray-100">
                                <h2 className="text-lg font-semibold text-gray-900">Recent Cases</h2>
                                <Link to="/cases" className="text-sm text-blue-600 hover:text-blue-700 font-medium">
                                    View all
                                </Link>
                            </div>
                            <div className="p-6 space-y-4">
                                {casesLoading ? (
                                    <div className="text-center py-4">Loading recent cases...</div>
                                ) : recentCases && recentCases.length > 0 ? (
                                    recentCases.map((case_) => (
                                        <div key={case_.id} className="flex items-center justify-between p-4 bg-gray-50 rounded-lg">
                                            <div className="flex items-center space-x-3">
                                                <Briefcase className="h-5 w-5 text-gray-500" />
                                                <div>
                                                    <p className="font-medium text-gray-900">{case_.title}</p>
                                                    <p className="text-sm text-gray-500">{case_.client}</p>
                                                </div>
                                            </div>
                                            <div className="text-right">
                                                <span className={`inline-flex px-2 py-1 text-xs font-medium rounded-full ${
                                                    case_.status === 'OPEN' ? 'bg-green-100 text-green-800' :
                                                        case_.status === 'PENDING' ? 'bg-yellow-100 text-yellow-800' :
                                                            'bg-gray-100 text-gray-800'
                                                }`}>
                                                    {case_.status}
                                                </span>
                                                <p className="text-xs text-gray-500 mt-1">{case_.createdAt}</p>
                                            </div>
                                        </div>
                                    ))
                                ) : (
                                    <div className="text-center py-8">
                                        <FileText className="h-12 w-12 text-gray-400 mx-auto mb-4" />
                                        <p className="text-gray-500">No recent cases</p>
                                        <Link to="/cases" className="mt-2 text-blue-600 hover:text-blue-700 font-medium">
                                            Create your first case
                                        </Link>
                                    </div>
                                )}
                            </div>
                        </div>

                        {/* Upcoming Hearings */}
                        <div className="bg-white rounded-xl shadow-sm border border-gray-100">
                            <div className="flex items-center justify-between p-6 border-b border-gray-100">
                                <h2 className="text-lg font-semibold text-gray-900">Upcoming Hearings</h2>
                                <Link to="/hearings" className="text-sm text-blue-600 hover:text-blue-700 font-medium">
                                    View all
                                </Link>
                            </div>
                            <div className="p-6 space-y-4">
                                {hearingsLoading ? (
                                    <div className="text-center py-4">Loading upcoming hearings...</div>
                                ) : upcomingHearings && upcomingHearings.length > 0 ? (
                                    upcomingHearings.map((hearing) => (
                                        <div key={hearing.id} className="flex items-center justify-between p-4 bg-gray-50 rounded-lg">
                                            <div className="flex items-center space-x-3">
                                                <Gavel className="h-5 w-5 text-gray-500" />
                                                <div>
                                                    <p className="font-medium text-gray-900">{hearing.case}</p>
                                                    <p className="text-sm text-gray-500">{hearing.notes}</p>
                                                </div>
                                            </div>
                                            <div className="text-right">
                                                <p className="text-sm font-medium text-gray-900">{hearing.date}</p>
                                                <p className="text-xs text-gray-500">{new Date(hearing.date).toLocaleTimeString()}</p>
                                            </div>
                                        </div>
                                    ))
                                ) : (
                                    <div className="text-center py-8">
                                        <Calendar className="h-12 w-12 text-gray-400 mx-auto mb-4" />
                                        <p className="text-gray-500">No upcoming hearings</p>
                                        <Link to="/hearings" className="mt-2 text-blue-600 hover:text-blue-700 font-medium">
                                            Schedule a hearing
                                        </Link>
                                    </div>
                                )}
                            </div>
                        </div>
                    </div>
                </main>
            </div>
        </div>
    );
};

export default Dashboard;