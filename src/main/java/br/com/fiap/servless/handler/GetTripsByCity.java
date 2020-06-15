package br.com.fiap.servless.handler;

import br.com.fiap.servless.dao.TripRepository;
import br.com.fiap.servless.model.HandlerRequest;
import br.com.fiap.servless.model.HandlerResponse;
import br.com.fiap.servless.model.Trip;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import java.util.List;

public class GetTripsByCity implements RequestHandler<HandlerRequest, HandlerResponse> {
    private final TripRepository tripRepository = new TripRepository();

    @Override
    public HandlerResponse handleRequest(HandlerRequest input, Context context) {
        final String country = input.getPathParameters().get("country");
        final String city = input.getQueryStringParameters().get("city");

        context.getLogger().log("Searching for trips to " + country + " and city " + city);
        final List<Trip> result = this.tripRepository.findByCity(country, city);

        if (result == null || result.isEmpty()) {
            return HandlerResponse.builder().setStatusCode(404).build();
        }

        return HandlerResponse.builder().setStatusCode(200).setObjectBody(result).build();
    }
}
