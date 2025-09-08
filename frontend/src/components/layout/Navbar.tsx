import React from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { authService } from '../../services/authService';

const Navbar: React.FC = () => {
    const navigate = useNavigate();

    const handleLogout = () => {
        authService.logout();
        navigate('/login');
    };

    return (
        <nav className="bg-white shadow-lg">
            <div className="max-w-7xl mx-auto px-4">
                <div className="flex justify-between h-16">
                    <div className="flex items-center">
                        <Link to="/dashboard" className="flex-shrink-0">
                            <h1 className="text-xl font-bold text-indigo-600">
                                Legal Case Management
                            </h1>
                        </Link>
                    </div>

                    <div className="flex items-center space-x-4">
                        <Link
                            to="/dashboard"
                            className="text-gray-700 hover:text-indigo-600 px-3 py-2 rounded-md text-sm font-medium"
                        >
                            Dashboard
                        </Link>
                        <Link
                            to="/cases"
                            className="text-gray-700 hover:text-indigo-600 px-3 py-2 rounded-md text-sm font-medium"
                        >
                            Cases
                        </Link>
                        <Link
                            to="/clients"
                            className="text-gray-700 hover:text-indigo-600 px-3 py-2 rounded-md text-sm font-medium"
                        >
                            Clients
                        </Link>
                        <Link
                            to="/hearings"
                            className="text-gray-700 hover:text-indigo-600 px-3 py-2 rounded-md text-sm font-medium"
                        >
                            Hearings
                        </Link>
                        <Link
                            to="/messages"
                            className="text-gray-700 hover:text-indigo-600 px-3 py-2 rounded-md text-sm font-medium"
                        >
                            Messages
                        </Link>
                        <button
                            onClick={handleLogout}
                            className="bg-indigo-600 text-white px-4 py-2 rounded-md text-sm font-medium hover:bg-indigo-700"
                        >
                            Logout
                        </button>
                    </div>
                </div>
            </div>
        </nav>
    );
};

export default Navbar;