import { User, CreateUserInput, UpdateUserInput } from '@/types';

// Ensure the API URL is correctly configured in environment variables
// In Next.js, public env vars need to be prefixed with NEXT_PUBLIC_
const API_BASE_URL = process.env.NEXT_PUBLIC_API_URL;

if (!API_BASE_URL) {
    console.error("Error: NEXT_PUBLIC_API_URL environment variable is not set.");
    // Potentially throw an error or provide a default for local dev if appropriate,
    // but failing loudly is often better for configuration issues.
}

const handleResponse = async <T>(response: Response): Promise<T> => {
    if (!response.ok) {
        const errorData = await response.text(); // Try to get error details
        console.error("API Error:", response.status, errorData);
        throw new Error(`API request failed with status ${response.status}: ${errorData || response.statusText}`);
    }
    // Handle cases where the response might be empty (e.g., 204 No Content for DELETE)
    if (response.status === 204) {
        return null as T; // Or handle as appropriate for the call site
    }
    return response.json() as Promise<T>;
};

export const getAllUsers = async (): Promise<User[]> => {
    const response = await fetch(`${API_BASE_URL}/users`);
    return handleResponse<User[]>(response);
};

export const getUserById = async (id: string): Promise<User> => {
    const response = await fetch(`${API_BASE_URL}/users/${id}`);
    return handleResponse<User>(response);
};

export const createUser = async (userData: CreateUserInput): Promise<User> => {
    const response = await fetch(`${API_BASE_URL}/users`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(userData),
    });
    return handleResponse<User>(response);
};

export const updateUser = async (id: string, userData: UpdateUserInput): Promise<User> => {
    const response = await fetch(`${API_BASE_URL}/users/${id}`, {
        // Using PATCH for partial updates as defined in UserController
        method: 'PATCH',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(userData),
    });
    return handleResponse<User>(response);
};

export const deleteUser = async (id: string): Promise<void> => {
    const response = await fetch(`${API_BASE_URL}/users/${id}`, {
        method: 'DELETE',
    });
    // Expecting 204 No Content, handleResponse will return null
    await handleResponse<void>(response);
};
