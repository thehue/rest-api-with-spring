package me.wonderotter.demoinflearnrestapi.common;

import me.wonderotter.demoinflearnrestapi.events.EventController;
import me.wonderotter.demoinflearnrestapi.index.IndexController;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.validation.Errors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

public class ErrorsEntityModel extends EntityModel<Errors> {
    public static EntityModel<Errors> modelOf(Errors errors){
        EntityModel<Errors> errorsModel = EntityModel.of(errors);
        errorsModel.add(linkTo(methodOn(IndexController.class).index()).withRel("index"));

        return errorsModel;
    }
}
