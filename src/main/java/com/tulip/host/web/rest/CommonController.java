package com.tulip.host.web.rest;

import com.tulip.host.data.DropDownOptionsDto;
import com.tulip.host.data.SessionDTO;
import com.tulip.host.enums.BloodGroupEnum;
import com.tulip.host.enums.DocumentCategoryEnum;
import com.tulip.host.enums.ExpenseTypeEnum;
import com.tulip.host.enums.GenderEnum;
import com.tulip.host.enums.PaymentOptionEnum;
import com.tulip.host.enums.RelationEnum;
import com.tulip.host.enums.ReligionEnum;
import com.tulip.host.enums.StdEnum;
import com.tulip.host.enums.TypeEnum;
import com.tulip.host.enums.UploadTypeEnum;
import com.tulip.host.service.FinancialYearService;
import com.tulip.host.service.SessionService;
import com.tulip.host.utils.CommonUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/common")
@RequiredArgsConstructor
public class CommonController {

    private final FinancialYearService financialYearService;

    private final SessionService sessionService;

    @RequestMapping("/genderList")
    public List<DropDownOptionsDto> genderList() {
        return Arrays
            .stream(GenderEnum.values())
            .map(item -> DropDownOptionsDto.builder()
                .label(item.getDisplayType())
                .value(item.name())
                .build())
            .collect(Collectors.toList());
    }

    @RequestMapping("/bloodGroupList")
    public List<DropDownOptionsDto> bloodGroupList() {
        return Arrays
            .stream(BloodGroupEnum.values())
            .map(item -> DropDownOptionsDto.builder()
                .label(item.getDisplayType())
                .value(item.name())
                .build())
            .collect(Collectors.toList());
    }

    @RequestMapping("/eventType")
    public List<DropDownOptionsDto> eventTypeList() {
        return Arrays
            .stream(TypeEnum.values())
            .map(item -> DropDownOptionsDto.builder()
                .label(item.name())
                .value(item.getColor())
                .build())
            .collect(Collectors.toList());
    }

    @RequestMapping("/relationList")
    public List<DropDownOptionsDto> dependentRelationList() {
        return Arrays
            .stream(RelationEnum.values())
            .map(item -> DropDownOptionsDto.builder()
                .label(item.name())
                .value(item.name())
                .build())
            .collect(Collectors.toList());
    }

    @RequestMapping("/religionList")
    public List<DropDownOptionsDto> religionList() {
        return Arrays
            .stream(ReligionEnum.values())
            .map(item -> DropDownOptionsDto.builder()
                .label(item.name())
                .value(item.name())
                .build())
            .collect(Collectors.toList());
    }

    @RequestMapping("/userRoleList")
    public List<DropDownOptionsDto> userRoleList() {
        return CommonUtils
            .findEligibleUG()
            .stream()
            .map(item -> DropDownOptionsDto.builder()
                .label(item.name())
                .value(item.name())
                .build())
            .collect(Collectors.toList());
    }

    @RequestMapping("/classList")
    public List<DropDownOptionsDto> classList() {
        return Arrays
            .stream(StdEnum.values())
            .map(item -> DropDownOptionsDto.builder()
                .label(item.name())
                .value(item.name())
                .build())
            .collect(Collectors.toList());
    }

    @GetMapping("/currentSession")
    public SessionDTO fetchCurrentSession() {
        return sessionService.fetchCurrentSession();
    }

    @GetMapping("/currentFinancialYear")
    public DropDownOptionsDto fetchCurrentFinancialYear() {
        return financialYearService.fetchCurrentSession();
    }

    @GetMapping("/financialYearList")
    public List<DropDownOptionsDto> fetchAllFinancialYear() {
        return financialYearService.fetchAllFinancialYear();
    }

    @GetMapping("/availablePaymentOptions")
    public List<DropDownOptionsDto> fetchPaymentOptions() {
        return Arrays
            .stream(PaymentOptionEnum.values())
            .map(item -> DropDownOptionsDto.builder()
                .label(item.name())
                .value(item.name())
                .build())
            .collect(Collectors.toList());
    }

    @RequestMapping("/expenseCategory")
    public List<DropDownOptionsDto> expenseCategoryList() {
        return Arrays
            .stream(ExpenseTypeEnum.values())
            .map(item -> DropDownOptionsDto.builder()
                .label(item.name())
                .value(item.name())
                .build())
            .collect(Collectors.toList());
    }

    @RequestMapping("/uploadOptions")
    public List<DropDownOptionsDto> uploadOptions() {
        return Arrays
            .stream(UploadTypeEnum.values())
            .map(item -> DropDownOptionsDto.builder()
                .label(item.name())
                .value(item.name())
                .build())
            .collect(Collectors.toList());
    }

    @RequestMapping("/systemDocumentType")
    public List<DropDownOptionsDto> systemDocumentOptions() {
        return Arrays
            .stream(DocumentCategoryEnum.values())
            .map(item -> DropDownOptionsDto.builder()
                .value(item.name())
                .label(item.getCategory())
                .build())
            .collect(Collectors.toList());
    }
}
