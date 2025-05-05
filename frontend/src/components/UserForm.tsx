"use client";

import React, { useEffect } from 'react'; // Removed unused useState
import { useForm, useFieldArray, Controller } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import * as z from 'zod'; // Assuming Zod is installed
import { User, CreateUserInput, UpdateUserInput } from '@/types'; // Removed unused Address, PhoneNumber
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Separator } from "@/components/ui/separator";
import { Popover, PopoverContent, PopoverTrigger } from "@/components/ui/popover";
import { Calendar } from "@/components/ui/calendar";
import { CalendarIcon, PlusCircle, Trash2 } from "lucide-react";
import { format, parseISO } from 'date-fns';
import { cn } from "@/lib/utils"; // Assuming utils file exists

// Placeholder Zod schema - will need refinement based on exact validation rules
const addressSchema = z.object({
    id: z.string().uuid().optional(), // Optional for creation
    postalCode: z.string().min(1, "Postal code is required"),
    city: z.string().min(1, "City is required"),
    street: z.string().min(1, "Street is required"),
    houseNumber: z.string().min(1, "House number is required"),
    otherInfo: z.string().optional(),
});

const phoneNumberSchema = z.object({
    id: z.string().uuid().optional(), // Optional for creation
    phoneNumber: z.string().min(1, "Phone number is required"), // Add more specific validation (e.g., regex)
});

const userFormSchema = z.object({
    name: z.string().min(1, "Name is required"),
    email: z.string().email("Invalid email address"),
    dateOfBirth: z.date({ required_error: "Date of birth is required" }),
    placeOfBirth: z.string().min(1, "Place of birth is required"),
    motherMaidenName: z.string().min(1, "Mother's maiden name is required"),
    taj: z.string().length(9, "TAJ must be 9 digits"), // Basic length validation
    taxId: z.string().length(10, "Tax ID must be 10 digits"), // Basic length validation
    addresses: z.array(addressSchema).min(1, "At least one address is required"),
    phoneNumbers: z.array(phoneNumberSchema).min(1, "At least one phone number is required"),
});

// Infer the TS type from the Zod schema
type UserFormData = z.infer<typeof userFormSchema>;

interface UserFormProps {
    initialData?: User | null; // User data for editing, null/undefined for creation
    onSubmit: (data: CreateUserInput | UpdateUserInput) => Promise<void>; // Function to handle form submission
    onCancel: () => void; // Function to handle cancellation
    isSubmitting: boolean; // Flag to disable button during submission
}

