package io.ogi.boid;

import io.helidon.webserver.*;
import io.ogi.boid.boidconfig.BoidSimulationConfig;
import io.ogi.boid.model.BoidModel;

import java.util.logging.Logger;

public class BoidService  implements Service {

    private static final Logger LOGGER = Logger.getLogger(BoidService.class.getName());
    private final BoidSimulationConfig boidSimulationConfig;

    public BoidService(BoidSimulationConfig boidSimulationConfig) {
        this.boidSimulationConfig = boidSimulationConfig;
    }

    @Override
    public void update(Routing.Rules rules) {
        rules.get("/", this::getCurrentBoidParams);
        rules.post("/", Handler.create(BoidModel.class, this::startWithNewBoidParams));
    }

    private void getCurrentBoidParams(ServerRequest serverRequest, ServerResponse serverResponse) {
        LOGGER.info("getCurrentBoidParams");
        boidSimulationConfig.getParams()
                .thenAccept(serverResponse::send)
                .exceptionally(serverResponse::send);
    }

    private void startWithNewBoidParams(ServerRequest serverRequest, ServerResponse serverResponse, BoidModel boidModel) {
        LOGGER.info("startWithNewBoidParams");

        this.boidSimulationConfig.modifyBoidSimulation(boidModel)
                .thenAccept(r -> serverResponse.status(201).send())
                .exceptionally(serverResponse::send);
    }

}
