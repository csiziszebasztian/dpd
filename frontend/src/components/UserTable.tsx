"use client";

import React, { useState } from 'react';
import { User } from '@/types';
import {
    Table,
    TableBody,
    TableCell,
    TableHead,
    TableHeader,
    TableRow,
} from "@/components/ui/table";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { cn } from "@/lib/utils"; // For conditional classes

interface UserTableProps {
    users: User[];
    onEdit: (user: User) => void;
    onDelete: (userId: string) => void;
}

const UserTable: React.FC<UserTableProps> = ({ users, onEdit, onDelete }) => {
    const [expandedRowId, setExpandedRowId] = useState<string | null>(null);

    if (!users || users.length === 0) {
        return <p>No users found.</p>;
    }

    const formatDate = (dateString: string | null | undefined) => {
        if (!dateString) return 'N/A';
        try {
            const date = new Date(dateString);
            if (isNaN(date.getTime())) {
                 return 'Invalid Date';
            }
            // Format to MM/DD/YYYY as shown in the image
            return date.toLocaleDateString('en-US', { month: 'numeric', day: 'numeric', year: 'numeric' });
        } catch (e) {
            console.error("Error formatting date:", dateString, e);
            return 'Invalid Date';
        }
    };

    const formatAddress = (addr: User['addresses'][0]) => {
        // Format address based on available fields, trying to resemble image structure loosely
        // Example: "1234 Test City, Main St 10 (Apt 1)"
        let formatted = `${addr.postalCode || ''} ${addr.city || ''}, ${addr.street || ''} ${addr.houseNumber || ''}`;
        if (addr.otherInfo) {
            formatted += ` (${addr.otherInfo})`;
        }
        return formatted.trim().replace(/^,|,$/g, '').replace(/,\s*,/g, ','); // Basic cleanup
    };

    const toggleRowExpansion = (userId: string) => {
        setExpandedRowId(prevId => (prevId === userId ? null : userId));
    };

    return (
        <Table>
            <TableHeader>
                <TableRow>
                    <TableHead>Name</TableHead>
                    <TableHead>Email</TableHead>
                    <TableHead>Date of Birth</TableHead>
                    {/* Removed Details Header */}
                    <TableHead className="text-right">Actions</TableHead> {/* Align Actions to right */}
                </TableRow>
            </TableHeader>
            <TableBody>
                {/* Revert to React.Fragment approach */}
                {users.map((user) => (
                    <React.Fragment key={user.id}>
                        {/* Main User Row */}
                        <TableRow
                            onClick={() => toggleRowExpansion(user.id)}
                            className={cn(
                                "cursor-pointer hover:bg-muted/50",
                                expandedRowId === user.id && "bg-muted/50" // Highlight expanded row slightly
                            )}
                        >
                            <TableCell>{user.name}</TableCell>
                            <TableCell>{user.email}</TableCell>
                            <TableCell>{formatDate(user.dateOfBirth)}</TableCell>
                            {/* Removed Details Cell */}
                            <TableCell className="text-right"> {/* Align Actions cell content */}
                                <div className="flex space-x-2 justify-end"> {/* Justify buttons to end */}
                                    {/* Stop propagation to prevent row click toggle when clicking buttons */}
                                    <Button variant="outline" size="sm" onClick={(e) => { e.stopPropagation(); onEdit(user); }}>Edit</Button>
                                    <Button variant="destructive" size="sm" onClick={(e) => { e.stopPropagation(); onDelete(user.id); }}>Delete</Button>
                                </div>
                            </TableCell>
                        </TableRow>

                        {/* Expanded Details Row (Conditional) */}
                        {/* Ensure conditional rendering is clean */}
                        {expandedRowId === user.id ? (
                            <TableRow className="bg-white dark:bg-zinc-900 hover:bg-white dark:hover:bg-zinc-900"> {/* Add hover style override */}
                                <TableCell colSpan={4}> {/* Span across all columns */}
                                    <div className="p-4 space-y-4 border-t border-border"> {/* Add top border */}
                                        {/* Basic Info Section */}
                                        <div className="grid grid-cols-1 md:grid-cols-2 gap-x-8 gap-y-2 mb-4">
                                            <p><strong>Place of Birth:</strong> {user.placeOfBirth || 'N/A'}</p>
                                            <p><strong>Mother's Maiden Name:</strong> {user.motherMaidenName || 'N/A'}</p>
                                            <p><strong>TAJ:</strong> {user.taj || 'N/A'}</p>
                                            <p><strong>Tax ID:</strong> {user.taxId || 'N/A'}</p>
                                        </div>

                                        {/* Addresses Card */}
                                        <Card>
                                            <CardHeader><CardTitle className="text-lg font-semibold">Addresses</CardTitle></CardHeader>
                                            <CardContent>
                                                {user.addresses && user.addresses.length > 0 ? (
                                                    <ul className="space-y-1">
                                                        {user.addresses.map((addr) => (
                                                            <li key={addr.id}>
                                                                {formatAddress(addr)}
                                                            </li>
                                                        ))}
                                                    </ul>
                                                ) : <p className="text-muted-foreground">No addresses listed.</p>}
                                            </CardContent>
                                        </Card>

                                        {/* Phone Numbers Card */}
                                        <Card>
                                            <CardHeader><CardTitle className="text-lg font-semibold">Phone Numbers</CardTitle></CardHeader>
                                            <CardContent>
                                                {user.phoneNumbers && user.phoneNumbers.length > 0 ? (
                                                     <ul className="space-y-1">
                                                        {user.phoneNumbers.map((phone) => (
                                                            <li key={phone.id}>+{phone.phoneNumber}</li>
                                                        ))}
                                                    </ul>
                                                ) : <p className="text-muted-foreground">No phone numbers listed.</p>}
                                            </CardContent>
                                        </Card>
                                    </div>
                                </TableCell>
                            </TableRow>
                        ) : null} {/* Explicitly return null if condition is false */}
                    </React.Fragment>
                ))}
            </TableBody>
        </Table>
    );
};

export default UserTable;
