/**
 * sm4加密工具类
 */

// https://www.npmjs.com/package/gm-crypt
const SM4 = require('gm-crypt').sm4

export const sm4 = new SM4({
//   // encrypt/decypt main key; cannot be omitted
  key: process.env.VUE_APP_ENCRYPT_KEY,
//   // optional; can be 'cbc' or 'ecb'
  mode: 'ecb',
//   // optional; when use cbc mode, it's necessary
//   iv: null, // default is null
//   // optional: this is the cipher data's type; Can be 'base64' or 'text'
  cipherType: 'base64'
})


// let plaintext = '中国国密加解密算法'
// let ciphertext = sm4.encrypt(plaintext)
// console.log('ciphertext', ciphertext)
//
// let plaintext222 = sm4.decrypt(ciphertext)
// console.log('plaintext222', plaintext222)

