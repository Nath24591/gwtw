function getBase64(file) {
    let reader = new FileReader();
    reader.readAsDataURL(file);
    reader.onload = function () {
        $("#base64").val(reader.result);
    };
    reader.onerror = function (error) {
        console.log('Error: ', error);
    };
}

function checkImageSize(file) {
    let img = new Image();
        img.onload = function() {

            if(this.width > 140 || this.height > 140) {
                alert("Image too big!");
            }
        };
        img.onerror = function() {
            alert( "not a valid file: " + file.type);
        };
        img.src = _URL.createObjectURL(file);

}

let _URL = window.URL || window.webkitURL;

$("#file").change(function(e) {
    let file;

    if ((file = this.files[0])) {
       checkImageSize(file);
       getBase64(file);
    }

});

