// $(function () {
//     let windowHeight = $(window).height();
//     let mainHeight = parseInt($('.el-main').css('height').replace("px", "")) + 60;
//     $('.el-aside').height(Math.max(windowHeight, mainHeight));
// })

//设置秘钥和秘钥偏移量
const SECRET_KEY = CryptoJS.enc.Utf8.parse("1234567890123456");
const SECRET_IV = CryptoJS.enc.Utf8.parse("1234567890123456");
/**
 * 加密方法
 * @param word
 * @returns {string}
 */
function encrypt(word) {
    let srcs = CryptoJS.enc.Utf8.parse(word);
    let encrypted = CryptoJS.AES.encrypt(srcs, SECRET_KEY, {
        iv: SECRET_IV ,
        mode: CryptoJS.mode.CBC,
        padding: CryptoJS.pad.ZeroPadding
    })
    return CryptoJS.enc.Base64.stringify(encrypted.ciphertext);
}


function decrypt(word) {
    let base64  = CryptoJS.enc.Base64.parse(word);
    let srcs = CryptoJS.enc.Base64.stringify(base64);
    const decrypt = CryptoJS.AES.decrypt(srcs, SECRET_KEY, {
        iv: SECRET_IV ,
        mode: CryptoJS.mode.CBC,
        padding: CryptoJS.pad.ZeroPadding
    });
    const decryptedStr = decrypt.toString(CryptoJS.enc.Utf8);
    return decryptedStr;
}