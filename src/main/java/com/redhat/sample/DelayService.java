package com.redhat.sample;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("delay")
@Produces(MediaType.APPLICATION_JSON)
public class DelayService {
  private static final Logger LOG = LoggerFactory.getLogger(DelayService.class);

  @HEAD
  public Response setCORS() {
    return Response
        .ok()
        .header("Access-Control-Allow-Origin", "*")
        .build();
  }

  @GET
  public Response doDelay() {
    LOG.info("Received new delay request...");

    long time = System.currentTimeMillis();

    String status;
    try {
      Thread.sleep(20_000);
      LOG.info("Request complete");
      status = "Complete";
    } catch (InterruptedException e) {

      LOG.warn("Interrupted!");
      status = "Interrupted";
    }

    return Response
        .ok("\"" + status + " (" + Math.round((float) (System.currentTimeMillis() - time) / 1000) + " seconds)\"", MediaType.APPLICATION_JSON)
        .header("Access-Control-Allow-Origin", "*")
        .build();
  }
}
