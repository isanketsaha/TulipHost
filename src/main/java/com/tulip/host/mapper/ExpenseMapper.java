package com.tulip.host.mapper;

import com.tulip.host.data.ExpenseItemDTO;
import com.tulip.host.data.StudentDetailsDTO;
import com.tulip.host.domain.Expense;
import com.tulip.host.domain.Student;
import com.tulip.host.web.rest.vm.ExpenseItemVM;
import java.util.List;
import java.util.Set;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ExpenseMapper {
    @Mapping(expression = "java(source.getItemName().toUpperCase())", target = "itemName")
    Expense toModel(ExpenseItemVM source);

    Set<Expense> toModelList(List<ExpenseItemVM> expenseItem);

    ExpenseItemDTO toEntity(Expense expense);

    List<ExpenseItemDTO> toEntityList(Set<Expense> expenseItem);
}
