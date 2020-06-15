package br.com.fiap.servless.handler;

import br.com.fiap.servless.dao.TripRepository;
import br.com.fiap.servless.model.HandlerRequest;
import br.com.fiap.servless.model.HandlerResponse;
import br.com.fiap.servless.model.Trip;

import java.io.IOException;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CreateTrip implements RequestHandler<HandlerRequest, HandlerResponse>{

	private final TripRepository repository = new TripRepository();

	@Override
	public HandlerResponse handleRequest(HandlerRequest input, Context context) {
		Trip newTrip = null;

		try {
			newTrip = new ObjectMapper().readValue(input.getBody(), Trip.class);
		} catch (IOException e) {
			return HandlerResponse.builder().setStatusCode(400).setRawBody("There is a error in your Trip info.").build();
		}
		context.getLogger().log("Creating a new Trip record for Country: " + newTrip.getCountry() + " - City: " + newTrip.getCity());
		final Trip recorded = repository.save(newTrip);
		return HandlerResponse.builder().setStatusCode(201).setObjectBody(recorded).build();
	}
}
