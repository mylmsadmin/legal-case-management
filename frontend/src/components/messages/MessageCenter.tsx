import React, { useState } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { api } from '../../utils/api';
import { Message, User } from '../../types';

const MessageCenter: React.FC = () => {
    const [selectedUser, setSelectedUser] = useState<User | null>(null);
    const [newMessage, setNewMessage] = useState('');
    const queryClient = useQueryClient();

    const { data: users } = useQuery({
        queryKey: ['users'],
        queryFn: async () => {
            const response = await api.get('/users');
            return response.data;
        },
    });

    const { data: messages } = useQuery({
        queryKey: ['messages', selectedUser?.id],
        queryFn: async () => {
            if (!selectedUser) return [];
            const response = await api.get(`/messages?userId=${selectedUser.id}`);
            return response.data;
        },
        enabled: !!selectedUser,
    });

    const sendMessageMutation = useMutation({
        mutationFn: async (messageData: { receiverId: string; content: string }) => {
            const response = await api.post('/messages', messageData);
            return response.data;
        },
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: ['messages', selectedUser?.id] });
            setNewMessage('');
        },
    });

    const handleSendMessage = (e: React.FormEvent) => {
        e.preventDefault();
        if (selectedUser && newMessage.trim()) {
            sendMessageMutation.mutate({
                receiverId: selectedUser.id,
                content: newMessage.trim(),
            });
        }
    };

    return (
        <div className="p-6">
            <h1 className="text-xl font-semibold text-gray-900 mb-6">Messages</h1>

            <div className="flex h-96 bg-white rounded-lg shadow overflow-hidden">
                {/* Users List */}
                <div className="w-1/3 bg-gray-50 border-r">
                    <div className="p-4 border-b">
                        <h3 className="text-sm font-medium text-gray-900">Contacts</h3>
                    </div>
                    <div className="overflow-y-auto">
                        {users?.map((user: User) => (
                            <div
                                key={user.id}
                                onClick={() => setSelectedUser(user)}
                                className={`p-4 cursor-pointer hover:bg-gray-100 border-b ${
                                    selectedUser?.id === user.id ? 'bg-indigo-50 border-indigo-200' : ''
                                }`}
                            >
                                <div className="font-medium text-sm text-gray-900">{user.name}</div>
                                <div className="text-xs text-gray-500">{user.role}</div>
                            </div>
                        ))}
                    </div>
                </div>

                {/* Message Area */}
                <div className="flex-1 flex flex-col">
                    {selectedUser ? (
                        <>
                            {/* Header */}
                            <div className="p-4 border-b bg-white">
                                <h3 className="font-medium text-gray-900">{selectedUser.name}</h3>
                                <p className="text-sm text-gray-500">{selectedUser.email}</p>
                            </div>

                            {/* Messages */}
                            <div className="flex-1 overflow-y-auto p-4 space-y-4">
                                {messages?.map((message: Message) => (
                                    <div
                                        key={message.id}
                                        className={`flex ${
                                            message.sender.id === selectedUser.id ? 'justify-start' : 'justify-end'
                                        }`}
                                    >
                                        <div
                                            className={`max-w-xs lg:max-w-md px-4 py-2 rounded-lg ${
                                                message.sender.id === selectedUser.id
                                                    ? 'bg-gray-200 text-gray-900'
                                                    : 'bg-indigo-600 text-white'
                                            }`}
                                        >
                                            <p className="text-sm">{message.content}</p>
                                            <p className="text-xs mt-1 opacity-75">
                                                {new Date(message.timestamp).toLocaleTimeString()}
                                            </p>
                                        </div>
                                    </div>
                                ))}
                            </div>

                            {/* Message Input */}
                            <form onSubmit={handleSendMessage} className="p-4 border-t bg-white">
                                <div className="flex space-x-2">
                                    <input
                                        type="text"
                                        value={newMessage}
                                        onChange={(e) => setNewMessage(e.target.value)}
                                        placeholder="Type your message..."
                                        className="flex-1 rounded-md border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500 sm:text-sm"
                                    />
                                    <button
                                        type="submit"
                                        disabled={!newMessage.trim() || sendMessageMutation.isPending}
                                        className="px-4 py-2 bg-indigo-600 text-white rounded-md hover:bg-indigo-700 disabled:opacity-50"
                                    >
                                        Send
                                    </button>
                                </div>
                            </form>
                        </>
                    ) : (
                        <div className="flex-1 flex items-center justify-center text-gray-500">
                            Select a contact to start messaging
                        </div>
                    )}
                </div>
            </div>
        </div>
    );
};

export default MessageCenter;