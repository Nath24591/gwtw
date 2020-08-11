package com.gwtw.spring.restcontroller;

import com.gwtw.spring.PaymentServer;
import com.stripe.exception.StripeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.time.LocalDateTime;

@RestController
public class StripeController {

    @Autowired
    private PaymentServer paymentServer;

    @PostMapping(value = "/create-payment-intent", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> createPaymentIntent(HttpServletRequest request) throws StripeException, IOException {

        String response = paymentServer.createPaymentIntent(request);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }


}


