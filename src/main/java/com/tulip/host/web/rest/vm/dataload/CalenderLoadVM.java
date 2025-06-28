package com.tulip.host.web.rest.vm.dataload;

import com.tulip.host.enums.TypeEnum;
import io.github.rushuat.ocell.annotation.FieldName;
import lombok.Data;

import java.time.LocalDate;
import java.util.Date;

@Data
public class CalenderLoadVM {

    @FieldName("Name")
    private String name;

    @FieldName("Description")
    private String description;

    @FieldName("Type")
    private String eventType;

    @FieldName("Start Date")
    private Date startDate;

    @FieldName("End Date")
    private Date endDate;

    @FieldName("pattern")
    private String recurring_pattern;

    @FieldName("Organizer")
    private String organizer;

//    @FieldName("session")
//    private String session;


}
