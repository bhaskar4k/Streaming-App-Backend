/* eslint-disable no-unused-vars */
import { Environment } from "../Environment/Environment";
export class EncryptionDecryption {
    constructor(name, year) {
        this.name = name;
        this.year = year;
        this.key= Environment.encryptionKey;
    }

    // Utility Function: Rotate bits left (8-bit safe)
    rotateLeft(value, shift) {
        return ((value << shift) & 0xFF) | (value >>> (8 - shift));
    }

    // Utility Function: Rotate bits right (8-bit safe)
    rotateRight(value, shift) {
        return ((value >>> shift) | (value << (8 - shift))) & 0xFF;
    }

    // Function to generate random padding
    generateRandomPadding(length) {
        const chars = '#4j*Vx8&3H!g7$FQp^w2BzE+Rm$k6GdE9t*Lz';
        let padding = '';
        for (let i = 0; i < length; i++) {
            const randomIndex = Math.floor(Math.random() * chars.length);
            padding += chars[randomIndex];
        }
        return padding;
        }

        // Function to embed the original length into the encrypted string
        embedOriginalLength(data, originalLength) {
        // Prefix the original length as a fixed 3-digit number
        const lengthStr = originalLength.toString().padStart(3, '0');
        return lengthStr + data;
    }

    // Function to extract the original length during decryption
    extractOriginalLength(data) {
        const lengthStr = data.slice(0, 3);
        const originalLength = parseInt(lengthStr, 10);
        return { originalLength, content: data.slice(3) };
    }

    // Custom Encrypt Function
    customEncrypt(input) {
        const keyLength = this.key.length;
        let encrypted = '';

        for (let i = 0; i < input.length; i++) {
            let charCode = input.charCodeAt(i);
            let keyChar = this.key.charCodeAt(i % keyLength);

            // Step 1: XOR with key
            charCode ^= keyChar;

            // Step 2: Rotate left (8-bit safe)
            charCode = this.rotateLeft(charCode, 3);

            encrypted += String.fromCharCode(charCode);
        }

        // Embed the original length
        encrypted = this.embedOriginalLength(encrypted, input.length);

        // Pad the encrypted string to at least 50 characters
        encrypted = this.padString(encrypted, 100);

        // Return Base64-encoded result
        return btoa(encrypted);
    }

    // Function to pad the encrypted string to minimum length
    padString(input, minLength) {
        if (input.length >= minLength) return input;

        const paddingLength = minLength - input.length;
        const randomPadding = this.generateRandomPadding(paddingLength);

        return input + randomPadding;
    }

    // Custom Decrypt Function
    customDecrypt(encryptedInput) {
        const keyLength = this.key.length;

        // Base64 decode
        let encrypted = atob(encryptedInput);

        // Extract the original length
        const { originalLength, content } = this.extractOriginalLength(encrypted);

        let decrypted = '';

        for (let i = 0; i < originalLength; i++) {
            let charCode = content.charCodeAt(i);
            let keyChar = this.key.charCodeAt(i % keyLength);

            // Step 1: Reverse rotate left (rotate right)
            charCode = this.rotateRight(charCode, 3);

            // Step 2: Reverse XOR with key
            charCode ^= keyChar;

            decrypted += String.fromCharCode(charCode);
        }

        return decrypted;
    }
}

