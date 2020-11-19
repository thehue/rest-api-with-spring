package me.wonderotter.demoinflearnrestapi.events;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import java.time.LocalDateTime;

@Component // 빈으로 등록해서 사용
public class EventValidator {
    public void validate(EventDto eventDto, Errors errors) {
        if (eventDto.getBasePrice() > eventDto.getMaxPrice() && eventDto.getMaxPrice() != 0) {
            //errors.rejectValue("basePrice", "wrongValue", "BasePrice is wrong");
            //errors.rejectValue("maxPrice", "wrongValue", "MaxPrice is wrong");
            // global error
            errors.reject("WrongPrices", "Values for prices are wrong");
        }

        LocalDateTime endEventDateTime = eventDto.getEndEventDateTime();
        if (endEventDateTime.isBefore(eventDto.getBeginEnrollmentDateTime()) ||
                endEventDateTime.isBefore(eventDto.getBeginEventDateTime()) ||
                endEventDateTime.isBefore(eventDto.getCloseEnrollmentDateTime())) {
            errors.rejectValue("endEventDateTime", "wrongValue", "EndEventDateTime is wrong"); // field error
        }

        //TODO beginEventDateTime
        //TODO CloseEnrollmentDateTime
    }
}
