import React, { useState } from 'react';
import { useMutation, useQueryClient } from '@tanstack/react-query';
import { api } from '../../utils/api';

interface DocumentUploadProps {
    caseId: string;
    onUploadComplete?: () => void;
}

const DocumentUpload: React.FC<DocumentUploadProps> = ({ caseId, onUploadComplete }) => {
    const [selectedFile, setSelectedFile] = useState<File | null>(null);
    const [] = useState(false);
    const queryClient = useQueryClient();

    const uploadMutation = useMutation({
        mutationFn: async (file: File) => {
            const formData = new FormData();
            formData.append('file', file);
            formData.append('caseId', caseId);

            const response = await api.post('/documents/upload', formData, {
                headers: {
                    'Content-Type': 'multipart/form-data',
                },
            });
            return response.data;
        },
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: ['case-documents', caseId] });
            setSelectedFile(null);
            onUploadComplete?.();
        },
    });

    const handleFileSelect = (e: React.ChangeEvent<HTMLInputElement>) => {
        const file = e.target.files?.[0];
        if (file) {
            setSelectedFile(file);
        }
    };

    const handleUpload = () => {
        if (selectedFile) {
            uploadMutation.mutate(selectedFile);
        }
    };

    return (
        <div className="max-w-lg mx-auto">
            <div className="mb-4">
                <label className="block text-sm font-medium text-gray-700 mb-2">
                    Select Document
                </label>
                <input
                    type="file"
                    onChange={handleFileSelect}
                    className="block w-full text-sm text-gray-500 file:mr-4 file:py-2 file:px-4 file:rounded-full file:border-0 file:text-sm file:font-semibold file:bg-indigo-50 file:text-indigo-700 hover:file:bg-indigo-100"
                    accept=".pdf,.doc,.docx,.jpg,.jpeg,.png"
                />
            </div>

            {selectedFile && (
                <div className="mb-4 p-4 bg-gray-50 rounded-lg">
                    <p className="text-sm text-gray-700">
                        Selected: {selectedFile.name} ({(selectedFile.size / 1024 / 1024).toFixed(2)} MB)
                    </p>
                </div>
            )}

            <button
                onClick={handleUpload}
                disabled={!selectedFile || uploadMutation.isPending}
                className="w-full flex justify-center py-2 px-4 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-indigo-600 hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500 disabled:opacity-50"
            >
                {uploadMutation.isPending ? 'Uploading...' : 'Upload Document'}
            </button>


        </div>
    );
};

export default DocumentUpload;