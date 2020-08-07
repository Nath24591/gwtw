$("#manual-num").on('click', function(){
    $(".number-all").removeClass('hidden');
});

$("#auto-num").on('click', function(){
    $(".number-all").addClass('hidden');
    $(".win-money").text("£" + "0.00");
});

$('.select-num').click( function() {
    let searchParams = new URLSearchParams(window.location.search);
    let idParam = searchParams.get('id');
    //Set to active/not active on click
    if($(this).hasClass('active')){
        $(this).removeClass('active');
        let object = {};
        object["ticketNumber"] = $(this).text();
        $.ajax({
            type: "PUT",
            contentType: "application/json",
            url: "/api/ticket/unreserve/"+idParam,
            dataType: 'text',
            cache: false,
            timeout: 600000,
            data: JSON.stringify(object),
            complete: function (data) {
                let baseCost = $(".base-cost").text();
                updateCost(baseCost);
            }
        });
        let domToUpdate = $('#selectedNumbers li').filter(function() {
            console.log($(this).text());
            return $(this).text() === object["ticketNumber"];
        });
        $(domToUpdate).remove();
    } else {
        let currentClass = $(this);
        let object = {};
        object["ticketNumber"] = $(this).text();
        $.ajax({
            type: "PUT",
            contentType: "application/json",
            url: "/api/ticket/"+idParam,
            dataType: 'text',
            cache: false,
            timeout: 600000,
            data: JSON.stringify(object),
            complete: function (data) {
                console.log(data.responseText);
                if(data.responseText === "reserved"){
                    let domToUpdate = $('.select-num').filter(function() {
                        console.log($(this).text());
                        return $(this).text() === object["ticketNumber"];
                    });
                    $(domToUpdate).addClass("hidden");
                    basicSwal("error", "Sorry ticket number " + object["ticketNumber"] + " has already been reserved!");
                } else {
                    console.log("updatingDom");
                    $(currentClass).addClass('active');
                    $("#selectedNumbers").append('<li><a href="#">'+object["ticketNumber"]+'</a></li>');
                    let baseCost = $(".base-cost").text();
                    updateCost(baseCost);
                }
            }
        });
    }
    return false;
});

function updateCost(baseCost) {
    let howManyActive = $('.active').length;
    let newValue = howManyActive * baseCost;
    $(".win-money").text("£" + newValue.toFixed(2));
}

function basicSwal(type, title) {
    const successSwal = swal.mixin({
        toast: true,
        position: 'top-end',
        showConfirmButton: false,
        timer: 5000
    });

    successSwal({
        type: type,
        title: title
    })
}