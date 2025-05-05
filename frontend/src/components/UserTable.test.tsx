import React from 'react';
import { render, screen, fireEvent } from '@testing-library/react';
import '@testing-library/jest-dom';
import UserTable from './UserTable';
import { User } from '@/types';

// Mock data
const mockUsers: User[] = [
  {
    id: '1',
    name: 'Alice Smith',
    email: 'alice@example.com',
    dateOfBirth: '1990-05-15',
    placeOfBirth: 'City A',
    motherMaidenName: 'Jones',
    taj: '111222333',
    taxId: '1111111111',
    addresses: [
      { id: 'a1', postalCode: '1000', city: 'City A', street: 'Main St', houseNumber: '1', otherInfo: 'Apt 1' }
    ],
    phoneNumbers: [
      { id: 'p1', phoneNumber: '123-456-7890' }
    ],
  },
  {
    id: '2',
    name: 'Bob Johnson',
    email: 'bob@example.com',
    dateOfBirth: '1985-10-20',
    placeOfBirth: 'City B',
    motherMaidenName: 'Williams',
    taj: '444555666',
    taxId: '2222222222',
    addresses: [],
    phoneNumbers: [],
  },
];

// Mock functions
const mockOnEdit = jest.fn();
const mockOnDelete = jest.fn();

describe('UserTable Component', () => {
  beforeEach(() => {
    // Reset mocks before each test
    mockOnEdit.mockClear();
    mockOnDelete.mockClear();
  });

  test('renders table headers correctly', () => {
    render(<UserTable users={mockUsers} onEdit={mockOnEdit} onDelete={mockOnDelete} />);
    expect(screen.getByRole('columnheader', { name: /name/i })).toBeInTheDocument();
    expect(screen.getByRole('columnheader', { name: /email/i })).toBeInTheDocument();
    expect(screen.getByRole('columnheader', { name: /date of birth/i })).toBeInTheDocument();
    expect(screen.getByRole('columnheader', { name: /details/i })).toBeInTheDocument();
    expect(screen.getByRole('columnheader', { name: /actions/i })).toBeInTheDocument();
  });

  test('renders user data correctly', () => {
    render(<UserTable users={mockUsers} onEdit={mockOnEdit} onDelete={mockOnDelete} />);
    expect(screen.getByRole('cell', { name: 'Alice Smith' })).toBeInTheDocument();
    expect(screen.getByRole('cell', { name: 'alice@example.com' })).toBeInTheDocument();
    expect(screen.getByRole('cell', { name: 'Bob Johnson' })).toBeInTheDocument();
    expect(screen.getByRole('cell', { name: 'bob@example.com' })).toBeInTheDocument();
    // Check formatted date (adjust format based on implementation if needed)
    expect(screen.getByText(new Date('1990-05-15').toLocaleDateString())).toBeInTheDocument();
  });

  test('renders "No users found" when users array is empty', () => {
    render(<UserTable users={[]} onEdit={mockOnEdit} onDelete={mockOnDelete} />);
    expect(screen.getByText('No users found.')).toBeInTheDocument();
  });

  test('calls onEdit with correct user when Edit button is clicked', () => {
    render(<UserTable users={mockUsers} onEdit={mockOnEdit} onDelete={mockOnDelete} />);
    const editButtons = screen.getAllByRole('button', { name: /edit/i });
    fireEvent.click(editButtons[0]); // Click Edit for Alice
    expect(mockOnEdit).toHaveBeenCalledTimes(1);
    expect(mockOnEdit).toHaveBeenCalledWith(mockUsers[0]);
  });

  test('calls onDelete with correct user ID when Delete button is clicked', () => {
    render(<UserTable users={mockUsers} onEdit={mockOnEdit} onDelete={mockOnDelete} />);
    const deleteButtons = screen.getAllByRole('button', { name: /delete/i });
    fireEvent.click(deleteButtons[1]); // Click Delete for Bob
    expect(mockOnDelete).toHaveBeenCalledTimes(1);
    expect(mockOnDelete).toHaveBeenCalledWith(mockUsers[1].id);
  });

  test('expands accordion to show more details', () => {
    render(<UserTable users={mockUsers} onEdit={mockOnEdit} onDelete={mockOnDelete} />);
    const viewMoreTriggers = screen.getAllByRole('button', { name: /view more/i });

    // Initially, details should not be visible
    expect(screen.queryByText(/Place of Birth:/i)).not.toBeVisible();
    expect(screen.queryByText(/Main St 1/i)).not.toBeVisible(); // Check address detail

    // Click to expand Alice's details
    fireEvent.click(viewMoreTriggers[0]);

    // Now details should be visible (allow for async rendering if needed)
    expect(screen.getByText(/Place of Birth:/i)).toBeVisible();
    expect(screen.getByText(/City A/i)).toBeVisible(); // Check place of birth value
    expect(screen.getByText(/Jones/i)).toBeVisible(); // Check mother's name value
    expect(screen.getByText(/111222333/i)).toBeVisible(); // Check TAJ
    expect(screen.getByText(/1111111111/i)).toBeVisible(); // Check Tax ID
    expect(screen.getByText(/Main St 1, 1000 City A \(Apt 1\)/i)).toBeVisible(); // Check full address string
    expect(screen.getByText(/123-456-7890/i)).toBeVisible(); // Check phone number
  });

   test('shows "No addresses listed" when addresses array is empty', () => {
    render(<UserTable users={mockUsers} onEdit={mockOnEdit} onDelete={mockOnDelete} />);
    const viewMoreTriggers = screen.getAllByRole('button', { name: /view more/i });

    // Expand Bob's details (who has no addresses/phones)
    fireEvent.click(viewMoreTriggers[1]);

    expect(screen.getByText('No addresses listed.')).toBeVisible();
    expect(screen.getByText('No phone numbers listed.')).toBeVisible();
  });

});
