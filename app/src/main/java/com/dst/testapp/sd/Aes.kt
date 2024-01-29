package com.dst.testapp.sd

 object Aes {

     fun decryptCbc(encrypted: ImmutableByteArray, key: ImmutableByteArray,
                    iv: ImmutableByteArray = ImmutableByteArray.empty(16)): ImmutableByteArray {
         require(encrypted.size % 16 == 0) { "Input length must be a multiple of 16 bytes" }

         val decrypted = ImmutableByteArray(encrypted.size)

         var previousBlock = iv

         for (i in 0 until encrypted.size step 16) {
             val currentBlock = decryptBlock(encrypted.sliceOffLen(i, 16), key)
             val decryptedBlock = currentBlock xor previousBlock
            // decrypted.copyInto(decryptedBlock, i)
             previousBlock = encrypted.sliceOffLen(i, 16)
         }

         return decrypted
     }

     // Assuming you have a decryptBlock function
     fun decryptBlock(block: ImmutableByteArray, key: ImmutableByteArray): ImmutableByteArray {
         // Implement your decryption logic here
         // This is just a placeholder, you should use a proper decryption algorithm
         // (e.g., AES) and library.
         return block
     }


     fun encryptCbc(decrypted: ImmutableByteArray, key: ImmutableByteArray,
                    iv: ImmutableByteArray = ImmutableByteArray.empty(16)): ImmutableByteArray {
         require(decrypted.size % 16 == 0) { "Input length must be a multiple of 16 bytes" }

         val encrypted = ImmutableByteArray(decrypted.size)

         var previousBlock = iv

         for (i in 0 until decrypted.size step 16) {
             val currentBlock = decrypted.sliceOffLen(i, 16) xor previousBlock
             val encryptedBlock = encryptBlock(currentBlock, key)
            // encrypted.copyIntod(encryptedBlock, i)
             previousBlock = encryptedBlock
         }

         return encrypted
     }

     // Assuming you have an encryptBlock function
     fun encryptBlock(block: ImmutableByteArray, key: ImmutableByteArray): ImmutableByteArray {
         // Implement your encryption logic here
         // This is just a placeholder, you should use a proper encryption algorithm
         // (e.g., AES) and library.
         return block
     }
 }