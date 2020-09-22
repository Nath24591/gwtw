package com.gwtw.spring;

import java.io.IOException;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.gwtw.spring.DTO.PaymentIntentDto;
import com.gwtw.spring.domain.Competition;
import com.gwtw.spring.repository.CompetitionRepository;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

@Service
public class PaymentServer {
    @Autowired
    CompetitionRepository competitionRepository;


    private static Gson gson = new Gson();
    static class CreatePayment {
        @SerializedName("items")
        Object[] items;
        public Object[] getItems() {
            return items;
        }
    }
    static class CreatePaymentResponse {
        private String clientSecret;
        public CreatePaymentResponse(String clientSecret) {
            this.clientSecret = clientSecret;
        }
    }
    private int calculateOrderAmount(Object[] items) {

        String compId = "";
        int amount = items.length;


        for (Object o: items) {
            PaymentIntentDto item = gson.fromJson((String) o, PaymentIntentDto.class);
            compId = item.getId();
        }
        Long compIdInt = Long.valueOf(compId);
        Competition competition = competitionRepository.getCompetitionById(compIdInt);
        double paymentPrice = Double.valueOf(competition.getPrice()) * 100;


        // Replace this constant with a calculation of the order's amount
        // Calculate the order total on the server to prevent
        // users from directly manipulating the amount on the client
        return (int)paymentPrice * amount;
    }

    public String createPaymentIntent(HttpServletRequest request) throws StripeException, IOException {
        Stripe.apiKey = "sk_test_51HEZBIHmruxLRvNCdxF1LgOalemnx9QscmmZvuWBB3MpZKYFQem3Sui2gmqGLyqr0chc4sY8tTYIbtsh2BydKtgs004DnlbCw5";
        String test = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
        CreatePayment postBody = gson.fromJson(test, CreatePayment.class);
        PaymentIntentCreateParams createParams = new PaymentIntentCreateParams.Builder()
                .setCurrency("gbp")
                .setAmount((long) calculateOrderAmount(postBody.getItems()))
                .build();
        // Create a PaymentIntent with the order amount and currency
        PaymentIntent intent = PaymentIntent.create(createParams);
        CreatePaymentResponse paymentResponse = new CreatePaymentResponse(intent.getClientSecret());
        return gson.toJson(paymentResponse);
    }
}
