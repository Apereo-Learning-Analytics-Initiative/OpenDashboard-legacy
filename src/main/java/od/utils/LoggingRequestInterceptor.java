package od.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.FileCopyUtils;

public class LoggingRequestInterceptor implements ClientHttpRequestInterceptor {

	private static final Logger log = LoggerFactory.getLogger(LoggingRequestInterceptor.class);

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {

    	//traceRequest(request,body);
        ClientHttpResponse response = execution.execute(request, body);
        //traceResponse(request,body,response);

        return response;
    }

    private void traceRequest(HttpRequest request, byte[] body) throws IOException {
    	log.debug("===========================request begin================================================");

    	log.debug("URI : " + request.getURI());
    	log.debug("Method : " + request.getMethod());
    	log.debug("Request Body : " + new String(body, "UTF-8"));
    	log.debug("==========================request end================================================");
    }

    private void traceResponse(HttpRequest request, byte[] body, ClientHttpResponse response) throws IOException {
    	
        String s = new String(body); 
        log.debug(request.getURI().toString()+" - "+s); 
    	
        byte[] b = FileCopyUtils.copyToByteArray(response.getBody()); 
        s = new String(b); 
        log.debug(request.getURI().toString()+" - "+s);
    }
}
