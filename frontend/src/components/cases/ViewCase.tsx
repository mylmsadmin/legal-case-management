import React from 'react';
import { useParams } from 'react-router-dom';
import { useQuery } from '@tanstack/react-query';
import { caseService } from '../../services/caseService';

const ViewCase: React.FC = () => {
    const { id } = useParams<{ id: string }>();
    const { data, isLoading, error } = useQuery({
        queryKey: ['case', id],
        queryFn: () => caseService.getCaseById(id!),
        enabled: !!id,
    });

    if (isLoading) return <div>Loading...</div>;
    if (error || !data) return <div>Error loading case.</div>;

    return (
        <div className="p-6">
            <h2 className="text-xl font-bold mb-4">Case Details</h2>
            <div>
                <strong>Case ID:</strong> {data.id}
            </div>
            <div>
                <strong>Title:</strong> {data.title}
            </div>
            <div>
                <strong>Description:</strong> {data.description}
            </div>
            <div>
                <strong>Status:</strong> {data.status}
            </div>
            <div>
                <strong>Created At:</strong> {data.createdAt}
            </div>
            <div>
                <strong>Updated At:</strong> {data.updatedAt ?? 'N/A'}
            </div>
            <div>
                <strong>Next Hearing Date:</strong> {data.nextHearingDate ?? 'N/A'}
            </div>
            <div>
                <strong>Total Hearings:</strong> {data.totalHearings}
            </div>
            <div>
                <strong>Total Documents:</strong> {data.totalDocuments}
            </div>

            {/* Client Details */}
            <div className="mt-4 font-semibold">Client Details:</div>
            <div>
                <strong>Client ID:</strong> {data.client?.id}
            </div>
            <div>
                <strong>Client Name:</strong> {data.client?.name}
            </div>
            <div>
                <strong>Contact Info:</strong> {data.client?.contactInfo}
            </div>
            <div>
                <strong>Address:</strong> {data.client?.address}
            </div>
            <div>
                <strong>Total Cases:</strong> {data.client?.totalCases ?? 'N/A'}
            </div>

            {/* Assigned Lawyer */}
            <div className="mt-4 font-semibold">Assigned Lawyer:</div>
            <div>
                <strong>Lawyer ID:</strong> {data.assignedLawyer?.id}
            </div>
            <div>
                <strong>Name:</strong> {data.assignedLawyer?.name}
            </div>
            <div>
                <strong>Email:</strong> {data.assignedLawyer?.email}
            </div>
            <div>
                <strong>Role:</strong> {data.assignedLawyer?.role}
            </div>
            <div>
                <strong>Contact Number:</strong> {data.assignedLawyer?.contactNumber}
            </div>

            {/* Hearings */}
            <div className="mt-4 font-semibold">Hearings:</div>
            {data.hearings?.map(hearing => (
                <div key={hearing.id} className="border p-2 mb-2">
                    <div><strong>Hearing ID:</strong> {hearing.id}</div>
                    <div><strong>Date:</strong> {hearing.date}</div>
                    <div><strong>Notes:</strong> {hearing.notes}</div>
                    <div><strong>Case ID:</strong> {hearing.caseId}</div>
                    <div><strong>Case Title:</strong> {hearing.caseTitle}</div>
                    <div><strong>Client Name:</strong> {hearing.clientName}</div>
                    <div><strong>Created At:</strong> {hearing.createdAt ?? 'N/A'}</div>
                </div>
            ))}

            {/* Documents */}
            <div className="mt-4 font-semibold">Documents:</div>
            {data.documents?.map(doc => (
                <div key={doc.id} className="border p-2 mb-2">
                    <div><strong>Document ID:</strong> {doc.id}</div>
                    <div><strong>Name:</strong> {doc.name}</div>
                    <div><strong>Type:</strong> {doc.type}</div>
                    <div><strong>File Size:</strong> {doc.fileSize ?? 'N/A'}</div>
                    <div><strong>Upload Date:</strong> {doc.uploadDate}</div>
                    <div><strong>Case ID:</strong> {doc.caseId}</div>
                    <div><strong>Case Title:</strong> {doc.caseTitle}</div>
                    <div><strong>Uploaded By:</strong> {doc.uploadedBy ?? 'N/A'}</div>
                </div>
            ))}

            {/* Recent History */}
            <div className="mt-4 font-semibold">Recent History:</div>
            {data.recentHistory?.map(history => (
                <div key={history.id} className="border p-2 mb-2">
                    <div><strong>History ID:</strong> {history.id}</div>
                    <div><strong>Action:</strong> {history.action}</div>
                    <div><strong>Description:</strong> {history.description}</div>
                    <div><strong>Performed By:</strong> {history.performedBy}</div>
                    <div><strong>Timestamp:</strong> {history.timestamp}</div>
                    <div><strong>Old Value:</strong> {history.oldValue ?? 'N/A'}</div>
                    <div><strong>New Value:</strong> {history.newValue ?? 'N/A'}</div>
                    <div><strong>Category:</strong> {history.category ?? 'N/A'}</div>
                </div>
            ))}

            {/* Statistics */}
            <div className="mt-4 font-semibold">Statistics:</div>
            <div>
                <strong>Total Documents:</strong> {data.statistics?.totalDocuments}
            </div>
            <div>
                <strong>Total Hearings:</strong> {data.statistics?.totalHearings}
            </div>
            <div>
                <strong>Completed Hearings:</strong> {data.statistics?.completedHearings}
            </div>
            <div>
                <strong>Upcoming Hearings:</strong> {data.statistics?.upcomingHearings}
            </div>
            <div>
                <strong>Days Open:</strong> {data.statistics?.daysOpen}
            </div>
            <div>
                <strong>Estimated Completion:</strong> {data.statistics?.estimatedCompletion ?? 'N/A'}
            </div>
            <div>
                <strong>Priority:</strong> {data.statistics?.priority}
            </div>
            <div>
                <strong>Related Cases:</strong> {data.statistics?.relatedCases}
            </div>
        </div>
    );
};

export default ViewCase;
