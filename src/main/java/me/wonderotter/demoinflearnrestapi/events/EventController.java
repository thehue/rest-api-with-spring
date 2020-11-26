package me.wonderotter.demoinflearnrestapi.events;

import me.wonderotter.demoinflearnrestapi.common.ErrorsEntityModel;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@Controller
@RequestMapping(value = "/api/events", produces = MediaTypes.HAL_JSON_VALUE)
public class EventController {
    // bean으로 등록한것 쓰는 방법 두가지
    //1) autowired
    @Autowired
    EventRepository eventRepository;
    //2) 생성자로 등
    private final ModelMapper modelMapper;

    @Autowired
    EventValidator eventValidator;

    public EventController(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @PostMapping
    public ResponseEntity createEvent(@RequestBody @Valid EventDto eventDto, Errors errors) {
        if(errors.hasErrors()){
            return badRequest(errors);
        }

        eventValidator.validate(eventDto, errors);
        if(errors.hasErrors()){
            return badRequest(errors);
        }

        Event event = this.modelMapper.map(eventDto, Event.class);
        event.update();
        Event newEvent = eventRepository.save(event);


        WebMvcLinkBuilder selfLinkBuilder = linkTo(EventController.class).slash(newEvent.getId());

        URI createdUri = selfLinkBuilder.toUri();
        EventResource eventResource = new EventResource(event);
        eventResource.add(linkTo(EventController.class).withRel("query-events"));
        //eventResource.add(selfLinkBuilder.withSelfRel()); // 셀프 추가
        eventResource.add(linkTo(EventController.class).withRel("update-events"));
        eventResource.add(Link.of("/docs/index.html#resources-events-create").withRel("profile"));
        return ResponseEntity.created(createdUri).body(eventResource); // Location 헤더에 생성된 이벤트를 조회할 수 있는 URI 담겨 있는지 확인
    }

    @GetMapping
    public ResponseEntity queryEvents(Pageable pageable, PagedResourcesAssembler<Event> assembler){
        Page<Event> page = eventRepository.findAll(pageable);
        //PagedResourcesAssembler로 resource로 변경
        var pagedResources = assembler.toModel(page, e -> new EventResource(e));
        pagedResources.add(Link.of("/docs/index.html#resources-events-list").withRel("profile"));
        return ResponseEntity.ok().body(pagedResources);
    }

    @GetMapping("/{id}")
    public ResponseEntity getEvent(@PathVariable Integer id){
        Optional<Event> optionalEvent = eventRepository.findById(id);
        if(optionalEvent.isEmpty()){
            return ResponseEntity.notFound().build();
        }

        Event event = optionalEvent.get();
        EventResource eventResource = new EventResource(event);
        eventResource.add(Link.of("/docs/index.html#resources-events-get").withRel("profile"));

        return ResponseEntity.ok(eventResource);
    }

    @PutMapping("/{id}")
    public ResponseEntity updateEvent(@PathVariable Integer id, @RequestBody @Valid EventDto eventDto, Errors errors){
        Optional<Event> optionalEvent = eventRepository.findById(id);
        if(optionalEvent.isEmpty()){
            return ResponseEntity.notFound().build();
        }

        if(errors.hasErrors()){
            return badRequest(errors);
        }

       eventValidator.validate(eventDto, errors);
        if(errors.hasErrors()){
            return badRequest(errors);
        }

        Event existingEvent = optionalEvent.get();
        // 받아온 eventDto 데이터를 existingEvent에 덮어준다.
        modelMapper.map(eventDto, existingEvent);
        Event savedEvent = eventRepository.save(existingEvent); //transaction에 들어있는 것이 아니기 때문에 변경사항이 자동적으로 dirtychecking이 되어 데이터베이스에 반영해주는 현상이 일어나지 않는다. -> 서비스 구현해야함.
        EventResource eventResource = new EventResource(savedEvent);
        eventResource.add(Link.of("/docs/index.html#resources-events-update").withRel("profile"));

        return ResponseEntity.ok(eventResource);
    }


    private ResponseEntity badRequest(Errors errors) {
        return ResponseEntity.badRequest().body(ErrorsEntityModel.modelOf(errors));
    }
}
