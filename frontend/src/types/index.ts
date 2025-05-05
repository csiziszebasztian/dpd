// Corresponds to backend AddressDTO
export interface Address {
  id: string; // UUID is represented as string in JSON
  postalCode: string;
  city: string;
  street: string;
  houseNumber: string;
  otherInfo?: string; // Optional field
}

// Corresponds to backend PhoneNumberDTO
export interface PhoneNumber {
  id: string; // UUID is represented as string in JSON
  phoneNumber: string;
}

// Corresponds to backend UserDTO
export interface User {
  id: string; // UUID is represented as string in JSON
  name: string;
  email: string;
  dateOfBirth: string; // LocalDate is typically represented as string (e.g., "YYYY-MM-DD")
  placeOfBirth: string;
  motherMaidenName: string;
  taj: string;
  taxId: string;
  addresses: Address[];
  phoneNumbers: PhoneNumber[];
}

// Corresponds to backend CreateUserDTO (adjust if needed, might be slightly different)
// Often similar to User but without 'id' and potentially different structure for nested items
export interface CreateUserInput {
    name: string;
    email: string;
    dateOfBirth: string;
    placeOfBirth: string;
    motherMaidenName: string;
    taj: string;
    taxId: string;
    addresses: Omit<Address, 'id'>[]; // Addresses without IDs for creation
    phoneNumbers: Omit<PhoneNumber, 'id'>[]; // Phone numbers without IDs for creation
}

// Corresponds to backend UpdateUserDTO (adjust if needed)
// Often uses partial types and includes the ID
export interface UpdateUserInput extends Partial<Omit<CreateUserInput, 'addresses' | 'phoneNumbers'>> {
    // Allow partial updates for top-level fields
    addresses?: Address[]; // Allow sending full address objects potentially with IDs for updates
    phoneNumbers?: PhoneNumber[]; // Allow sending full phone objects potentially with IDs for updates
}
