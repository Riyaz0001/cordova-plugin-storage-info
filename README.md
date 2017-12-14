# Cordova Android external SD card path plugin

Plugin that returns the Info of internal & external Storage in Android.

## Using
    
Install the plugin

    $ cordova plugin add https://github.com/teamSolutionAnalysts/ionic-plugin-ExternalSdCardPath.git
    


```js  
    function getStorageInfo() {
        var success = function(message) {
            var path = JSON.parse(message);
            var intStorage = path.Storage.external;
            var extStorage = path.Storage.external;
            
            console.log("Internal Path: " + intStorage + 
                        "External Path: " + extStorage
                        );
        }

        var failure = function() {
            alert("Error calling external Plugin");
        }
        
        // get Android Internal & External Storage Path.
        StorageInfo.getPath("microSD", success, failure);
        
        // get Used Space, Free Space, Total Space in Android Internal & External Storage. 
        StorageInfo.getSpace("microSD", function(res) {
            alert(JSON.stringify(res));
            
        }, function(e) {
            alert(JSON.stringify(e));

        });
    }
```
    
   You can access path of external and internal by following this way:
   
    1) path.SDCard.external 
    2) path.SDCard.internal 

Install Android platform

    cordova platform add android
    

## More Info

For more information on setting up Cordova see [the documentation](http://cordova.apache.org/docs/en/latest/guide/cli/index.html)

For more info on plugins see the [Plugin Development Guide](http://cordova.apache.org/docs/en/latest/guide/hybrid/plugins/index.html)

