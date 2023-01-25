package com.tulip.host.web.rest;

import com.tulip.host.data.DropDownOptionsDto;
import com.tulip.host.data.SessionDTO;
import com.tulip.host.enums.*;
import com.tulip.host.service.FinancialYearService;
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

    @RequestMapping("/genderList")
    public List<DropDownOptionsDto> genderList() {
        return Arrays
            .stream(GenderEnum.values())
            .map(item -> DropDownOptionsDto.builder().label(item.getDisplayType()).value(item.name()).build())
            .collect(Collectors.toList());
    }

    @RequestMapping("/bloodGroupList")
    public List<DropDownOptionsDto> bloodGroupList() {
        return Arrays
            .stream(BloodGroupEnum.values())
            .map(item -> DropDownOptionsDto.builder().label(item.getDisplayType()).value(item.name()).build())
            .collect(Collectors.toList());
    }

    @RequestMapping("/relationList")
    public List<DropDownOptionsDto> dependentRelationList() {
        return Arrays
            .stream(RelationEnum.values())
            .map(item -> DropDownOptionsDto.builder().label(item.name()).value(item.name()).build())
            .collect(Collectors.toList());
    }

    @RequestMapping("/religionList")
    public List<DropDownOptionsDto> religionList() {
        return Arrays
            .stream(ReligionEnum.values())
            .map(item -> DropDownOptionsDto.builder().label(item.name()).value(item.name()).build())
            .collect(Collectors.toList());
    }

    @RequestMapping("/userRoleList")
    public List<DropDownOptionsDto> userRoleList() {
        return Arrays
            .stream(UserRoleEnum.values())
            .map(item -> DropDownOptionsDto.builder().label(item.name()).value(item.name()).build())
            .collect(Collectors.toList());
    }

    @RequestMapping("/classList")
    public List<DropDownOptionsDto> classList() {
        return Arrays
            .stream(StdEnum.values())
            .map(item -> DropDownOptionsDto.builder().label(item.name()).value(item.name()).build())
            .collect(Collectors.toList());
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
            .map(item -> DropDownOptionsDto.builder().label(item.name()).value(item.name()).build())
            .collect(Collectors.toList());
    }

    @RequestMapping("/expenseCategory")
    public List<DropDownOptionsDto> expenseCategoryList() {
        return Arrays
            .stream(ExpenseTypeEnum.values())
            .map(item -> DropDownOptionsDto.builder().label(item.name()).value(item.name()).build())
            .collect(Collectors.toList());
    }
}
