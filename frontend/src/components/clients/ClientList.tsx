import React, { useState } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { api } from '../../utils/api';
import { Client } from '../../types';

const ClientList: React.FC = () => {
    const [showModal, setShowModal] = useState(false);
    const [editingClient, setEditingClient] = useState<Client | null>(null);
    const queryClient = useQueryClient();

    const { data: clients, isLoading } = useQuery({
        queryKey: ['clients'],
        queryFn: async () => {
            const response = await api.get('/clients');
            return response.data.content;
        },
    });

    const deleteClientMutation = useMutation({
        mutationFn: async (id: string) => {
            await api.delete(`/clients/${id}`);
        },
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: ['clients'] });
        },
    });

    const handleEdit = (client: Client) => {
        setEditingClient(client);
        setShowModal(true);
    };

    const handleDelete = (id: string) => {
        if (window.confirm('Are you sure you want to delete this client?')) {
            deleteClientMutation.mutate(id);
        }
    };

    if (isLoading) return <div className="flex justify-center p-8">Loading clients...</div>;

    return (
        <div className="p-6">
            <div className="sm:flex sm:items-center">
                <div className="sm:flex-auto">
                    <h1 className="text-xl font-semibold text-gray-900">Clients</h1>
                    <p className="mt-2 text-sm text-gray-700">
                        Manage all clients in your system
                    </p>
                </div>
                <div className="mt-4 sm:mt-0 sm:ml-16 sm:flex-none">
                    <button
                        onClick={() => {
                            setEditingClient(null);
                            setShowModal(true);
                        }}
                        className="inline-flex items-center justify-center rounded-md border border-transparent bg-indigo-600 px-4 py-2 text-sm font-medium text-white shadow-sm hover:bg-indigo-700"
                    >
                        Add Client
                    </button>
                </div>
            </div>

            <div className="mt-8 overflow-hidden shadow ring-1 ring-black ring-opacity-5 md:rounded-lg">
                <table className="min-w-full divide-y divide-gray-300">
                    <thead className="bg-gray-50">
                    <tr>
                        <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                            Name
                        </th>
                        <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                            Contact Info
                        </th>
                        <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                            Address
                        </th>
                        <th className="relative px-6 py-3">
                            <span className="sr-only">Actions</span>
                        </th>
                    </tr>
                    </thead>
                    <tbody className="bg-white divide-y divide-gray-200">
                    {clients?.map((client: Client) => (
                        <tr key={client.id}>
                            <td className="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">
                                {client.name}
                            </td>
                            <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                                {client.contactInfo}
                            </td>
                            <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                                {client.address}
                            </td>
                            <td className="px-6 py-4 whitespace-nowrap text-right text-sm font-medium">
                                <button
                                    onClick={() => handleEdit(client)}
                                    className="text-indigo-600 hover:text-indigo-900 mr-4"
                                >
                                    Edit
                                </button>
                                <button
                                    onClick={() => handleDelete(client.id)}
                                    className="text-red-600 hover:text-red-900"
                                >
                                    Delete
                                </button>
                            </td>
                        </tr>
                    ))}
                    </tbody>
                </table>
            </div>

            {showModal && (
                <ClientModal
                    client={editingClient}
                    onClose={() => setShowModal(false)}
                    onSave={() => {
                        setShowModal(false);
                        queryClient.invalidateQueries({ queryKey: ['clients'] });
                    }}
                />
            )}
        </div>
    );
};

// Client Modal Component
interface ClientModalProps {
    client: Client | null;
    onClose: () => void;
    onSave: () => void;
}

const ClientModal: React.FC<ClientModalProps> = ({ client, onClose, onSave }) => {
    const [formData, setFormData] = useState({
        name: client?.name || '',
        contactInfo: client?.contactInfo || '',
        address: client?.address || '',
    });

    const saveMutation = useMutation({
        mutationFn: async (data: typeof formData) => {
            if (client) {
                return api.put(`/clients/${client.id}`, data);
            } else {
                return api.post('/clients', data);
            }
        },
        onSuccess: () => {
            onSave();
        },
    });

    const handleSubmit = (e: React.FormEvent) => {
        e.preventDefault();
        saveMutation.mutate(formData);
    };

    return (
        <div className="fixed inset-0 bg-gray-600 bg-opacity-50 overflow-y-auto h-full w-full flex items-center justify-center">
            <div className="bg-white p-6 rounded-lg shadow-xl max-w-md w-full mx-4">
                <h3 className="text-lg font-medium text-gray-900 mb-4">
                    {client ? 'Edit Client' : 'Add New Client'}
                </h3>

                <form onSubmit={handleSubmit} className="space-y-4">
                    <div>
                        <label className="block text-sm font-medium text-gray-700">Name</label>
                        <input
                            type="text"
                            value={formData.name}
                            onChange={(e) => setFormData({ ...formData, name: e.target.value })}
                            className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500 sm:text-sm"
                            required
                        />
                    </div>

                    <div>
                        <label className="block text-sm font-medium text-gray-700">Contact Info</label>
                        <input
                            type="text"
                            value={formData.contactInfo}
                            onChange={(e) => setFormData({ ...formData, contactInfo: e.target.value })}
                            className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500 sm:text-sm"
                            required
                        />
                    </div>

                    <div>
                        <label className="block text-sm font-medium text-gray-700">Address</label>
                        <textarea
                            value={formData.address}
                            onChange={(e) => setFormData({ ...formData, address: e.target.value })}
                            className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500 sm:text-sm"
                            rows={3}
                            required
                        />
                    </div>

                    <div className="flex justify-end space-x-3 pt-4">
                        <button
                            type="button"
                            onClick={onClose}
                            className="px-4 py-2 text-sm font-medium text-gray-700 bg-white border border-gray-300 rounded-md hover:bg-gray-50"
                        >
                            Cancel
                        </button>
                        <button
                            type="submit"
                            disabled={saveMutation.isPending}
                            className="px-4 py-2 text-sm font-medium text-white bg-indigo-600 border border-transparent rounded-md hover:bg-indigo-700 disabled:opacity-50"
                        >
                            {saveMutation.isPending ? 'Saving...' : 'Save'}
                        </button>
                    </div>
                </form>
            </div>
        </div>
    );
};

export default ClientList;