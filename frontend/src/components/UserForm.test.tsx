import React from 'react';
import { render, screen, fireEvent, waitFor } from '@testing-library/react'; // Removed unused 'act'
import userEvent from '@testing-library/user-event'; // Use userEvent for better simulation
import '@testing-library/jest-dom';
import UserForm from './UserForm';
import { User, CreateUserInput } from '@/types'; // Removed unused UpdateUserInput
import { format } from 'date-fns';

// Mock the necessary modules and components used within UserForm
// Mocking Shadcn UI components that might cause issues in Jest (like Popover/Calendar)
jest.mock('@/components/ui/popover', () => ({
  Popover: ({ children }: { children: React.ReactNode }) => <div>{children}</div>,
  PopoverTrigger: ({ children }: { children: React.ReactNode }) => <button>{children}</button>,
  PopoverContent: ({ children }: { children: React.ReactNode }) => <div>{children}</div>,
}));

jest.mock('@/components/ui/calendar', () => ({
  Calendar: ({ selected, onSelect }: { selected?: Date, onSelect?: (date?: Date) => void }) => (
    <input
      type="date"
      data-testid="mock-calendar"
      value={selected ? format(selected, 'yyyy-MM-dd') : ''}
      onChange={(e) => onSelect?.(e.target.value ? new Date(e.target.value + 'T00:00:00') : undefined)} // Handle date selection
    />
  ),
}));

// Mock Lucide icons
jest.mock('lucide-react', () => ({
  CalendarIcon: () => <span>Calendar</span>,
  PlusCircle: () => <span>Add</span>,
  Trash2: () => <span>Delete</span>,
}));

// Mock props
const mockOnSubmit = jest.fn().mockResolvedValue(undefined); // Mock async submit
const mockOnCancel = jest.fn();

const mockInitialUser: User = {
  id: 'user-123',
  name: 'Test User',
  email: 'test@example.com',
  dateOfBirth: '1995-08-22',
  placeOfBirth: 'Test Place',
  motherMaidenName: 'Test Maiden',
  taj: '987654321',
  taxId: '0123456789',
  addresses: [
    { id: 'addr-1', postalCode: '1234', city: 'Test City', street: 'Test St', houseNumber: '10', otherInfo: 'Info' }
  ],
  phoneNumbers: [
    { id: 'ph-1', phoneNumber: '555-1234' }
  ],
};

