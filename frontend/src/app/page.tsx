"use client"; // Needed for state, effects, and event handlers

import React, { useState, useEffect, useCallback } from 'react';
import UserTable from '@/components/UserTable';
import UserForm from '@/components/UserForm';
import { Button } from "@/components/ui/button";
import {
    Dialog,
    DialogContent,
    DialogHeader,
    DialogTitle,
    DialogDescription,
    // DialogFooter removed
} from "@/components/ui/dialog";
import { User, CreateUserInput, UpdateUserInput } from '@/types';
import * as api from '@/lib/api'; // Import API functions
// Removed useToast import
import { Toaster, toast } from "sonner"; // Import toast function from sonner

export default function Home() {
    const [users, setUsers] = useState<User[]>([]);
    const [isLoading, setIsLoading] = useState<boolean>(true);
    const [error, setError] = useState<string | null>(null);
    const [isFormOpen, setIsFormOpen] = useState<boolean>(false);
    const [editingUser, setEditingUser] = useState<User | null>(null);
    const [isSubmitting, setIsSubmitting] = useState<boolean>(false);
    // Removed useToast initialization

    // Fetch users function
    const fetchUsers = useCallback(async () => {
        setIsLoading(true);
        setError(null);
        try {
            const fetchedUsers = await api.getAllUsers();
            setUsers(fetchedUsers);
        } catch (err) {
            console.error("Failed to fetch users:", err);
            setError(err instanceof Error ? err.message : "An unknown error occurred while fetching users.");
            // Use sonner toast directly
            toast.error("Error Fetching Users", {
                 description: err instanceof Error ? err.message : "Could not load user data.",
            });
        } finally {
            setIsLoading(false);
        }
    }, []); // Removed toast dependency

    // Fetch users on initial mount
    useEffect(() => {
        fetchUsers();
    }, [fetchUsers]);

    // --- Event Handlers ---

    const handleCreateNew = () => {
        setEditingUser(null); // Ensure we are in create mode
        setIsFormOpen(true);
    };

    const handleEdit = (user: User) => {
        setEditingUser(user);
        setIsFormOpen(true);
    };

    const handleDelete = async (userId: string) => {
        // Optional: Add confirmation dialog here
        if (!confirm("Are you sure you want to delete (depersonalize) this user? This action cannot be undone.")) {
            return;
        }

        try {
            await api.deleteUser(userId);
            setUsers(prevUsers => prevUsers.filter(user => user.id !== userId));
            // Use sonner toast directly
            toast.success("User Deleted", {
                 description: "User data has been depersonalized.",
            });
        } catch (err) {
            console.error("Failed to delete user:", err);
            setError(err instanceof Error ? err.message : "An unknown error occurred while deleting the user.");
             // Use sonner toast directly
             toast.error("Error Deleting User", {
                 description: err instanceof Error ? err.message : "Could not delete user.",
            });
        }
    };

    const handleFormSubmit = async (data: CreateUserInput | UpdateUserInput) => {
        setIsSubmitting(true);
        setError(null);
        try {
            let savedUser: User;
            if (editingUser) {
                // Update existing user
                savedUser = await api.updateUser(editingUser.id, data as UpdateUserInput);
                setUsers(prevUsers => prevUsers.map(user => user.id === savedUser.id ? savedUser : user));
                 // Use sonner toast directly
                 toast.success("User Updated", { description: `User ${savedUser.name} updated successfully.` });
            } else {
                // Create new user
                savedUser = await api.createUser(data as CreateUserInput);
                setUsers(prevUsers => [...prevUsers, savedUser]);
                 // Use sonner toast directly
                 toast.success("User Created", { description: `User ${savedUser.name} created successfully.` });
            }
            setIsFormOpen(false); // Close dialog on success
            setEditingUser(null); // Reset editing state
        } catch (err) {
            console.error("Failed to save user:", err);
            setError(err instanceof Error ? err.message : "An unknown error occurred while saving the user.");
             // Use sonner toast directly
             toast.error("Error Saving User", {
                 description: err instanceof Error ? err.message : "Could not save user data.",
            });
        } finally {
            setIsSubmitting(false);
        }
    };

    const handleFormCancel = () => {
        setIsFormOpen(false);
        setEditingUser(null);
    };

    // --- Rendering ---

    return (
        <main className="container mx-auto p-4 md:p-8">
            <Toaster /> {/* Add Toaster component for Shadcn Sonner */}
            <div className="flex justify-between items-center mb-6">
                <h1 className="text-2xl font-bold">Personal Data Management</h1>
                <Button onClick={handleCreateNew}>Add New User</Button>
            </div>

            {isLoading && <p>Loading users...</p>}
            {error && <p className="text-red-500">Error: {error}</p>}

            {!isLoading && !error && (
                <UserTable users={users} onEdit={handleEdit} onDelete={handleDelete} />
            )}

            {/* User Form Dialog */}
            <Dialog open={isFormOpen} onOpenChange={setIsFormOpen}>
                <DialogContent className="sm:max-w-[80vw] md:max-w-[60vw] lg:max-w-[50vw]"> {/* Adjust width */}
                    <DialogHeader>
                        <DialogTitle>{editingUser ? 'Edit User' : 'Create New User'}</DialogTitle>
                        <DialogDescription>
                            {editingUser ? 'Update the details for this user.' : 'Enter the details for the new user.'}
                        </DialogDescription>
                    </DialogHeader>
                    {/* Render form only when dialog is open to ensure correct state initialization */}
                    {isFormOpen && (
                         <div className="max-h-[70vh] overflow-y-auto p-1 pr-4"> {/* Make form scrollable */}
                            <UserForm
                                initialData={editingUser}
                                onSubmit={handleFormSubmit}
                                onCancel={handleFormCancel}
                                isSubmitting={isSubmitting}
                            />
                        </div>
                    )}
                    {/* Footer is usually outside the scrollable area, handled by Dialog component */}
                     {/* <DialogFooter>
                        // Buttons are now inside the UserForm component
                    </DialogFooter> */}
                </DialogContent>
            </Dialog>
        </main>
    );
}
