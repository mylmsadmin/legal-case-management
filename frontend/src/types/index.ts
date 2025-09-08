export interface User {
    id: string;
    name: string;
    email: string;
    role: 'ADMIN' | 'LAWYER' | 'PARALEGAL';
    contactNumber: string;
}

export interface Client {
    id: string;
    name: string;
    contactInfo: string;
    address: string;
    cases?: Case[];
}

export interface Case {
    id: string;
    title: string;
    description: string;
    status: 'OPEN' | 'CLOSED' | 'PENDING';
    client: Client;
    assignedLawyer: User;
    hearings?: Hearing[];
    documents?: Document[];
    createdDate?: string;
}

export interface Hearing {
    id: string;
    date: string;
    notes: string;
    case: Case;
}

export interface Document {
    id: string;
    name: string;
    type: string;
    uploadDate: string;
    case: Case;
}

export interface Message {
    id: string;
    sender: User;
    receiver: User;
    content: string;
    timestamp: string;
}

export interface AuthResponse {
    token: string;
    user: User;
}

export interface LoginRequest {
    email: string;
    password: string;
}

export interface RegisterRequest {
    name: string;
    email: string;
    password: string;
    role: 'ADMIN' | 'LAWYER' | 'PARALEGAL';
    contactNumber: string;
}