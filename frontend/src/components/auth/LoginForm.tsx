
import React, { useState } from 'react';
import { useMutation } from '@tanstack/react-query';
import { authService } from '../../services/authService';
import { LoginRequest } from '../../types';

interface LoginFormProps {
    onSuccess: () => void;
}

const LoginForm: React.FC<LoginFormProps> = ({ onSuccess }) => {
    const [credentials, setCredentials] = useState<LoginRequest>({ email: '', password: '' });
    const [errors, setErrors] = useState<Partial<LoginRequest>>({});

    const loginMutation = useMutation({
        mutationFn: authService.login,
        onSuccess: (data) => {
            localStorage.setItem('jwt_token', data.token);
            onSuccess();
        },
        onError: () => {
            setErrors({ email: 'Invalid credentials' });
        },
    });

    const handleSubmit = (e: React.FormEvent) => {
        e.preventDefault();

        // Basic validation
        const newErrors: Partial<LoginRequest> = {};
        if (!credentials.email) newErrors.email = 'Email is required';
        if (!credentials.password) newErrors.password = 'Password is required';

        if (Object.keys(newErrors).length > 0) {
            setErrors(newErrors);
            return;
        }

        loginMutation.mutate(credentials);
    };

    return (
        <div className="min-h-screen flex items-center justify-center bg-gray-50 py-12 px-4 sm:px-6 lg:px-8">
            <div className="max-w-md w-full space-y-8">
                <div>
                    <h2 className="mt-6 text-center text-3xl font-extrabold text-gray-900">
                        Legal Case Management
                    </h2>
                    <p className="mt-2 text-center text-sm text-gray-600">
                        Sign in to your account
                    </p>
                </div>
                <form className="mt-8 space-y-6" onSubmit={handleSubmit}>
                    <div className="rounded-md shadow-sm -space-y-px">
                        <div>
                            <input
                                type="email"
                                value={credentials.email}
                                onChange={(e) => setCredentials({ ...credentials, email: e.target.value })}
                                className="appearance-none rounded-none relative block w-full px-3 py-2 border border-gray-300 placeholder-gray-500 text-gray-900 rounded-t-md focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 focus:z-10 sm:text-sm"
                                placeholder="Email address"
                            />
                            {errors.email && <p className="text-red-500 text-xs mt-1">{errors.email}</p>}
                        </div>
                        <div>
                            <input
                                type="password"
                                value={credentials.password}
                                onChange={(e) => setCredentials({ ...credentials, password: e.target.value })}
                                className="appearance-none rounded-none relative block w-full px-3 py-2 border border-gray-300 placeholder-gray-500 text-gray-900 rounded-b-md focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 focus:z-10 sm:text-sm"
                                placeholder="Password"
                            />
                            {errors.password && <p className="text-red-500 text-xs mt-1">{errors.password}</p>}
                        </div>
                    </div>

                    <div>
                        <button
                            type="submit"
                            disabled={loginMutation.isPending}
                            className="group relative w-full flex justify-center py-2 px-4 border border-transparent text-sm font-medium rounded-md text-white bg-indigo-600 hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500 disabled:opacity-50"
                        >
                            {loginMutation.isPending ? 'Signing in...' : 'Sign in'}
                        </button>
                    </div>
                </form>
            </div>
        </div>
    );
};

export default LoginForm;
