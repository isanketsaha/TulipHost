package com.tulip.host.utils;

import com.tulip.host.domain.ClassDetail;
import java.util.Comparator;

public class ClassComparatorBySession implements Comparator<ClassDetail> {

    @Override
    public int compare(ClassDetail o1, ClassDetail o2) {
        return o2.getSession().getFromDate().compareTo(o1.getSession().getFromDate());
    }
}
