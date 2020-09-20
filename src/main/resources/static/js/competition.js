let searchParams = new URLSearchParams(window.location.search);
let idParam = searchParams.get('id');
$(".answerClick").on('click', function() {
    if($("input[name=answer]:checked").val() === "false") {
        swal('Incorrect Answer', 'Please Try Again!', 'warning');
    } else {
        setTimeout(function () {
            $(".number-serial").addClass('disabled');
            $(".buyText").addClass('hidden');
            $(".hideButton").addClass('hidden');
            $(".stripeDeviceSizing").removeClass('hidden');


            let items = [];
            $('#selectedNumbers li').each(function () {
                items.push("{id:" + idParam + ", ticket:" + $(this).text() + "}");
            });

            let purchaseObject = {
                items: items
            };

// A reference to Stripe.js initialized with your real test publishable API key.
            var stripe = Stripe("pk_test_51HEZBIHmruxLRvNC6e3b1flQJYEXSqHT0k8J0WpXOZ3sZ2a1dhqw1lYYg2C709GVBG1A7An8eCWlCx0BJNynBeNF00T0ykFsvS");
// Disable the button until we have Stripe set up on the page
            document.querySelector("button").disabled = true;
            fetch("/create-payment-intent", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify(purchaseObject)
            })
                .then(function (result) {
                    return result.json();
                })
                .then(function (data) {
                    var elements = stripe.elements();
                    var style = {
                        base: {
                            color: "#32325d",
                            fontFamily: 'Arial, sans-serif',
                            fontSmoothing: "antialiased",
                            fontSize: "16px",
                            "::placeholder": {
                                color: "#32325d"
                            }
                        },
                        invalid: {
                            fontFamily: 'Arial, sans-serif',
                            color: "#fa755a",
                            iconColor: "#fa755a"
                        }
                    };
                    var card = elements.create("card", {style: style});
                    // Stripe injects an iframe into the DOM
                    card.mount("#card-element");
                    card.on("change", function (event) {
                        // Disable the Pay button if there are no card details in the Element
                        document.querySelector("button").disabled = event.empty;
                        document.querySelector("#card-error").textContent = event.error ? event.error.message : "";
                    });
                    var form = document.getElementById("payment-form");
                    form.addEventListener("submit", function (event) {
                        event.preventDefault();
                        // Complete payment when the submit button is clicked
                        payWithCard(stripe, card, data.clientSecret);
                    });
                });
// Calls stripe.confirmCardPayment
// If the card requires authentication Stripe shows a pop-up modal to
// prompt the user to enter authentication details without leaving your page.
            var payWithCard = function (stripe, card, clientSecret) {
                loading(true);
                stripe
                    .confirmCardPayment(clientSecret, {
                        payment_method: {
                            card: card
                        }
                    })
                    .then(function (result) {
                        if (result.error) {
                            // Show error to your customer
                            showError(result.error.message);
                        } else {
                            // The payment succeeded!
                            let items = [];
                            $('#selectedNumbers li').each(function () {
                                items.push("{id:" + idParam + ", ticket:" + $(this).text() + "}");
                            });

                            let purchaseObject = {
                                items: items
                            };

                            let tempTickets = [];
                            $('#selectedNumbers li').filter(function() {
                                tempTickets.push($(this).text());
                            });
                            let object = {};
                            object["tickets"] = tempTickets;
                            object["email"] = $("#loggedInEmail").val();
                            $.ajax({
                                type: "PUT",
                                contentType: "application/json",
                                url: "/api/ticket/purchaseTickets/"+idParam,
                                dataType: 'text',
                                cache: false,
                                timeout: 600000,
                                data: JSON.stringify(object),
                                complete: function (data) {
                                    let baseCost = $(".base-cost").text();
                                    updateCost(baseCost);
                                }
                            });
                            orderComplete(result.paymentIntent.id);
                        }
                    });
            };
            /* ------- UI helpers ------- */
// Shows a success message when the payment is complete
            let orderComplete = function (paymentIntentId) {
                loading(false);
                document
                    .querySelector(".result-message a");
                document.querySelector(".result-message").classList.remove("hidden");
                $("#submit").attr("disabled", true);
                document.querySelector("button").disabled = true;
            };
// Show the customer the error from Stripe if their card fails to charge
            var showError = function (errorMsgText) {
                loading(false);
                var errorMsg = document.querySelector("#card-error");
                errorMsg.textContent = errorMsgText;
                setTimeout(function () {
                    errorMsg.textContent = "";
                }, 4000);
            };
// Show a spinner on payment submission
            var loading = function (isLoading) {
                if (isLoading) {
                    // Disable the button and show a spinner
                    document.querySelector("button").disabled = true;
                    document.querySelector("#spinner").classList.remove("hidden");
                    document.querySelector("#button-text").classList.add("hidden");
                } else {
                    document.querySelector("button").disabled = false;
                    document.querySelector("#spinner").classList.add("hidden");
                    document.querySelector("#button-text").classList.remove("hidden");
                }
            };
            return false;
        }, 250);
    }
});
$('.select-num').click( function() {
    if($("#loggedIn").length === 0){
        basicSwal("error", "Please log in or create an account before selecting tickets.");
        return false;
    }

    //Set to active/not active on click
    if($($(this).parent().parent()[0]).hasClass("disabled")){
        basicSwal("error", "Your order has been locked in, please refresh you page if you want to select different tickets");
        return false;
    }
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
                if(data.responseText === "reserved"){
                    let domToUpdate = $('.select-num').filter(function() {
                        return $(this).text() === object["ticketNumber"];
                    });
                    $(domToUpdate).addClass("hidden");
                    basicSwal("error", "Sorry ticket number " + object["ticketNumber"] + " has already been reserved!");
                } else {
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

function check_navigation_display(el) {
    //accepts a jQuery object of the containing div as a parameter
    if ($(el).find('ul').children('li').first().is(':visible')) {
        $(el).children('.prev').hide();
    } else {
        $(el).children('.prev').show();
    }

    if ($(el).find('ul').children('li').last().is(':visible')) {
        $(el).children('.next').hide();
    } else {
        $(el).children('.next').show();
    }
}

$('.number-all').each(function () {
    $(this).append('<a class="prev">prev</a> | <a class="next">next</a>');
    $(this).find('ul li:gt(29)').hide();

    check_navigation_display($(this));

    $(this).find('.next').click(function () {
        var last = $(this).siblings('ul').children('li:visible:last');
        last.nextAll(':lt(30)').show();
        last.next().prevAll().hide();
        check_navigation_display($(this).closest('div'));
    });

    $(this).find('.prev').click(function () {
        var first = $(this).siblings('ul').children('li:visible:first');
        first.prevAll(':lt(30)').show();
        first.prev().nextAll().hide();
        check_navigation_display($(this).closest('div'));
    });
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