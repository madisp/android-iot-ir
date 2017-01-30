package pink.madis.things.ir;

import com.google.android.things.pio.I2cDevice;
import com.google.android.things.pio.PeripheralManagerService;
import com.koushikdutta.async.http.server.AsyncHttpServer;
import com.koushikdutta.async.http.server.AsyncHttpServerRequest;
import com.koushikdutta.async.http.server.AsyncHttpServerResponse;
import com.koushikdutta.async.http.server.HttpServerRequestCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Closeable;
import java.io.IOException;

class Server implements Closeable {
    private final AsyncHttpServer httpServer;
    private final I2cDevice device;

    static Server create(PeripheralManagerService peripherals) throws IOException {
        Server server = new Server(peripherals);
        server.init();
        return server;
    }

    private Server(PeripheralManagerService peripherals) throws IOException {
        httpServer = new AsyncHttpServer();
        device = peripherals.openI2cDevice(peripherals.getI2cBusList().get(0), 8);
    }

    private void init() throws IOException {
        httpServer.post("/rc5", new HttpServerRequestCallback() {
            @Override
            public void onRequest(AsyncHttpServerRequest request, AsyncHttpServerResponse response) {
                try {
                    Object req = request.getBody().get();

                    if (!(req instanceof JSONObject)) {
                        throw new IllegalArgumentException("Request not a JSON object");
                    }
                    JSONObject reqJson = (JSONObject) req;

                    int addr = reqJson.getInt("address");
                    int cmd = reqJson.getInt("command");

                    System.out.println("Sending IR addr=" + addr + " cmd=" + cmd);
                    device.write(new byte[] { (byte)addr, (byte)cmd }, 2);

                    JSONObject resp = new JSONObject();
                    response.send(resp);
                } catch (JSONException | IOException e) {
                    error(response, e.getMessage());
                }
            }
        });
        httpServer.listen(5000);
    }

    private void error(AsyncHttpServerResponse response, String message) {
        JSONObject resp = new JSONObject();
        try {
            resp.put("error", message);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        response.send(resp);
    }

    @Override
    public void close() throws IOException {
        device.close();
        httpServer.stop();
    }
}
