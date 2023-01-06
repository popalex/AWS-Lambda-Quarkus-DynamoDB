package airhacks.lambda.greetings;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

import airhacks.lambda.greetings.model.Fruit;
import airhacks.lambda.greetings.service.FruitSyncService;

@Path("/fruits")
public class FruitResource {

    @Inject
    FruitSyncService fruitSyncService;

    @PostConstruct
    void init() {
        fruitSyncService.add(new Fruit("Apple", "Winter fruit"));
        fruitSyncService.add(new Fruit("Pineapple", "Tropical fruit"));
    }

    @GET
    public List<Fruit> list() {
        return fruitSyncService.findAll();
    }

    @POST
    public List<Fruit> add(Fruit fruit) {
        return fruitSyncService.add(fruit);
    }

    @DELETE
    public List<Fruit> delete(Fruit fruit) {
        fruitSyncService.delete(fruit);
        return fruitSyncService.findAll();
    }
}
