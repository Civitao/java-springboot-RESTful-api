package com.devvitormarques.todolist.utils;

import java.beans.PropertyDescriptor;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;

import org.springframework.beans.BeanWrapperImpl;

public class Utils {

  public static void copyNonNullProperties(Object source, Object target) {
    BeanUtils.copyProperties(source, target, getNullPropertyFields(source));
  }
  
public static String[] getNullPropertyFields(Object source) {
  final BeanWrapper src = new BeanWrapperImpl(source);
  PropertyDescriptor[] pds = src.getPropertyDescriptors(); 

  Set<String> emptyNames = new HashSet<>();

  for(PropertyDescriptor pd : pds ) {
    Object srcValue = src.getPropertyValue(pd.getName());
    if(srcValue == null) {
      emptyNames.add(pd.getName());
    }
  }

  String[] result = new String[emptyNames.size()];
  System.out.println(Arrays.toString(result));
  return emptyNames.toArray(result);
}

}