const UserForm: React.FC<UserFormProps> = ({ initialData, onSubmit, onCancel, isSubmitting }) => {

    const { register, handleSubmit, control, formState: { errors }, reset } = useForm<UserFormData>({
        resolver: zodResolver(userFormSchema),
        defaultValues: initialData ? {
            ...initialData,
            dateOfBirth: initialData.dateOfBirth ? parseISO(initialData.dateOfBirth) : undefined, // Convert string to Date
            addresses: initialData.addresses || [],
            phoneNumbers: initialData.phoneNumbers || [],
        } : {
            name: '',
            email: '',
            dateOfBirth: undefined,
            placeOfBirth: '',
            motherMaidenName: '',
            taj: '',
            taxId: '',
            addresses: [{ postalCode: '', city: '', street: '', houseNumber: '', otherInfo: '' }],
            phoneNumbers: [{ phoneNumber: '' }],
        },
    });

    const { fields: addressFields, append: appendAddress, remove: removeAddress } = useFieldArray({
        control,
        name: "addresses",
    });

    const { fields: phoneFields, append: appendPhone, remove: removePhone } = useFieldArray({
        control,
        name: "phoneNumbers",
    });

    // Reset form if initialData changes (e.g., when opening edit modal)
    useEffect(() => {
        if (initialData) {
            reset({
                ...initialData,
                dateOfBirth: initialData.dateOfBirth ? parseISO(initialData.dateOfBirth) : undefined,
                addresses: initialData.addresses || [],
                phoneNumbers: initialData.phoneNumbers || [],
            });
        } else {
             reset({ // Reset to default empty state for creation
                name: '', email: '', dateOfBirth: undefined, placeOfBirth: '', motherMaidenName: '',
                taj: '', taxId: '',
                addresses: [{ postalCode: '', city: '', street: '', houseNumber: '', otherInfo: '' }],
                phoneNumbers: [{ phoneNumber: '' }],
            });
        }
    }, [initialData, reset]);


    const handleFormSubmit = (data: UserFormData) => {
        const submissionData: CreateUserInput | UpdateUserInput = {
            ...data,
            dateOfBirth: format(data.dateOfBirth, 'yyyy-MM-dd'), // Format Date back to string
            // Ensure IDs are included for update, omitted for create (handled by backend DTO structure)
            addresses: data.addresses.map(addr => ({ ...addr })),
            phoneNumbers: data.phoneNumbers.map(phone => ({ ...phone })),
        };
        onSubmit(submissionData);
    };

    return (
        <form onSubmit={handleSubmit(handleFormSubmit)} className="space-y-6">
            {/* Basic Info */}
            <Card>
                <CardHeader><CardTitle>Personal Information</CardTitle></CardHeader>
                <CardContent className="space-y-4">
                    <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                        <div>
                            <Label htmlFor="name">Name</Label>
                            <Input id="name" {...register("name")} />
                            {errors.name && <p className="text-red-500 text-sm mt-1">{errors.name.message}</p>}
                        </div>
                        <div>
                            <Label htmlFor="email">Email</Label>
                            <Input id="email" type="email" {...register("email")} />
                            {errors.email && <p className="text-red-500 text-sm mt-1">{errors.email.message}</p>}
                        </div>
                        <div>
                            <Label htmlFor="dateOfBirth">Date of Birth</Label>
                             <Controller
                                name="dateOfBirth"
                                control={control}
                                render={({ field }) => (
                                    <Popover modal>
                                        <PopoverTrigger asChild>
                                             <Button
                                                variant={"outline"}
                                                className={cn(
                                                    "w-full justify-start text-left font-normal",
                                                    !field.value && "text-muted-foreground"
                                                )}
                                            >
                                                <CalendarIcon className="mr-2 h-4 w-4" />
                                                {field.value ? format(field.value, "PPP") : <span>Pick a date</span>}
                                            </Button>
                                        </PopoverTrigger>
                                        <PopoverContent className="w-auto p-0">
                                            <Calendar
                                                mode="single"
                                                selected={field.value}
                                                onSelect={field.onChange}
                                                initialFocus
                                                // Add date range restrictions if needed
                                            />
                                        </PopoverContent>
                                    </Popover>
                                )}
                            />
                            {errors.dateOfBirth && <p className="text-red-500 text-sm mt-1">{errors.dateOfBirth.message}</p>}
                        </div>
                         <div>
                            <Label htmlFor="placeOfBirth">Place of Birth</Label>
                            <Input id="placeOfBirth" {...register("placeOfBirth")} />
                            {errors.placeOfBirth && <p className="text-red-500 text-sm mt-1">{errors.placeOfBirth.message}</p>}
                        </div>
                         <div>
                            {/* Ensuring the apostrophe is escaped */}
                            <Label htmlFor="motherMaidenName">Mother&#39;s Maiden Name</Label>
                            <Input id="motherMaidenName" {...register("motherMaidenName")} />
                            {errors.motherMaidenName && <p className="text-red-500 text-sm mt-1">{errors.motherMaidenName.message}</p>}
                        </div>
                         <div>
                            <Label htmlFor="taj">Social Security (TAJ)</Label>
                            <Input id="taj" {...register("taj")} />
                            {errors.taj && <p className="text-red-500 text-sm mt-1">{errors.taj.message}</p>}
                        </div>
                         <div>
                            <Label htmlFor="taxId">Tax ID</Label>
                            <Input id="taxId" {...register("taxId")} />
                            {errors.taxId && <p className="text-red-500 text-sm mt-1">{errors.taxId.message}</p>}
                        </div>
                    </div>
                </CardContent>
            </Card>

            <Separator />

            {/* Addresses */}
            <Card>
                <CardHeader className="flex flex-row items-center justify-between">
                    <CardTitle>Addresses</CardTitle>
                    <Button type="button" variant="outline" size="sm" onClick={() => appendAddress({ postalCode: '', city: '', street: '', houseNumber: '', otherInfo: '' })}>
                        <PlusCircle className="mr-2 h-4 w-4" /> Add Address
                    </Button>
                </CardHeader>
                <CardContent className="space-y-4">
                    {addressFields.map((field, index) => (
                        <div key={field.id} className="p-4 border rounded space-y-3 relative">
                             <Button
                                type="button"
                                variant="ghost"
                                size="sm"
                                className="absolute top-1 right-1 text-red-500 hover:text-red-700"
                                onClick={() => removeAddress(index)}
                                disabled={addressFields.length <= 1} // Prevent removing the last address
                            >
                                <Trash2 className="h-4 w-4" />
                            </Button>
                            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                                <div>
                                    <Label htmlFor={`addresses.${index}.postalCode`}>Postal Code</Label>
                                    <Input id={`addresses.${index}.postalCode`} {...register(`addresses.${index}.postalCode`)} />
                                    {errors.addresses?.[index]?.postalCode && <p className="text-red-500 text-sm mt-1">{errors.addresses?.[index]?.postalCode?.message}</p>}
                                </div>
                                <div>
                                    <Label htmlFor={`addresses.${index}.city`}>City</Label>
                                    <Input id={`addresses.${index}.city`} {...register(`addresses.${index}.city`)} />
                                     {errors.addresses?.[index]?.city && <p className="text-red-500 text-sm mt-1">{errors.addresses?.[index]?.city?.message}</p>}
                                </div>
                                <div>
                                    <Label htmlFor={`addresses.${index}.street`}>Street</Label>
                                    <Input id={`addresses.${index}.street`} {...register(`addresses.${index}.street`)} />
                                     {errors.addresses?.[index]?.street && <p className="text-red-500 text-sm mt-1">{errors.addresses?.[index]?.street?.message}</p>}
                                </div>
                                <div>
                                    <Label htmlFor={`addresses.${index}.houseNumber`}>House Number</Label>
                                    <Input id={`addresses.${index}.houseNumber`} {...register(`addresses.${index}.houseNumber`)} />
                                     {errors.addresses?.[index]?.houseNumber && <p className="text-red-500 text-sm mt-1">{errors.addresses?.[index]?.houseNumber?.message}</p>}
                                </div>
                                <div className="md:col-span-2">
                                    <Label htmlFor={`addresses.${index}.otherInfo`}>Other Info (Optional)</Label>
                                    <Input id={`addresses.${index}.otherInfo`} {...register(`addresses.${index}.otherInfo`)} />
                                </div>
                            </div>
                        </div>
                    ))}
                     {errors.addresses?.root && <p className="text-red-500 text-sm mt-1">{errors.addresses.root.message}</p>}
                     {errors.addresses?.message && <p className="text-red-500 text-sm mt-1">{errors.addresses.message}</p>}
                </CardContent>
            </Card>

             <Separator />

            {/* Phone Numbers */}
             <Card>
                <CardHeader className="flex flex-row items-center justify-between">
                    <CardTitle>Phone Numbers</CardTitle>
                    <Button type="button" variant="outline" size="sm" onClick={() => appendPhone({ phoneNumber: '' })}>
                         <PlusCircle className="mr-2 h-4 w-4" /> Add Phone
                    </Button>
                </CardHeader>
                <CardContent className="space-y-4">
                    {phoneFields.map((field, index) => (
                         <div key={field.id} className="flex items-center space-x-2 relative">
                            <div className="flex-grow">
                                <Label htmlFor={`phoneNumbers.${index}.phoneNumber`} className="sr-only">Phone Number</Label>
                                <Input id={`phoneNumbers.${index}.phoneNumber`} placeholder="Enter phone number" {...register(`phoneNumbers.${index}.phoneNumber`)} />
                            </div>
                            <Button
                                type="button"
                                variant="ghost"
                                size="sm"
                                className="text-red-500 hover:text-red-700"
                                onClick={() => removePhone(index)}
                                disabled={phoneFields.length <= 1} // Prevent removing the last phone number
                            >
                                <Trash2 className="h-4 w-4" />
                            </Button>
                             {errors.phoneNumbers?.[index]?.phoneNumber && <p className="text-red-500 text-sm mt-1 absolute -bottom-5 left-0">{errors.phoneNumbers?.[index]?.phoneNumber?.message}</p>}
                        </div>
                    ))}
                    {errors.phoneNumbers?.root && <p className="text-red-500 text-sm mt-1">{errors.phoneNumbers.root.message}</p>}
                    {errors.phoneNumbers?.message && <p className="text-red-500 text-sm mt-1">{errors.phoneNumbers.message}</p>}
                </CardContent>
            </Card>

            <Separator />

            {/* Action Buttons */}
            <div className="flex justify-end space-x-4">
                <Button type="button" variant="outline" onClick={onCancel} disabled={isSubmitting}>
                    Cancel
                </Button>
                <Button type="submit" disabled={isSubmitting}>
                    {isSubmitting ? 'Saving...' : (initialData ? 'Update User' : 'Create User')}
                </Button>
            </div>
        </form>
    );
};

export default UserForm;
