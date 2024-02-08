package me.sunmisc.transflow.vk;

import me.sunmisc.transflow.vk.requests.Request;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;
import java.util.stream.Stream;

public class VkWire implements Wire {
    private static final URI API_URL = URI.create(
            "https://api.vk.com/method/");
    private final String uri;
    private final HttpClient client;

    public VkWire(String uri, HttpClient client) {
        this.uri = uri;
        this.client = client;
    }


    private Response send(Request input, String method) throws Exception {
        var req = HttpRequest
                .newBuilder(API_URL.resolve(uri))
                .headers(input
                        .headers()
                        .flatMap(x -> Stream.of(x.name(), x.value()))
                        .toArray(String[]::new))
                .method(method, input
                        .stream()
                        .map(HttpRequest.BodyPublishers::ofByteArray)
                        .orElse(HttpRequest.BodyPublishers.noBody()))
                .build();
        HttpResponse<byte[]> response =
                client.send(req, HttpResponse.BodyHandlers.ofByteArray());
        return new WrapReponse(response);
    }

    @Override
    public Response post(Request request) throws Exception {
        return send(request, "POST");
    }

    @Override
    public Response patch(Request request) throws Exception {
        return send(request, "PATCH");
    }


    @Override
    public Response delete(Request request) throws Exception {
        return send(request, "DELETE");
    }

    @Override
    public Response get(Request request) throws Exception {
        return send(request, "GET");
    }

    private record WrapReponse(
            HttpResponse<byte[]> response
    ) implements Response {

        @Override
        public int status() {
            return response.statusCode();
        }

        @Override
        public Optional<byte[]> stream() {
            return Optional.of(response.body());
        }

        @Override
        public Stream<Header> headers() {
            return response
                    .headers()
                    .map()
                    .entrySet()
                    .stream()
                    .flatMap(x -> {
                        String key = x.getKey();
                        return x
                                .getValue()
                                .stream()
                                .map(r -> new Header(key, r));
                    });
        }
    }
}