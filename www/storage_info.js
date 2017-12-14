/*global cordova, module*/
var exec = require('cordova/exec');
module.exports = {
    // get Storage Path
    getPath: function (name, successCallback, errorCallback) {
        exec(successCallback, errorCallback, "StorageInfo", "getPath", [name]);
    },
    // get Storage Space
    getSpace: function (name, successCallback, errorCallback) {
        exec(successCallback, errorCallback, "StorageInfo", "getSpace", [name]);

    }
};