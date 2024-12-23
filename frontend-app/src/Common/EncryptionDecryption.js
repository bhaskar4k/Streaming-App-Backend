/* eslint-disable no-undef */
/* eslint-disable no-unused-vars */
import { Environment } from "../Environment/Environment";

export class EncryptionDecryption {
    constructor() {
        this.privateKey1=Environment.encryptionKey1;
        this.privateKey2=Environment.encryptionKey2;
        this.encryptionPadding=Environment.encryptionPadding;
        this.encryptionNumber=Environment.encryptionNumber;
    }

    Padding(password,pad_size){
        let encryptedPassword="";
        let encryptionPaddingLength=this.encryptionPadding.length, encryptionNumberLength=this.encryptionNumber.length;
        let XORedCharCode;

        for(let i=0; i<pad_size; i++){
            XORedCharCode=(password.charCodeAt(i%password.length) ^ this.encryptionPadding.charCodeAt(i%encryptionPaddingLength));

            XORedCharCode*=this.encryptionNumber[i%encryptionNumberLength];

            encryptedPassword+=(`${XORedCharCode}`+'.');
            
        }

        return encryptedPassword;
    }

    Encrypt(password) {
        let encryptedPassword="", j=0;
        let privateKeyLength1=this.privateKey1.length;
        let privateKeyLength2=this.privateKey2.length;
        let encryptionNumberLength=this.encryptionNumber.length;
        let XORedCharCode;
        
        for(let i=0; i<password.length; i++){          
            if(i%2===0) XORedCharCode=(password.charCodeAt(i) ^ this.privateKey1.charCodeAt((j++)%privateKeyLength1));
            else XORedCharCode=(password.charCodeAt(i) ^ this.privateKey2.charCodeAt((j++)%privateKeyLength2));

            XORedCharCode*=this.encryptionNumber[i%encryptionNumberLength];

            encryptedPassword+=(`${XORedCharCode}`+'.');
        }

        let pad_size=99-(2*password.length);
        encryptedPassword+=this.Padding(password,pad_size);

        for(let i=0; i<password.length; i++){
            if(i%2===0) XORedCharCode=(password.charCodeAt(i) ^ this.privateKey2.charCodeAt((j++)%privateKeyLength2));
            else XORedCharCode=(password.charCodeAt(i) ^ this.privateKey1.charCodeAt((j++)%privateKeyLength1));

            XORedCharCode*=this.encryptionNumber[i%encryptionNumberLength];

            encryptedPassword+=(`${XORedCharCode}`+'.');
        }

        let fistEncryptedCharVal = "";
        for(let i=0; i<encryptedPassword.length; i++){
            if(encryptedPassword[i]==='.') break;
            fistEncryptedCharVal+=encryptedPassword[i];
        }

        encryptedPassword+=(`${password.length*this.encryptionNumber[encryptionNumberLength-1]*parseInt(fistEncryptedCharVal)}`+'.');

        return btoa(encryptedPassword);
    }

    Decrypt(encryptedPassword) {
        encryptedPassword=atob(encryptedPassword);
        
        let decryptedPassword="";
        let encryptionNumberLength=this.encryptionNumber.length;
        let encryptedParts=encryptedPassword.split('.').filter(part => part !== "");
        let originalPasswordLength=encryptedParts[encryptedParts.length-1]/this.encryptionNumber[encryptionNumberLength-1]/encryptedParts[0];
        let privateKeyLength1=this.privateKey1.length;
        let privateKeyLength2=this.privateKey2.length;
        let j=0;

        for(let i=0; i<originalPasswordLength; i++){
            let XORedCharCode = parseInt(encryptedParts[i]);

            XORedCharCode/=this.encryptionNumber[i%encryptionNumberLength];

            if(i%2===0){
                decryptedPassword+=(String.fromCharCode(XORedCharCode ^ this.privateKey1.charCodeAt((j++)%privateKeyLength1)));
            }else{
                decryptedPassword+=(String.fromCharCode(XORedCharCode ^ this.privateKey2.charCodeAt((j++)%privateKeyLength2)));
            }
        }

        return decryptedPassword;
    }

}

