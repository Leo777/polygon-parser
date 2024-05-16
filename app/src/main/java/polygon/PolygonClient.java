package polygon;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import org.json.JSONArray;
import org.json.JSONObject;

public class PolygonClient {
    private HttpClient client = HttpClient.newHttpClient();

    public HttpResponse<String> getBlocks(String nodeURL, int from, int to) throws IOException, InterruptedException {
        // Create a HttpClient
        

        // URL of the JSON RPC server
        URI uri = URI.create(nodeURL);

        JSONArray batchRequest = batchRequest(from, to);

        // Build the HttpRequest
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(batchRequest.toString(), StandardCharsets.UTF_8))
                .build();

        // Send the request and receive response

        HttpResponse<String> response = client.send(request, BodyHandlers.ofString());

        return response;
    }

    // Asynchronous version
    public CompletableFuture<HttpResponse<String>> getBlocksAsync(String nodeURL, int from, int to, Executor executor) {
        HttpClient asyncClient = HttpClient.newBuilder()
                .executor(executor) // Use custom executor for HTTP client
                .build();
                
        URI uri = URI.create(nodeURL);
        JSONArray batchRequest = batchRequest(from, to);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(batchRequest.toString(), StandardCharsets.UTF_8))
                .build();
        return asyncClient.sendAsync(request, BodyHandlers.ofString());
    }

    private JSONArray batchRequest(int from, int to) {
        JSONArray batchRequest = new JSONArray();

        for (int blockId = from; blockId <= to; blockId++) {
            // Prepare JSON RPC request payload
            String hexBlockId = String.format("0x%X", blockId);;
            JSONObject json = new JSONObject();
            json.put("jsonrpc", "2.0");
            json.put("method", "eth_getBlockByNumber");
            json.put("params", new JSONArray(List.of(hexBlockId, false)));
            json.put("id", 1);

            batchRequest.put(json);

        }

        return batchRequest;
    }

}
