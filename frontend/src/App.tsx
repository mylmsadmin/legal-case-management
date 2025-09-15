import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import LoginForm from './components/auth/LoginForm.tsx';
import Dashboard from './components/dashboard/Dashboard';
import CaseList from './components/cases/CaseList';
import ClientList from './components/clients/ClientList';
import MessageCenter from './components/messages/MessageCenter';
import Navbar from './components/layout/Navbar';
import ViewCase from './components/cases/ViewCase';
import EditCase from './components/cases/EditCase';


const queryClient = new QueryClient();

// Protected Route Component
const ProtectedRoute: React.FC<{ children: React.ReactNode }> = ({ children }) => {
    const token = localStorage.getItem('jwt_token');

    if (!token) {
        return <Navigate to="/login" replace />;
    }

    return (
        <>
            <Navbar />
            <main className="min-h-screen bg-gray-50">
                {children}
            </main>
        </>
    );
};

const App: React.FC = () => {
    return (
        <QueryClientProvider client={queryClient}>
            <Router>
                <div className="App">
                    <Routes>
                        <Route
                            path="/login"
                            element={
                                <LoginForm onSuccess={() => window.location.href = '/dashboard'} />
                            }
                        />
                        <Route
                            path="/dashboard"
                            element={
                                <ProtectedRoute>
                                    <Dashboard />
                                </ProtectedRoute>
                            }
                        />
                        <Route
                            path="/cases"
                            element={
                                <ProtectedRoute>
                                    <CaseList />
                                </ProtectedRoute>
                            }
                        />
                        <Route path="/cases/:id"
                            element={
                        <ProtectedRoute>
                        <ViewCase />
                       </ProtectedRoute>
                            }
                               />

                        <Route path="/cases/:id/edit"
                               element={
                                   <ProtectedRoute>
                                       <EditCase />
                                   </ProtectedRoute>
                               }
                        />


                        <Route
                            path="/clients"
                            element={
                                <ProtectedRoute>
                                    <ClientList />
                                </ProtectedRoute>
                            }
                        />
                        <Route
                            path="/messages"
                            element={
                                <ProtectedRoute>
                                    <MessageCenter />
                                </ProtectedRoute>
                            }
                        />
                        <Route path="/" element={<Navigate to="/dashboard" replace />} />
                    </Routes>
                </div>
            </Router>
        </QueryClientProvider>
    );
};

export default App;