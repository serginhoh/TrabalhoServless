package br.com.fiap.servless.handler;

import br.com.fiap.servless.dao.TripRepository;
import br.com.fiap.servless.model.HandlerRequest;
import br.com.fiap.servless.model.HandlerResponse;
import br.com.fiap.servless.model.Trip;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import java.util.ArrayList;
import java.util.List;

public class GetTripsByPeriod implements RequestHandler<HandlerRequest, HandlerResponse> {
    private final TripRepository tripRepository = new TripRepository();

    @SuppressWarnings("serial")
	@Override
	public HandlerResponse handleRequest(HandlerRequest input, Context context) {
        final String startDate = input.getQueryStringParameters().get("starts");
        final String endDate = input.getQueryStringParameters().get("ends");

        context.getLogger().log("Searching for trips between " + startDate + " and " + endDate);
        List<Trip> result = this.tripRepository.findByPeriod(startDate, endDate);

        if (result == null || result.isEmpty()) {
            result = new ArrayList<Trip>() {{ add(new Trip());}} ;
        }

        return HandlerResponse.builder().setStatusCode(200).setObjectBody(result).build();
    }
}
