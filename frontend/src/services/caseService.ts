import { api } from '../utils/api';
import { Case } from '../types';

export const caseService = {
    getCases: async (page = 0, size = 10, search = ''): Promise<{ content: Case[]; totalElements: number }> => {
        const response = await api.get(`/cases?page=${page}&size=${size}&search=${search}`);
        return response.data;
    },

    getCaseById: async (id: string): Promise<Case> => {
        const response = await api.get(`/cases/${id}`);
        return response.data;
    },

    createCase: async (caseData: Partial<Case>): Promise<Case> => {
        const response = await api.post('/cases', caseData);
        return response.data;
    },

    updateCase: async (id: string, caseData: Partial<Case>): Promise<Case> => {
        const response = await api.put(`/cases/${id}`, caseData);
        return response.data;
    },

    deleteCase: async (id: string): Promise<void> => {
        await api.delete(`/cases/${id}`);
    },
};
