package main.structures;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.ConnectException;
import java.net.UnknownHostException;

import javax.net.ssl.SSLException;

import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HttpContext;

public class VariableNumberRetryHandler implements HttpRequestRetryHandler {
	final int maxExecutionCount;
	public VariableNumberRetryHandler(int maxExecutionCount) {
		this.maxExecutionCount = maxExecutionCount;
	}
	public boolean retryRequest(IOException exception, int executionCount, HttpContext context) {
        if (executionCount >= maxExecutionCount) {
            // Do not retry if over max retry count
            return false;
        }
        if (exception instanceof InterruptedIOException) {
            // Timeout
            return false;
        }
        if (exception instanceof UnknownHostException) {
            // Unknown host
            return false;
        }
        if (exception instanceof ConnectException) {
            // Connection refused
            return false;
        }
        if (exception instanceof SSLException) {
            // SSL handshake exception
            return false;
        }
        HttpRequest request = (HttpRequest) context.getAttribute(
                ExecutionContext.HTTP_REQUEST);
        boolean idempotent = !(request instanceof HttpEntityEnclosingRequest); 
        if (idempotent) {
            // Retry if the request is considered idempotent 
            return true;
        }
        return false;
    }
}