describe('UserForm Component', () => {
  beforeEach(() => {
    mockOnSubmit.mockClear();
    mockOnCancel.mockClear();
  });

  // Helper function to render the form
  const renderForm = (initialData: User | null = null) => {
    render(
      <UserForm
        initialData={initialData}
        onSubmit={mockOnSubmit}
        onCancel={mockOnCancel}
        isSubmitting={false}
      />
    );
  };

  test('renders correctly in create mode with default fields', () => {
    renderForm();

    expect(screen.getByLabelText(/name/i)).toBeInTheDocument();
    expect(screen.getByLabelText(/email/i)).toBeInTheDocument();
    expect(screen.getByLabelText(/date of birth/i)).toBeInTheDocument();
    expect(screen.getByLabelText(/place of birth/i)).toBeInTheDocument();
    expect(screen.getByLabelText(/mother's maiden name/i)).toBeInTheDocument();
    expect(screen.getByLabelText(/social security \(taj\)/i)).toBeInTheDocument();
    expect(screen.getByLabelText(/tax id/i)).toBeInTheDocument();

    // Check default address and phone fields exist
    expect(screen.getByLabelText(/postal code/i)).toBeInTheDocument();
    expect(screen.getByPlaceholderText(/enter phone number/i)).toBeInTheDocument();

    expect(screen.getByRole('button', { name: /create user/i })).toBeInTheDocument();
    expect(screen.getByRole('button', { name: /cancel/i })).toBeInTheDocument();
  });

  test('renders correctly in edit mode with initial data', () => {
    renderForm(mockInitialUser);

    expect(screen.getByLabelText(/name/i)).toHaveValue(mockInitialUser.name);
    expect(screen.getByLabelText(/email/i)).toHaveValue(mockInitialUser.email);
    // Check date - using the mock calendar input's value
    expect(screen.getByTestId('mock-calendar')).toHaveValue(mockInitialUser.dateOfBirth);
    expect(screen.getByLabelText(/place of birth/i)).toHaveValue(mockInitialUser.placeOfBirth);
    expect(screen.getByLabelText(/mother's maiden name/i)).toHaveValue(mockInitialUser.motherMaidenName);
    expect(screen.getByLabelText(/social security \(taj\)/i)).toHaveValue(mockInitialUser.taj);
    expect(screen.getByLabelText(/tax id/i)).toHaveValue(mockInitialUser.taxId);

    // Check initial address and phone data
    expect(screen.getByLabelText(/postal code/i)).toHaveValue(mockInitialUser.addresses[0].postalCode);
    expect(screen.getByLabelText(/city/i)).toHaveValue(mockInitialUser.addresses[0].city);
    expect(screen.getByPlaceholderText(/enter phone number/i)).toHaveValue(mockInitialUser.phoneNumbers[0].phoneNumber);

    expect(screen.getByRole('button', { name: /update user/i })).toBeInTheDocument();
  });

  test('calls onCancel when Cancel button is clicked', () => {
    renderForm();
    fireEvent.click(screen.getByRole('button', { name: /cancel/i }));
    expect(mockOnCancel).toHaveBeenCalledTimes(1);
  });

  test('adds and removes address fields dynamically', async () => {
    const user = userEvent.setup();
    renderForm();

    // Initially one address section
    expect(screen.getAllByLabelText(/postal code/i)).toHaveLength(1);

    // Add another address
    await user.click(screen.getByRole('button', { name: /add address/i }));
    expect(screen.getAllByLabelText(/postal code/i)).toHaveLength(2);

    // Remove the first address
    const addressDeleteButtons = screen.getAllByRole('button', { name: /delete/i }).filter(btn => btn.closest('.space-y-3') !== null); // Target address delete buttons
    expect(addressDeleteButtons[0]).not.toBeDisabled(); // Delete button for first address
    await user.click(addressDeleteButtons[0]);
    expect(screen.getAllByLabelText(/postal code/i)).toHaveLength(1);
    // Removed unused deleteButtons variable declaration from previous step

    // Try removing the last address - button should be disabled
     const lastDeleteButton = screen.getByRole('button', { name: /delete/i });
     expect(lastDeleteButton).toBeDisabled();

  });

   test('adds and removes phone number fields dynamically', async () => {
    const user = userEvent.setup();
    renderForm();

    // Initially one phone section
    expect(screen.getAllByPlaceholderText(/enter phone number/i)).toHaveLength(1);

    // Add another phone
    await user.click(screen.getByRole('button', { name: /add phone/i }));
    expect(screen.getAllByPlaceholderText(/enter phone number/i)).toHaveLength(2);

    // Remove the first phone
    const deleteButtons = screen.getAllByRole('button', { name: /delete/i });
    // Note: Address delete buttons also exist, need to be specific or rely on order/structure
    const phoneDeleteButtons = screen.getAllByRole('button', { name: /delete/i }).filter(btn => btn.closest('.space-y-3') === null); // Filter out address delete buttons
    expect(phoneDeleteButtons[0]).not.toBeDisabled();
    await user.click(phoneDeleteButtons[0]);
    expect(screen.getAllByPlaceholderText(/enter phone number/i)).toHaveLength(1);

     // Try removing the last phone - button should be disabled
     const lastPhoneDeleteButton = screen.getAllByRole('button', { name: /delete/i }).filter(btn => btn.closest('.space-y-3') === null)[0];
     expect(lastPhoneDeleteButton).toBeDisabled();
  });


  test('shows validation errors for required fields on submit', async () => {
    const user = userEvent.setup();
    renderForm(); // Create mode, fields initially empty

    await user.click(screen.getByRole('button', { name: /create user/i }));

    // Wait for validation errors to appear
    expect(await screen.findByText('Name is required')).toBeInTheDocument();
    expect(await screen.findByText('Invalid email address')).toBeInTheDocument(); // Empty email is invalid
    expect(await screen.findByText('Date of birth is required')).toBeInTheDocument();
    expect(await screen.findByText("Place of birth is required")).toBeInTheDocument();
    expect(await screen.findByText("Mother's maiden name is required")).toBeInTheDocument();
    expect(await screen.findByText('TAJ must be 9 digits')).toBeInTheDocument(); // Empty TAJ fails length check
    expect(await screen.findByText('Tax ID must be 10 digits')).toBeInTheDocument(); // Empty Tax ID fails length check
    // Address/Phone validation (assuming default empty fields fail validation)
    expect(await screen.findByText('Postal code is required')).toBeInTheDocument();
    expect(await screen.findByText('Phone number is required')).toBeInTheDocument();

    expect(mockOnSubmit).not.toHaveBeenCalled();
  });

   test('calls onSubmit with correct data when form is valid', async () => {
    const user = userEvent.setup();
    renderForm(); // Create mode

    // Fill the form
    await user.type(screen.getByLabelText(/name/i), 'Valid User');
    await user.type(screen.getByLabelText(/email/i), 'valid@email.com');
    // Select date using mock calendar input
    await user.type(screen.getByTestId('mock-calendar'), '2000-01-01');
    await user.type(screen.getByLabelText(/place of birth/i), 'Valid Place');
    await user.type(screen.getByLabelText(/mother's maiden name/i), 'Valid Maiden');
    await user.type(screen.getByLabelText(/social security \(taj\)/i), '123456789');
    await user.type(screen.getByLabelText(/tax id/i), '1234567890');
    // Fill default address and phone
    await user.type(screen.getByLabelText(/postal code/i), '5432');
    await user.type(screen.getByLabelText(/city/i), 'Valid City');
    await user.type(screen.getByLabelText(/street/i), 'Valid St');
    await user.type(screen.getByLabelText(/house number/i), '99');
    await user.type(screen.getByPlaceholderText(/enter phone number/i), '555-9999');

    // Submit the form
    await user.click(screen.getByRole('button', { name: /create user/i }));

    // Assertions
    await waitFor(() => {
      expect(mockOnSubmit).toHaveBeenCalledTimes(1);
    });

    // Check the data passed to onSubmit
    const expectedSubmitData: CreateUserInput = {
      name: 'Valid User',
      email: 'valid@email.com',
      dateOfBirth: '2000-01-01', // Formatted date string
      placeOfBirth: 'Valid Place',
      motherMaidenName: 'Valid Maiden',
      taj: '123456789',
      taxId: '1234567890',
      addresses: [{
        postalCode: '5432',
        city: 'Valid City',
        street: 'Valid St',
        houseNumber: '99',
        otherInfo: '', // Default empty string
        // id should be omitted for creation
      }],
      phoneNumbers: [{
        phoneNumber: '555-9999',
        // id should be omitted for creation
      }],
    };

    // Check if the argument passed to mockOnSubmit matches the expected structure
    // Need to be careful about object identity vs structure matching
    expect(mockOnSubmit).toHaveBeenCalledWith(expect.objectContaining({
        name: expectedSubmitData.name,
        email: expectedSubmitData.email,
        dateOfBirth: expectedSubmitData.dateOfBirth,
        placeOfBirth: expectedSubmitData.placeOfBirth,
        motherMaidenName: expectedSubmitData.motherMaidenName,
        taj: expectedSubmitData.taj,
        taxId: expectedSubmitData.taxId,
        addresses: expect.arrayContaining([
            expect.objectContaining(expectedSubmitData.addresses[0])
        ]),
        phoneNumbers: expect.arrayContaining([
            expect.objectContaining(expectedSubmitData.phoneNumbers[0])
        ])
    }));
    // Optionally check array lengths precisely
    expect(mockOnSubmit.mock.calls[0][0].addresses).toHaveLength(1);
    expect(mockOnSubmit.mock.calls[0][0].phoneNumbers).toHaveLength(1);

  });

});
