import React, { useState } from 'react';
import { useQuery } from '@tanstack/react-query';
import { caseService } from '../../services/caseService';
import { Case } from '../../types';
import { Link } from 'react-router-dom';

const CaseList: React.FC = () => {
    const [page, setPage] = useState(0);
    const [search, setSearch] = useState('');
    const [statusFilter, setStatusFilter] = useState('');

    // Use search if there's a search term, otherwise get all cases
    const { data, isLoading, error } = useQuery({
        queryKey: ['cases', page, search, statusFilter],
        queryFn: () => {
            if (search.trim()) {
                return caseService.searchCases(page, 10, search.trim());
            } else {
                return caseService.getCases(page, 10);
            }
        },
        keepPreviousData: true,
    });

    const handleSearchSubmit = (e: React.FormEvent) => {
        e.preventDefault();
        setPage(0); // Reset to first page when searching
    };

    if (isLoading) return (
        <div className="flex justify-center items-center p-8">
            <div className="animate-spin rounded-full h-32 w-32 border-b-2 border-indigo-500"></div>
        </div>
    );

    if (error) return (
        <div className="text-red-500 p-8 text-center">
            <h3 className="text-lg font-semibold">Error loading cases</h3>
            <p className="mt-2">Please try again later.</p>
        </div>
    );

    return (
        <div className="p-6">
            <div className="sm:flex sm:items-center">
                <div className="sm:flex-auto">
                    <h1 className="text-xl font-semibold text-gray-900">Cases</h1>
                    <p className="mt-2 text-sm text-gray-700">
                        Manage all legal cases in your system
                    </p>
                </div>
                <div className="mt-4 sm:mt-0 sm:ml-16 sm:flex-none">
                    <button
                        className="inline-flex items-center justify-center rounded-md border border-transparent bg-indigo-600 px-4 py-2 text-sm font-medium text-white shadow-sm hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:ring-offset-2 sm:w-auto"
                        onClick={() => alert('Add Case functionality to be implemented')}
                    >
                        Add Case
                    </button>
                </div>
            </div>

            {/* Search and Filter Bar */}
            <div className="mt-6 flex flex-col sm:flex-row gap-4">
                <form onSubmit={handleSearchSubmit} className="flex-1 flex gap-2">
                    <input
                        type="text"
                        value={search}
                        onChange={(e) => setSearch(e.target.value)}
                        placeholder="Search cases by title, client name..."
                        className="flex-1 block rounded-md border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500 sm:text-sm"
                    />
                    <button
                        type="submit"
                        className="px-4 py-2 bg-indigo-600 text-white rounded-md hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-indigo-500"
                    >
                        Search
                    </button>
                    {search && (
                        <button
                            type="button"
                            onClick={() => {
                                setSearch('');
                                setPage(0);
                            }}
                            className="px-4 py-2 bg-gray-300 text-gray-700 rounded-md hover:bg-gray-400 focus:outline-none focus:ring-2 focus:ring-gray-500"
                        >
                            Clear
                        </button>
                    )}
                </form>
                <select
                    value={statusFilter}
                    onChange={(e) => {
                        setStatusFilter(e.target.value);
                        setPage(0);
                    }}
                    className="rounded-md border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500 sm:text-sm"
                >
                    <option value="">All Statuses</option>
                    <option value="OPEN">Open</option>
                    <option value="PENDING">Pending</option>
                    <option value="CLOSED">Closed</option>
                </select>
            </div>

            {/* Cases Table */}
            <div className="mt-8 flex flex-col">
                <div className="-my-2 -mx-4 overflow-x-auto sm:-mx-6 lg:-mx-8">
                    <div className="inline-block min-w-full py-2 align-middle md:px-6 lg:px-8">
                        <div className="overflow-hidden shadow ring-1 ring-black ring-opacity-5 md:rounded-lg">
                            {data?.content && data.content.length > 0 ? (
                                <table className="min-w-full divide-y divide-gray-300">
                                    <thead className="bg-gray-50">
                                    <tr>
                                        <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                                            Case
                                        </th>
                                        <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                                            Client
                                        </th>
                                        <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                                            Assigned Lawyer
                                        </th>
                                        <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                                            Status
                                        </th>
                                        <th className="relative px-6 py-3">
                                            <span className="sr-only">Actions</span>
                                        </th>
                                    </tr>
                                    </thead>
                                    <tbody className="bg-white divide-y divide-gray-200">
                                    {data.content.map((case_: Case) => (
                                        <tr key={case_.id}>
                                            <td className="px-6 py-4 whitespace-nowrap">
                                                <div>
                                                    <div className="text-sm font-medium text-gray-900">
                                                        {case_.title}
                                                    </div>
                                                    <div className="text-sm text-gray-500">
                                                        {case_.description ?
                                                            (case_.description.length > 50 ?
                                                                    `${case_.description.substring(0, 50)}...` :
                                                                    case_.description
                                                            ) :
                                                            'No description'
                                                        }
                                                    </div>
                                                </div>
                                            </td>
                                            <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                                                {case_.client?.name || 'N/A'}
                                            </td>
                                            <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                                                {case_.assignedLawyer?.name || 'Not assigned'}
                                            </td>
                                            <td className="px-6 py-4 whitespace-nowrap">
                                                    <span className={`inline-flex px-2 py-1 text-xs font-semibold rounded-full ${
                                                        case_.status === 'OPEN' ? 'bg-green-100 text-green-800' :
                                                            case_.status === 'PENDING' ? 'bg-yellow-100 text-yellow-800' :
                                                                'bg-gray-100 text-gray-800'
                                                    }`}>
                                                        {case_.status}
                                                    </span>
                                            </td>
                                            <td className="px-6 py-4 whitespace-nowrap text-right text-sm font-medium">
                                                <button
                                                    onClick={() => alert(`View case ${case_.id}`)}
                                                    className="text-indigo-600 hover:text-indigo-900 mr-4"
                                                >
                                                    View
                                                </button>
                                                <button
                                                    onClick={() => alert(`Edit case ${case_.id}`)}
                                                    className="text-indigo-600 hover:text-indigo-900"
                                                >
                                                    Edit
                                                </button>
                                            </td>
                                        </tr>
                                    ))}
                                    </tbody>
                                </table>
                            ) : (
                                <div className="text-center py-12">
                                    <div className="text-gray-400 mb-4">
                                        <svg className="mx-auto h-12 w-12" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
                                        </svg>
                                    </div>
                                    <h3 className="text-lg font-medium text-gray-900">No cases found</h3>
                                    <p className="text-gray-500 mt-2">
                                        {search ? 'Try adjusting your search criteria.' : 'Get started by creating your first case.'}
                                    </p>
                                </div>
                            )}
                        </div>
                    </div>
                </div>
            </div>

            {/* Pagination */}
            {data?.content && data.content.length > 0 && (
                <div className="mt-6 flex items-center justify-between">
                    <div className="text-sm text-gray-700">
                        Showing {page * 10 + 1} to {Math.min((page + 1) * 10, data?.totalElements || 0)} of{' '}
                        {data?.totalElements || 0} results
                    </div>
                    <div className="flex space-x-2">
                        <button
                            onClick={() => setPage(Math.max(0, page - 1))}
                            disabled={page === 0}
                            className="px-3 py-2 text-sm font-medium text-gray-700 bg-white border border-gray-300 rounded-md hover:bg-gray-50 disabled:opacity-50 disabled:cursor-not-allowed"
                        >
                            Previous
                        </button>
                        <span className="px-3 py-2 text-sm text-gray-700">
                            Page {page + 1} of {data?.totalPages || 1}
                        </span>
                        <button
                            onClick={() => setPage(page + 1)}
                            disabled={!data?.content || data.content.length < 10 || page >= (data?.totalPages - 1)}
                            className="px-3 py-2 text-sm font-medium text-gray-700 bg-white border border-gray-300 rounded-md hover:bg-gray-50 disabled:opacity-50 disabled:cursor-not-allowed"
                        >
                            Next
                        </button>
                    </div>
                </div>
            )}
        </div>
    );
};

export default CaseList;