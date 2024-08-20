package com.example.sumda.config.ai;

import java.util.function.Function;

public class MockWeatherService implements Function<MockWeatherService.Request, MockWeatherService.Response> {

	public enum Unit {C, F}

	public record Request(String location, Unit unit) {
	}

	public record Response(double temp, Unit unit) {
	}

	@Override
	public Response apply(Request request) {
		return new Response(30.0, Unit.C); // 간단한 응답으로 30도(섭씨)를 반환합니다.
	}
}