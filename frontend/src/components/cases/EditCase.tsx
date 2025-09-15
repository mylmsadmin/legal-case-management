// Add missing imports if needed
import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { useQuery, useMutation } from '@tanstack/react-query';
import { caseService } from '../../services/caseService';

const EditCase: React.FC = () => {
    const { id } = useParams<{ id: string }>();
    const navigate = useNavigate();
    const { data, isLoading, error } = useQuery({
        queryKey: ['case', id],
        queryFn: () => caseService.getCaseById(id!),
        enabled: !!id,
    });

    const [form, setForm] = useState({
        clientId: '',
        title: '',
        description: '',
        status: '',
        assignedLawyerId: '',
    });

    useEffect(() => {
        if (data) {
            setForm({
                clientId: data.client?.id || '',
                title: data.title || '',
                description: data.description || '',
                status: data.status || '',
                assignedLawyerId: data.assignedLawyer?.id || '',
            });
        }
    }, [data]);

    const mutation = useMutation({
        mutationFn: (updated: typeof form) => caseService.updateCase(id!, updated),
        onSuccess: () => navigate(`/cases/${id}`),
    });

    const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement | HTMLTextAreaElement>) => {
        setForm({ ...form, [e.target.name]: e.target.value });
    };

    const handleSubmit = (e: React.FormEvent) => {
        e.preventDefault();
        mutation.mutate(form);
    };

    if (isLoading) return <div>Loading...</div>;
    if (error || !data) return <div>Error loading case.</div>;

    return (
        <div className="p-6">
            <h2 className="text-xl font-bold mb-4">Edit Case</h2>
            <form onSubmit={handleSubmit} className="space-y-4">
                <div>
                    <label className="block font-medium">Client ID</label>
                    <input
                        name="clientId"
                        value={form.clientId}
                        onChange={handleChange}
                        className="border rounded px-2 py-1 w-full"
                        required
                    />
                </div>
                <div>
                    <label className="block font-medium">Title</label>
                    <input
                        name="title"
                        value={form.title}
                        onChange={handleChange}
                        className="border rounded px-2 py-1 w-full"
                        required
                    />
                </div>
                <div>
                    <label className="block font-medium">Description</label>
                    <textarea
                        name="description"
                        value={form.description}
                        onChange={handleChange}
                        className="border rounded px-2 py-1 w-full"
                    />
                </div>
                <div>
                    <label className="block font-medium">Status</label>
                    <select
                        name="status"
                        value={form.status}
                        onChange={handleChange}
                        className="border rounded px-2 py-1 w-full"
                        required
                    >
                        <option value="OPEN">Open</option>
                        <option value="PENDING">Pending</option>
                        <option value="CLOSED">Closed</option>
                    </select>
                </div>
                <div>
                    <label className="block font-medium">Assigned Lawyer ID</label>
                    <input
                        name="assignedLawyerId"
                        value={form.assignedLawyerId}
                        onChange={handleChange}
                        className="border rounded px-2 py-1 w-full"
                    />
                </div>
                {/* Arrays and nested objects can be displayed as read-only */}
                <div className="mt-4 font-semibold">Hearings:</div>
                {data.hearings?.map(hearing => (
                    <div key={hearing.id} className="border p-2 mb-2">
                        <div><strong>Date:</strong> {hearing.date}</div>
                        <div><strong>Notes:</strong> {hearing.notes}</div>
                    </div>
                ))}
                <div className="mt-4 font-semibold">Documents:</div>
                {data.documents?.map(doc => (
                    <div key={doc.id} className="border p-2 mb-2">
                        <div><strong>Name:</strong> {doc.name}</div>
                        <div><strong>Type:</strong> {doc.type}</div>
                    </div>
                ))}
                <button
                    type="submit"
                    className="bg-indigo-600 text-white px-4 py-2 rounded"
                    disabled={mutation.isLoading}
                >
                    Save
                </button>
            </form>
        </div>
    );
};

export default EditCase;